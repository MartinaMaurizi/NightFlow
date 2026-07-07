package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.bean.*;
import it.ispwproject.nightflow.controller.applicativo.ClientManagementController;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.pattern.singleton.SessionManager; // 🌟 Aggiunto import
import it.ispwproject.nightflow.view.gui.ManageParticipantsGUIView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.List;

public class ManageParticipantsGUI {

    private final Stage stage;
    private final ClientManagementController clientManagementController = new ClientManagementController();
    private final ManageParticipantsGUIView view = new ManageParticipantsGUIView();

    public ManageParticipantsGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        // 🌟 Assegniamo le azioni a Indietro, Logout, Profilo e Home
        BorderPane root = view.buildRoot(
                () -> new DashboardOrganizerGUI(stage).show(), // Azione Tasto Indietro
                () -> {                                        // Azione Logout
                    SessionManager.getInstance().setLoggedUser(null);
                    MainGUI.showLogin();
                },
                () -> new ProfileGUI(stage).show(),            // Azione Profilo
                () -> new DashboardOrganizerGUI(stage).show()  // Azione Home
        );

        view.clearList();

        try {
            List<ClientBean> allClients = clientManagementController.getClients();
            boolean hasParticipants = false;

            for (ClientBean client : allClients) {
                List<BookingResponseBean> bookings = clientManagementController.getClientBookings(client.getId());

                if (bookings != null && !bookings.isEmpty()) {
                    hasParticipants = true;
                    // Chiamiamo la View senza passargli l'azione del Check-in (rimossa)
                    view.addParticipantCard(client, bookings);
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