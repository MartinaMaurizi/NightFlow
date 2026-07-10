package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.bean.BookingResponseBean;
import it.ispwproject.nightflow.controller.applicativo.BookingController;
import it.ispwproject.nightflow.enumerator.BookingStatus;
import it.ispwproject.nightflow.enumerator.PaymentMethod; // 🌟 Aggiunto import
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.view.gui.ViewBookingsGUIView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.Clock;

public class ViewBookingsGUI {

    private final Stage stage;
    private final BookingController bookingController = new BookingController();
    private final ViewBookingsGUIView view = new ViewBookingsGUIView();

    public ViewBookingsGUI(Stage stage) { this.stage = stage; }

    public void show() {
        int clientId = SessionManager.getInstance().getLoggedUser().getId();

        // Assicurati che MainGUI abbia il metodo showDashboardClient()
        BorderPane root = view.buildRoot(MainGUI::showDashboardClient);
        view.clearError();

        try {
            // Usiamo i metodi allineati con il tuo BookingController
            List<BookingResponseBean> allBookings = bookingController.getAllClientBookings(clientId);
            List<BookingResponseBean> past        = bookingController.getClientPastBookings(clientId);

            // Filtro e ordinamento semplificati grazie a LocalDateTime
            List<BookingResponseBean> confirmed = allBookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                    .filter(b -> b.getEvent().getDateTime().isAfter(LocalDateTime.now(Clock.systemDefaultZone())))
                    .sorted((a, b) -> a.getEvent().getDateTime().compareTo(b.getEvent().getDateTime()))
                    .toList();
// 🌟 Ora chiediamo direttamente le prenotazioni cancellate al Controller!
            List<BookingResponseBean> cancelled = bookingController.getCancelledBookings();

            // 🌟 PASSIAMO ENTRAMBI I METODI (ANNULLA E MODIFICA)
            view.buildContent(root, confirmed, cancelled, past, this::confirmCancel, this::handleEditBooking);

        } catch (DAOException e) {
            view.setError("Errore: " + e.getMessage());
            root.setCenter(view.errorLabel);
        }

        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    private void confirmCancel(BookingResponseBean b) {
        // Recuperiamo l'ID direttamente qui dentro!
        int clientId = SessionManager.getInstance().getLoggedUser().getId();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma annullamento");
        alert.setHeaderText(null);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        alert.setContentText("Vuoi annullare la prenotazione?\n\n" +
                b.getEvent().getName() + " — " +
                b.getEvent().getDateTime().format(fmt));

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    bookingController.cancelBooking(b.getId(), clientId);
                    show();
                } catch (DAOException e) {
                    view.setError("Errore: " + e.getMessage());
                }
            }
        });
    }

    // GESTIONE DEL CLICK SU MODIFICA PRENOTAZIONE
    private void handleEditBooking(BookingResponseBean b) {

        new CheckoutGUI(stage, b.getEvent(), b.getTicketType(), true).show();
    }

}