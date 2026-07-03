package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.bean.BookingRequestBean;
import it.ispwproject.nightflow.bean.BookingResponseBean;
import it.ispwproject.nightflow.controller.applicativo.BookingController;
import it.ispwproject.nightflow.pattern.payment.Observer;
import it.ispwproject.nightflow.pattern.payment.Subject;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.service.NotificationService;
import it.ispwproject.nightflow.exception.NotificationException;
import it.ispwproject.nightflow.view.gui.CheckoutGUIView;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CheckoutGUI {
    private final Stage stage;
    private final CheckoutGUIView view = new CheckoutGUIView();

    private final EventBean event;
    private final String ticketDetails;

    public CheckoutGUI(Stage stage, EventBean event, String ticketDetails) {
        this.stage = stage;
        this.event = event;
        this.ticketDetails = ticketDetails;
    }

    public void show() {
        String dateFormatted = event.getDateTime().format(DateTimeFormatter.ofPattern("dd MMM", Locale.ITALIAN)).toUpperCase();
        String imagePath = "/locali/" + event.getLocalName().toLowerCase().replace(" ", "").replace("ò", "o") + ".png";

        Scene scene = new Scene(view.buildRoot(
                () -> new BookTicketGUI(stage, event).show(),
                () -> {
                    SessionManager.getInstance().setLoggedUser(null);
                    MainGUI.showLogin();
                },
                this::processCheckout, // 🌟 TRUCCO MAGICO: Usiamo il Method Reference per puntare al metodo qui sotto!
                event.getLocalName().toUpperCase() + " - " + event.getName(),
                dateFormatted,
                ticketDetails,
                imagePath
        ), MainGUI.WINDOW_WIDTH, MainGUI.WINDOW_HEIGHT);

        // Collegamento tasti Navbar superiori
        if (view.profileBtn != null) view.profileBtn.setOnAction(e -> new ProfileGUI(stage).show());
        if (view.homeBtn != null) view.homeBtn.setOnAction(e -> new DashboardClientGUI(stage).show());

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/nightflow.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Foglio di stile non trovato, caricamento grafica base.");
        }

        stage.setScene(scene);
        stage.show();
    }

    // =================================================================
    // 🌟 METODO ESTRATTO: Indentazione perfetta e leggibilità massima
    // =================================================================
    private void processCheckout() {
        // 1. CONTROLLO METODO DI PAGAMENTO
        if (view.selectedPaymentMethod == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Metodo di Pagamento Mancante");
            alert.setHeaderText("Non hai selezionato come pagare!");
            alert.setContentText("Per favore, scegli un metodo di pagamento prima di confermare la prenotazione.");
            alert.showAndWait();
            return; // Blocca l'esecuzione se manca il pagamento
        }

        // 2. RECUPERO EMAIL UTENTE DALLA VIEW
        String userEmail = view.emailFld.getText();

        System.out.println("Elaborazione transazione simulata con il metodo: " + view.selectedPaymentMethod.name());

        // 3. SALVATAGGIO NEL DB TRAMITE IL CONTROLLER APPLICATIVO
        try {
            BookingController appController = new BookingController();

            BookingRequestBean requestBean = new BookingRequestBean();
            requestBean.setEvent(event);
            requestBean.setTicketType(ticketDetails);

            BookingResponseBean bookingBean = appController.createBooking(requestBean, view.selectedPaymentMethod);

            // =================================================================
            // 🌟 INIZIO PATTERN: OBSERVER 🌟
            // =================================================================
            Subject subject = new Subject() {};

            subject.registerObserver(new Observer() {
                @Override
                public void update() {
                    try {
                        System.out.println("Observer attivato! Invio email a: " + userEmail);
                        NotificationService.sendBookingConfirmation(userEmail, bookingBean);

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Prenotazione Confermata");
                        alert.setHeaderText("Tutto pronto per fare serata! 🪩");
                        alert.setContentText("Il biglietto " + bookingBean.getTicketCode() + " è stato inviato a " + userEmail + "\nMetodo scelto: " + view.selectedPaymentMethod.name());
                        alert.showAndWait();

                    } catch (NotificationException ex) {
                        System.err.println("Errore Invio Mail: " + ex.getMessage());

                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Errore di Rete");
                        errorAlert.setHeaderText("Ops, qualcosa è andato storto!");
                        errorAlert.setContentText("La prenotazione è salvata, ma non abbiamo inviato l'email di conferma.\n\nDettaglio: " + ex.getMessage());
                        errorAlert.showAndWait();
                    }
                }
            });

            // Avvisiamo tutti (scatta l'email con i dati reali)
            subject.notifyObservers();

            // 4. Riportiamo l'utente alla Home
            new DashboardClientGUI(stage).show();

            // =================================================================
            // 🌟 FINE PATTERN 🌟
            // =================================================================

        } catch (Exception dbEx) {
            Alert dbAlert = new Alert(Alert.AlertType.ERROR);
            dbAlert.setTitle("Errore di Sistema");
            dbAlert.setHeaderText("Impossibile salvare la prenotazione");
            dbAlert.setContentText("C'è stato un problema durante il salvataggio.\n" + dbEx.getMessage());
            dbAlert.showAndWait();
        }
    }
}