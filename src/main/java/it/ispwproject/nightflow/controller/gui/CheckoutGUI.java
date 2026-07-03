package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.bean.BookingRequestBean;
import it.ispwproject.nightflow.bean.BookingResponseBean;
import it.ispwproject.nightflow.controller.applicativo.BookingController;
import it.ispwproject.nightflow.pattern.payment.Subject;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.service.NotificationService;
import it.ispwproject.nightflow.exception.NotificationException;
import it.ispwproject.nightflow.util.logger.AppLogger; // 🌟 Usa il tuo logger!
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
        if (view.selectedPaymentMethod == null) {
            showAlert(Alert.AlertType.WARNING, "Metodo di Pagamento Mancante", "Scegli un metodo di pagamento.");
            return;
        }

        String userEmail = view.emailFld.getText();
        AppLogger.logInfo("Elaborazione transazione: " + view.selectedPaymentMethod.name());

        try {
            BookingController appController = new BookingController();
            BookingRequestBean requestBean = new BookingRequestBean();
            requestBean.setEvent(event);
            requestBean.setTicketType(ticketDetails);

            BookingResponseBean bookingBean = appController.createBooking(requestBean, view.selectedPaymentMethod);

            // 🌟 CORREZIONE: Usiamo la Lambda al posto della classe anonima per l'Observer
            Subject subject = new Subject() {};
            subject.registerObserver(() -> {
                try {
                    NotificationService.sendBookingConfirmation(userEmail, bookingBean);
                    showAlert(Alert.AlertType.INFORMATION, "Prenotazione Confermata", "Biglietto inviato a " + userEmail);
                } catch (NotificationException ex) {
                    AppLogger.logError("Errore Invio Mail: " + ex.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Errore di Rete", "Prenotazione salvata, email non inviata.");
                }
            });

            subject.notifyObservers();
            new DashboardClientGUI(stage).show();

        } catch (Exception dbEx) {
            AppLogger.logError("Errore salvataggio: " + dbEx.getMessage());
            showAlert(Alert.AlertType.ERROR, "Errore di Sistema", "Impossibile salvare la prenotazione.");
        }
    }

    // 🌟 UTILITY PER EVITARE DUPLICAZIONE DI CODICE (DRY principle)
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}