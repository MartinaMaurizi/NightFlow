package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.model.User;
import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.bean.BookingRequestBean;
import it.ispwproject.nightflow.bean.BookingResponseBean;
import it.ispwproject.nightflow.controller.applicativo.BookingController;
import it.ispwproject.nightflow.enumerator.PaymentMethod;
import it.ispwproject.nightflow.pattern.observer.Observable;
import it.ispwproject.nightflow.bean.PaymentRequestBean;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.service.NotificationService;
import it.ispwproject.nightflow.exception.NotificationException;
import it.ispwproject.nightflow.service.PaymentService;
import it.ispwproject.nightflow.util.logger.AppLogger;
import it.ispwproject.nightflow.view.gui.CheckoutGUIView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

public class CheckoutGUI {
    private final Stage stage;
    private final CheckoutGUIView view = new CheckoutGUIView();
    private final EventBean event;
    private final String ticketDetails;
    private final boolean isModification;

    public CheckoutGUI(Stage stage, EventBean event, String ticketDetails) {
        this(stage, event, ticketDetails, false);
    }

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

        // 🌟 PREPARAZIONE DATI PER DISACCOPPIARE LA VIEW DAL MODELLO
        String userName = "";
        String userEmail = "";
        String userDob = "Data non inserita";

        if (loggedUser != null) {
            userName = loggedUser.getName();
            userEmail = loggedUser.getEmail();
            if (loggedUser instanceof it.ispwproject.nightflow.model.Client) {
                it.ispwproject.nightflow.model.Client clientUser = (it.ispwproject.nightflow.model.Client) loggedUser;
                if (clientUser.getDateOfBirth() != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    userDob = clientUser.getDateOfBirth().format(formatter);
                }
            }
        }

