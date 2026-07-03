package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.bean.*;
import it.ispwproject.nightflow.controller.applicativo.ClientManagementController;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.view.gui.ManageParticipantsGUIView;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ManageParticipantsGUI {

    private final Stage stage;
    private final ClientManagementController clientManagementController = new ClientManagementController();
    private final ManageParticipantsGUIView view = new ManageParticipantsGUIView();

    public ManageParticipantsGUI(Stage stage) { this.stage = stage; }

    public void show() {
        BorderPane root = view.buildRoot(MainGUI::showDashboardOrganizer);

        try {
            // Assicurati che nel tuo controller applicativo il metodo si chiami getClients()
            view.participantCombo.getItems().setAll(clientManagementController.getClients());
        } catch (DAOException e) {
            view.errorLabel.setText("Errore: " + e.getMessage());
        }

        view.participantCombo.setOnAction(e -> {
            ClientBean selected = view.participantCombo.getValue();
            if (selected == null) return;
            loadParticipantCard(selected);
        });

        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    // ─── Caricamento card partecipante ──────────────────────────────────────
    private void loadParticipantCard(ClientBean client) {
        VBox card = view.getParticipantCard();
        try {
            // Recuperiamo le prenotazioni del cliente per gli eventi gestiti dall'organizzatore
            List<BookingResponseBean> bookings = clientManagementController.getClientBookings(client.getId());

            view.buildParticipantCard(card, client, bookings,
                    booking -> handleCheckIn(booking, client, card));

        } catch (DAOException e) {
            view.errorLabel.setText("Errore: " + e.getMessage());
        }
    }

    // ─── Azioni ─────────────────────────────────────────────────────────────
    private void handleCheckIn(BookingResponseBean booking, ClientBean client, VBox card) {
        try {
            // Chiamata al controller applicativo per segnare l'ingresso
            clientManagementController.performCheckIn(booking.getId());
            showInfo("Check-in effettuato con successo!");

            // Ricarichiamo la card per aggiornare lo stato
            loadParticipantCard(client);
        } catch (DAOException e) {
            view.errorLabel.setText("Errore durante il check-in: " + e.getMessage());
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Operazione completata");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}