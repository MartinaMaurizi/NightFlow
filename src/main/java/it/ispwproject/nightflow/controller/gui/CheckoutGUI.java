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
        if (isBlank(view.phoneFld.getText())) {
            showAlert(Alert.AlertType.WARNING, "Campi Obbligatori Mancanti", "Per favore, compila il campo obbligatorio: Telefono.");
            return;
        }

        if (view.selectedPaymentMethod == null) {
            showAlert(Alert.AlertType.WARNING, "Metodo di Pagamento Mancante", "Scegli un metodo di pagamento prima di confermare.");
            return;
        }

        // 🌟 SE IL PAGAMENTO È CON CARTA, VALIDIAMO PRESENZA E FORMATO DEI DATI CARTA
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

    /**
     * 🌟 Valida presenza e formato dei campi della carta di debito/credito.
     * Mostra un alert e ritorna false al primo errore trovato.
     */
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

        // Nome sulla carta: solo lettere, spazi e apostrofi, almeno 2 caratteri
        if (!cardName.trim().matches("[A-Za-zÀ-ÿ' ]{2,}")) {
            showAlert(Alert.AlertType.WARNING, "Nome Carta non valido",
                    "Inserisci il nome così come riportato sulla carta (solo lettere).");
            return false;
        }

        // Numero carta: 13-19 cifre, con o senza spazi
        String cardNumDigits = cardNum.replaceAll("\\s+", "");
        if (!cardNumDigits.matches("\\d{13,19}")) {
            showAlert(Alert.AlertType.WARNING, "Numero Carta non valido",
                    "Inserisci un numero di carta valido (13-19 cifre).");
            return false;
        }

        // Scadenza formato MM/AA e non già scaduta
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

        // CVV: 3 o 4 cifre
        if (!cardCvv.trim().matches("\\d{3,4}")) {
            showAlert(Alert.AlertType.WARNING, "CVV non valido",
                    "Il CVV deve contenere 3 o 4 cifre.");
            return false;
        }

        return true;
    }

    /**
     * Controlla se una scadenza MM/AA è già passata rispetto al mese/anno corrente.
     */
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

        // 🌟 IMPOSTAZIONE DEL COUNTDOWN A 2 MINUTI (120 SECONDI)
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

        // Renderizza il testo iniziale subito
        updateVisualCountdown.run();

        // Configurazione dell'evento scatenato ad ogni secondo della Timeline
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), event -> {
            secondsLeft[0]--;
            updateVisualCountdown.run();

            if (secondsLeft[0] <= 0) {
                timeline.stop();
                alert.close(); // Chiude forzatamente il pop-up sbloccando showAndWait()
            }
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.play(); // Avvia il countdown

        Optional<ButtonType> result = alert.showAndWait();
        timeline.stop(); // 🌟 IMPORTANTE: Ferma il timer se l'utente risponde prima della scadenza

        // Se il tempo è scaduto, mostra l'alert di errore e reindirizza l'utente
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