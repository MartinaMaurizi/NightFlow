package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.bean.*;
import it.ispwproject.nightflow.controller.applicativo.ClientManagementController;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.util.logger.AppLogger;
import it.ispwproject.nightflow.view.gui.ManageParticipantsGUIView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;

public class ManageParticipantsGUI {

    private final Stage stage;
    private final ClientManagementController clientManagementController = new ClientManagementController();
    private final ManageParticipantsGUIView view = new ManageParticipantsGUIView();

    public ManageParticipantsGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        BorderPane root = view.buildRoot(
                () -> new DashboardOrganizerGUI(stage).show(),
                () -> {
                    AppLogger.logInfo("Esecuzione Logout Client: pulizia sessione...");
                    SessionManager.getInstance().clearSession(); // 🌟 Pulizia completa!
                    MainGUI.showLogin();
                },
                () -> new ProfileGUI(stage).show(),
                () -> new DashboardOrganizerGUI(stage).show()
        );

        view.clearList();

        try {
            List<ClientBean> allClients = clientManagementController.getClients();
            boolean hasParticipants = false;
            LocalDateTime now = LocalDateTime.now();

            for (ClientBean client : allClients) {
                List<BookingResponseBean> bookings = clientManagementController.getClientBookings(client.getId());

                if (bookings != null && !bookings.isEmpty()) {
                    hasParticipants = true;

                    // Filtriamo in attive e passate
                    List<BookingResponseBean> active = bookings.stream()
                            .filter(b -> b.getEvent().getDateTime().isAfter(now))
                            .toList();
                    List<BookingResponseBean> past = bookings.stream()
                            .filter(b -> b.getEvent().getDateTime().isBefore(now))
                            .toList();

                    view.addParticipantCard(client, active, past);
                }
            }

            if (!hasParticipants) {
                view.showEmptyMessage();
            }

        } catch (DAOException e) {
            view.errorLabel.setText("Errore di caricamento: " + e.getMessage());
        }

        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }
}