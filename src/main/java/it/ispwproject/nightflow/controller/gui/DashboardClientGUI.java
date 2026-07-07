package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.controller.applicativo.EventController; // 🌟 Aggiunto
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.view.gui.DashboardClientGUIView;
import it.ispwproject.nightflow.util.logger.AppLogger;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.List;

public class DashboardClientGUI {
    private final Stage stage;
    private final DashboardClientGUIView view = new DashboardClientGUIView();
    private final EventController eventController = new EventController(); // 🌟 Collegamento al "Database"

    public DashboardClientGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        Scene scene = new Scene(view.buildRoot(
                () -> {
                    SessionManager.getInstance().setLoggedUser(null);
                    MainGUI.showLogin();
                },
                () -> new ProfileGUI(stage).show()
        ), MainGUI.WINDOW_WIDTH, MainGUI.WINDOW_HEIGHT);

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/nightflow.css").toExternalForm());
        } catch (Exception e) {
            AppLogger.logWarning("CSS non trovato: " + e.getMessage());
        }

        // 🌟 1. CHIEDIAMO GLI EVENTI VERI! (Basta liste scritte a mano)
        try {
            List<EventBean> eventiVeri = eventController.getAllUpcomingEvents();
            view.updateEventList(eventiVeri, event -> new BookTicketGUI(stage, event).show());
        } catch (Exception e) {
            AppLogger.logError("Errore nel caricamento eventi per il cliente: " + e.getMessage());
        }

        // 🌟 2. GESTIONE RICERCA E CLICK
        view.searchField.setOnMouseClicked(e -> {
            e.consume();
            new SearchEventsGUI(stage).show();
        });

        // --- AZIONI DI NAVIGAZIONE GLOBALI ---
        if (view.profileBtn != null) view.profileBtn.setOnAction(e -> new ProfileGUI(stage).show());
        if (view.homeBtn != null) view.homeBtn.setOnAction(e -> this.show());

        stage.setScene(scene);
        stage.show();
    }
}