        // 🌟 PASSIAMO ALLA VIEW SOLO STRINGHE
        Scene scene = new Scene(view.buildRoot(
                userName,
                userEmail,
                userDob,
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
        if (isBlank(view.phoneFld.getText())) {
            showAlert(Alert.AlertType.WARNING, "Campi Obbligatori Mancanti", "Per favore, compila il campo obbligatorio: Telefono.");
            return;
        }

        if (view.selectedPaymentMethod == null) {
            showAlert(Alert.AlertType.WARNING, "Metodo di Pagamento Mancante", "Scegli un metodo di pagamento prima di confermare.");
            return;
        }

        if (view.selectedPaymentMethod == PaymentMethod.CREDIT_CARD && !validateCardFields()) {
            return;
        }

        String userEmail = view.emailFld.getText();

        if (view.selectedPaymentMethod == PaymentMethod.CREDIT_CARD || view.selectedPaymentMethod == PaymentMethod.PAYPAL) {
            double amountToPay = event.getPrice();
            PaymentRequestBean paymentRequest = new PaymentRequestBean(amountToPay, userEmail);

            boolean pagamentoRiuscito = mostraSimulazioneGateway(view.selectedPaymentMethod, paymentRequest);

            if (!pagamentoRiuscito) {
                AppLogger.logInfo("Pagamento annullato o tempo scaduto.");
                return;
            }

            // 🌟 CHIAMATA AL PAYMENT SERVICE INSERITA QUI 🌟
            PaymentService paymentService = new PaymentService();
            boolean addebitoCompletato = paymentService.processPayment(paymentRequest);

            // Controllo di sicurezza
            if (!addebitoCompletato) {
                showAlert(Alert.AlertType.ERROR, "Transazione Fallita", "La banca ha rifiutato l'addebito.");
                return;
            }
        }

        if (isModification) {
            try {
                BookingController appController = new BookingController();
                appController.updatePaymentMethod(event.getId(), view.selectedPaymentMethod);

                showAlert(Alert.AlertType.INFORMATION, "Pagamento Completato", "La tua prenotazione è stata aggiornata a Pagata!");
                new ViewBookingsGUI(stage).show();

            } catch (Exception ex) {
                AppLogger.logError("Errore aggiornamento: " + ex.getMessage());
                showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile aggiornare il pagamento.");
            }
            return;
        }

        AppLogger.logInfo("Elaborazione transazione confermata. Creazione biglietto...");

        try {
            BookingController appController = new BookingController();
            BookingRequestBean requestBean = new BookingRequestBean();
            requestBean.setEvent(event);
            requestBean.setTicketType(ticketDetails);

            BookingResponseBean bookingBean = appController.createBooking(requestBean, view.selectedPaymentMethod);

            Observable subject = new Observable() {};
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

    private boolean validateCardFields() {
        String cardName = view.cardNameFld.getText();
        String cardNum = view.cardNumFld.getText();
        String cardExp = view.cardExpFld.getText();
        String cardCvv = view.cardCvvFld.getText();

        if (isBlank(cardName) || isBlank(cardNum) || isBlank(cardExp) || isBlank(cardCvv)) {
            showAlert(Alert.AlertType.WARNING, "Dati Carta Mancanti",
                    "Per favore, compila tutti i campi della carta: Nome, Numero, Scadenza e CVV.");
            return false;
        }

        if (!cardName.trim().matches("[A-Za-zÀ-ÿ' ]{2,}")) {
            showAlert(Alert.AlertType.WARNING, "Nome Carta non valido",
                    "Inserisci il nome così come riportato sulla carta (solo lettere).");
            return false;
        }

        String cardNumDigits = cardNum.replaceAll("\\s+", "");
        if (!cardNumDigits.matches("\\d{13,19}")) {
            showAlert(Alert.AlertType.WARNING, "Numero Carta non valido",
                    "Inserisci un numero di carta valido (13-19 cifre).");
            return false;
        }

        if (!cardExp.trim().matches("(0[1-9]|1[0-2])/\\d{2}")) {
            showAlert(Alert.AlertType.WARNING, "Scadenza non valida",
                    "Inserisci la scadenza nel formato MM/AA.");
            return false;
        }
        if (isCardExpired(cardExp.trim())) {
            showAlert(Alert.AlertType.WARNING, "Carta Scaduta",
                    "La carta inserita risulta scaduta.");
            return false;
        }

        if (!cardCvv.trim().matches("\\d{3,4}")) {
            showAlert(Alert.AlertType.WARNING, "CVV non valido",
                    "Il CVV deve contenere 3 o 4 cifre.");
            return false;
        }

        return true;
    }

    private boolean isCardExpired(String monthYear) {
        String[] parts = monthYear.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = 2000 + Integer.parseInt(parts[1]);

        java.time.YearMonth expiry = java.time.YearMonth.of(year, month);
        java.time.YearMonth now = java.time.YearMonth.now();

        return expiry.isBefore(now);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean mostraSimulazioneGateway(PaymentMethod method, PaymentRequestBean request) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        String nomeGateway = method == PaymentMethod.PAYPAL ? "PayPal" : "Mastercard / Visa";
        alert.setTitle("Gateway Esterno: " + nomeGateway);
        alert.setHeaderText("Autorizzazione Transazione con " + nomeGateway);

        ButtonType btnPaga = new ButtonType("Paga Ora", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnPaga, btnAnnulla);

        final int[] secondsLeft = {120};
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        Runnable updateVisualCountdown = () -> {
            int minutes = secondsLeft[0] / 60;
            int seconds = secondsLeft[0] % 60;
            alert.setContentText(
                    "Stai per effettuare un pagamento sicuro.\n\n" +
                            "Dettagli Richiesta:\n" +
                            "• Account: " + request.getUserEmail() + "\n" +
                            "• Importo totale: € " + request.getAmount() + "\n\n" +
                            "⚠️ TEMPO MASSIMO PER CONFERMARE: " + String.format("%02d:%02d", minutes, seconds) + "\n\n" +
                            "Premi 'Paga Ora' per simulare l'addebito."
            );
        };

        updateVisualCountdown.run();

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), event -> {
            secondsLeft[0]--;
            updateVisualCountdown.run();

            if (secondsLeft[0] <= 0) {
                timeline.stop();
                alert.close();
            }
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();

        Optional<ButtonType> result = alert.showAndWait();
        timeline.stop();

        if (secondsLeft[0] <= 0) {
            showAlert(Alert.AlertType.ERROR, "Tempo Scaduto",
                    "Il tempo massimo di 2 minuti per confermare il pagamento è terminato.\nLa transazione è stata annullata.");

            new DashboardClientGUI(stage).show();
            return false;
        }

        return result.isPresent() && result.get() == btnPaga;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}