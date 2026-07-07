package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.controller.applicativo.EventController;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.view.gui.ViewEventsGUIView;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.Clock;
import java.util.Optional;

public class ViewEventsGUI {

    private final Stage stage;
    private final EventController eventController = new EventController();
    private final ViewEventsGUIView view = new ViewEventsGUIView();

    public ViewEventsGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        BorderPane root = view.buildRoot(
                () -> new DashboardOrganizerGUI(stage).show(), // 1. Azione Indietro
                () -> {                                        // 2. Azione Logout
                    it.ispwproject.nightflow.pattern.singleton.SessionManager.getInstance().setLoggedUser(null);
                    MainGUI.showLogin();
                },
                () -> new ProfileGUI(stage).show(),            // 3. Azione Profilo
                () -> new DashboardOrganizerGUI(stage).show()  // 4. Azione Home
        );
        view.clearError();

        try {
            List<EventBean> allEvents = eventController.getOrganizerEvents();
            LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());

            List<EventBean> upcoming = allEvents.stream()
                    .filter(e -> e.getDateTime().isAfter(now))
                    .sorted((a, b) -> a.getDateTime().compareTo(b.getDateTime()))
                    .toList();

            List<EventBean> past = allEvents.stream()
                    .filter(e -> !e.getDateTime().isAfter(now))
                    .sorted((a, b) -> b.getDateTime().compareTo(a.getDateTime()))
                    .toList();

            // 🌟 AGGIORNAMENTO: Passiamo il riferimento a changeDate e confirmDelete
            view.buildContent(root, upcoming, past, this::changeDate, this::confirmDelete);

        } catch (DAOException e) {
            view.setError("Errore durante il caricamento: " + e.getMessage());
            root.setCenter(view.errorLabel);
        }

        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    // 🌟 NUOVO METODO: Gestisce il popup del calendario
    private void changeDate(EventBean eventBean) {
        Dialog<LocalDateTime> dialog = new Dialog<>();
        dialog.setTitle("Modifica Data Evento");
        dialog.setHeaderText("Seleziona la nuova data per: " + eventBean.getName());

        DatePicker datePicker = new DatePicker(eventBean.getDateTime().toLocalDate());
        TextField timeField = new TextField(eventBean.getDateTime().format(DateTimeFormatter.ofPattern("HH:mm")));

        VBox content = new VBox(10, new Label("Data:"), datePicker, new Label("Orario (HH:mm):"), timeField);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    return datePicker.getValue().atTime(LocalTime.parse(timeField.getText()));
                } catch (Exception e) { return null; }
            }
            return null;
        });

        Optional<LocalDateTime> result = dialog.showAndWait();
        result.ifPresent(newDate -> {
            try {
                eventBean.setDateTime(newDate);
                // Assicurati che EventController abbia un metodo updateEventDate(EventBean)
                eventController.updateEventDate(eventBean);
                show();
            } catch (DAOException e) {
                view.setError("Errore durante l'aggiornamento: " + e.getMessage());
            }
        });
    }

    private void confirmDelete(EventBean eventBean) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione");
        alert.setHeaderText(null);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        alert.setContentText("Attenzione: vuoi davvero eliminare questo evento?\n\n" +
                eventBean.getName() + " — " +
                eventBean.getDateTime().format(fmt));

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