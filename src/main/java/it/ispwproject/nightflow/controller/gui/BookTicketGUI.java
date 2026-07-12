package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.util.logger.AppLogger;
import it.ispwproject.nightflow.view.gui.BookTicketGUIView;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class BookTicketGUI {
    private final Stage stage;
    private final BookTicketGUIView view = new BookTicketGUIView();
    private final EventBean event;

    public BookTicketGUI(Stage stage, EventBean event) {
        this.stage = stage;
        this.event = event;
    }

    public void show() {
        Scene scene = new Scene(view.buildRoot(
                () -> new DashboardClientGUI(stage).show(),
                () -> {
                    SessionManager.getInstance().setLoggedUser(null);
                    MainGUI.showLogin();
                },

                // IL CONSUMER PRENDE DUE VALORI: TICKETINFO E FINALPRICE
                (ticketInfo, finalPrice) -> {
                    if (event.getAvailableTickets() < 1) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Prenotazione non disponibile");
                        alert.setHeaderText("Disponibilità Esaurita");
                        alert.setContentText("Event SOLD OUT! Tutti i biglietti per questo evento sono stati venduti.");
                        alert.showAndWait();

                        AppLogger.logWarning("Tentativo di acquisto bloccato: Evento SOLD OUT (" + event.getName() + ")");
                        return;
                    }

                    // FORZIAMO L'EVENTBEAN AD AVERE IL PREZZO DEL VIP!
                    // Aggiorniamo temporaneamente l'EventBean con il prezzo maggiorato
                    // prima di passarlo alla CheckoutGUI, così la ricevuta sarà perfetta.
                    event.setPrice(finalPrice);

                    new CheckoutGUI(stage, event, ticketInfo).show();
                },

                event
        ), MainGUI.WINDOW_WIDTH, MainGUI.WINDOW_HEIGHT);

        if (view.profileBtn != null) view.profileBtn.setOnAction(e -> new ProfileGUI(stage).show());
        if (view.homeBtn != null) view.homeBtn.setOnAction(e -> new DashboardClientGUI(stage).show());

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/nightflow.css").toExternalForm());
        } catch (Exception e) {
            AppLogger.logWarning("Impossibile caricare il CSS: " + e.getMessage());
        }

        stage.setScene(scene);
        stage.show();
    }
}