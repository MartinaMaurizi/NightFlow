package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.model.User;
import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.bean.BookingRequestBean;
import it.ispwproject.nightflow.bean.BookingResponseBean;
import it.ispwproject.nightflow.controller.applicativo.BookingController;
import it.ispwproject.nightflow.enumerator.PaymentMethod;
import it.ispwproject.nightflow.pattern.payment.PaymentRequest;
import it.ispwproject.nightflow.pattern.payment.Subject;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.service.NotificationService;
import it.ispwproject.nightflow.exception.NotificationException;
import it.ispwproject.nightflow.util.logger.AppLogger;
import it.ispwproject.nightflow.view.gui.CheckoutGUIView;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

public class CheckoutGUI {
    private final Stage stage;
    private final CheckoutGUIView view = new CheckoutGUIView();
    private final EventBean event;
    private final String ticketDetails;

    // 🌟 NUOVA VARIABILE PER CAPIRE SE È UNA MODIFICA
    private final boolean isModification;

    // Costruttore originale (usato per le nuove prenotazioni)
    public CheckoutGUI(Stage stage, EventBean event, String ticketDetails) {
        this(stage, event, ticketDetails, false); // Di default non è una modifica
    }

    // Nuovo costruttore (usato quando modifichiamo una prenotazione esistente)
    public CheckoutGUI(Stage stage, EventBean event, String ticketDetails, boolean isModification) {
        this.stage = stage;
        this.event = event;
        this.ticketDetails = ticketDetails;
        this.isModification = isModification;
    }

    public void show() {
        String dateFormatted = event.getDateTime().format(DateTimeFormatter.ofPattern("dd MMM", Locale.ITALIAN)).toUpperCase();
        String imagePath = "/locali/" + event.getLocalName().toLowerCase().replace(" ", "").replace("ò", "o") + ".png";

        User loggedUser = SessionManager.getInstance().getLoggedUser();

        Scene scene = new Scene(view.buildRoot(
                loggedUser,
                () -> {
                    if (isModification) new ViewBookingsGUI(stage).show();
                    else new BookTicketGUI(stage, event).show();
                },
                () -> {
                    SessionManager.getInstance().setLoggedUser(null);
                    MainGUI.showLogin();
                },
                this::processCheckout,
                event.getLocalName().toUpperCase() + " - " + event.getName(),
                dateFormatted,
                ticketDetails,
                imagePath
        ), MainGUI.WINDOW_WIDTH, MainGUI.WINDOW_HEIGHT);

        if (view.profileBtn != null) view.profileBtn.setOnAction(e -> new ProfileGUI(stage).show());
        if (view.homeBtn != null) view.homeBtn.setOnAction(e -> new DashboardClientGUI(stage).show());

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/nightflow.css").toExternalForm());
        } catch (Exception e) {
            AppLogger.logWarning("CSS non caricato: " + e.getMessage());
        }

        stage.setScene(scene);
        stage.show();
    }

    private void processCheckout() {
        if (view.phoneFld.getText() == null || view.phoneFld.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campi Obbligatori Mancanti", "Per favore, compila il campo obbligatorio: Telefono.");
            return;
        }

        if (view.selectedPaymentMethod == null) {
            showAlert(Alert.AlertType.WARNING, "Metodo di Pagamento Mancante", "Scegli un metodo di pagamento prima di confermare.");
            return;
        }

        String userEmail = view.emailFld.getText();

        if (view.selectedPaymentMethod == PaymentMethod.CREDIT_CARD || view.selectedPaymentMethod == PaymentMethod.PAYPAL) {
            double amountToPay = event.getPrice();
            PaymentRequest paymentRequest = new PaymentRequest(amountToPay, userEmail);

            boolean pagamentoRiuscito = mostraSimulazioneGateway(view.selectedPaymentMethod, paymentRequest);

            if (!pagamentoRiuscito) {
                AppLogger.logInfo("Pagamento annullato dall'utente.");
                return;
            }
        }

        // 🌟 SE È UNA MODIFICA, AGGIORNIAMO SOLO IL PAGAMENTO E FERMIAMO QUI IL CODICE!
        if (isModification) {
            try {
                BookingController appController = new BookingController();
                appController.updatePaymentMethod(event.getId(), view.selectedPaymentMethod);

                showAlert(Alert.AlertType.INFORMATION, "Pagamento Completato", "La tua prenotazione è stata aggiornata a Pagata!");
                new ViewBookingsGUI(stage).show(); // Torna alla pagina delle prenotazioni

            } catch (Exception ex) {
                AppLogger.logError("Errore aggiornamento: " + ex.getMessage());
                showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile aggiornare il pagamento.");
            }
            return; // ⛔ IMPORTANTISSIMO: il return ferma tutto ed evita di creare un biglietto nuovo!
        }

        // Se INVECE non è una modifica, si prosegue col creare il nuovo biglietto...
        AppLogger.logInfo("Elaborazione transazione confermata. Creazione biglietto...");

        try {
            BookingController appController = new BookingController();
            BookingRequestBean requestBean = new BookingRequestBean();
            requestBean.setEvent(event);
            requestBean.setTicketType(ticketDetails);

            BookingResponseBean bookingBean = appController.createBooking(requestBean, view.selectedPaymentMethod);

            Subject subject = new Subject() {};
            subject.registerObserver(() -> {
                try {
                    NotificationService.sendBookingConfirmation(userEmail, bookingBean);
                    showAlert(Alert.AlertType.INFORMATION, "Prenotazione Confermata", "Biglietto inviato a " + userEmail);
                } catch (NotificationException ex) {
                    AppLogger.logError("Errore Invio Mail: " + ex.getMessage());
                    showAlert(Alert.AlertType.WARNING, "Errore di Rete", "Prenotazione salvata, email non inviata.");
                }
            });

            subject.notifyObservers();

            new DashboardClientGUI(stage).show();

        } catch (Exception dbEx) {
            AppLogger.logError("Errore salvataggio: " + dbEx.getMessage());
            showAlert(Alert.AlertType.ERROR, "Errore di Sistema", dbEx.getMessage());
        }
    }

    private boolean mostraSimulazioneGateway(PaymentMethod method, PaymentRequest request) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        String nomeGateway = method == PaymentMethod.PAYPAL ? "PayPal" : "Mastercard / Visa";
        alert.setTitle("Gateway Esterno: " + nomeGateway);
        alert.setHeaderText("Autorizzazione Transazione con " + nomeGateway);

        alert.setContentText(
                "Stai per effettuare un pagamento sicuro.\n\n" +
                        "Dettagli Richiesta:\n" +
                        "• Account: " + request.getUserEmail() + "\n" +
                        "• Importo totale: € " + request.getAmount() + "\n\n" +
                        "Premi 'Paga Ora' per simulare l'addebito."
        );

        ButtonType btnPaga = new ButtonType("Paga Ora", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnPaga, btnAnnulla);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == btnPaga;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}