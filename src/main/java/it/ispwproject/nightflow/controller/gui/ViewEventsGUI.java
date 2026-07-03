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
import java.time.Clock;

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
            List<EventBean> allEvents = eventController.getOrganizerEvents();

            // 🌟 Correzione Timezone (Clock.systemDefaultZone())
            LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());

            List<EventBean> upcoming = allEvents.stream()
                    .filter(e -> e.getDateTime().isAfter(now))
                    .sorted((a, b) -> a.getDateTime().compareTo(b.getDateTime()))
                    .toList();

            List<EventBean> past = allEvents.stream()
                    .filter(e -> e.getDateTime().isBefore(now) || e.getDateTime().isEqual(now))
                    .sorted((a, b) -> b.getDateTime().compareTo(a.getDateTime()))
                    .toList();

            // 🌟 Rimosso 'organizerId' dalla lambda perché non serve nell'eliminazione
            view.buildContent(root, upcoming, past, eventBean -> confirmDelete(eventBean));

        } catch (DAOException e) {
            view.setError("Errore durante il caricamento: " + e.getMessage());
            root.setCenter(view.errorLabel);
        }

        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    // 🌟 Rimosso 'organizerId' inutilizzato dalla firma del metodo
    private void confirmDelete(EventBean eventBean) {
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
                    show();
                } catch (DAOException e) {
                    view.setError("Errore durante l'eliminazione: " + e.getMessage());
                }
            }
        });
    }

}