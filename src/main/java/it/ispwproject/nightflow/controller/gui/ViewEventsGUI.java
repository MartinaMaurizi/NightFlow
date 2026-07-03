package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.controller.applicativo.EventController;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.view.gui.ViewEventsGUIView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ViewEventsGUI {

    private final Stage stage;
    private final EventController eventController = new EventController();
    private final ViewEventsGUIView view = new ViewEventsGUIView();

    public ViewEventsGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        int organizerId = SessionManager.getInstance().getLoggedUser().getId();

        BorderPane root = view.buildRoot(MainGUI::showDashboardOrganizer);
        view.clearError();

        try {
            // Assicurati che questo metodo nel tuo EventController restituisca List<EventBean>
            // (e non i Model crudi, proprio come abbiamo fatto per i Booking!)
            List<EventBean> allEvents = eventController.getOrganizerEvents();

            // Filtra e ordina gli eventi futuri
            List<EventBean> upcoming = allEvents.stream()
                    .filter(e -> e.getDateTime().isAfter(LocalDateTime.now()))
                    .sorted((a, b) -> a.getDateTime().compareTo(b.getDateTime()))
                    .toList();

            // Filtra e ordina gli eventi passati (storico)
            List<EventBean> past = allEvents.stream()
                    .filter(e -> e.getDateTime().isBefore(LocalDateTime.now()) || e.getDateTime().isEqual(LocalDateTime.now()))
                    .sorted((a, b) -> b.getDateTime().compareTo(a.getDateTime())) // Ordine decrescente per i passati
                    .toList();

            // Usiamo la lambda per catturare l'id dell'organizer senza passarlo alla View
            view.buildContent(root, upcoming, past, eventBean -> confirmDelete(eventBean, organizerId));

        } catch (DAOException e) {
            view.setError("Errore durante il caricamento: " + e.getMessage());
            root.setCenter(view.errorLabel);
        }

        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    private void confirmDelete(EventBean eventBean, int organizerId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione");
        alert.setHeaderText(null);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        alert.setContentText("Attenzione: vuoi davvero eliminare questo evento?\n\n" +
                eventBean.getName() + " — " +
                eventBean.getDateTime().format(fmt) +
                "\n\nTutte le prenotazioni associate verranno annullate.");

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    eventController.deleteEvent(eventBean.getId());
                    show(); // Ricarica la schermata per aggiornare la lista
                } catch (DAOException e) {
                    view.setError("Errore durante l'eliminazione: " + e.getMessage());
                }
            }
        });
    }
}