package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.view.gui.DashboardOrganizerGUIView;
import it.ispwproject.nightflow.util.logger.AppLogger; // 🌟 Import logger
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DashboardOrganizerGUI {
    private final Stage stage;
    private final DashboardOrganizerGUIView view = new DashboardOrganizerGUIView();

    public DashboardOrganizerGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        Scene scene = new Scene(view.buildRoot(
                () -> {
                    SessionManager.getInstance().setLoggedUser(null);
                    MainGUI.showLogin();
                },
                () -> {
                    AppLogger.logInfo("Navigazione: Apertura pagina 'Crea Evento'..."); // 🌟 Logger
                    new CreateEventGUI(stage).show();
                },
                () -> AppLogger.logInfo("Navigazione: Apertura pagina 'Modifica Evento'..."), // 🌟 Senza graffe inutili
                () -> AppLogger.logInfo("Navigazione: Apertura pagina 'Gestione Lista'...")  // 🌟 Senza graffe inutili
        ), MainGUI.WINDOW_WIDTH, MainGUI.WINDOW_HEIGHT);

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/nightflow.css").toExternalForm());
        } catch (Exception e) {
            AppLogger.logWarning("CSS non trovato"); // 🌟 Logger
        }

        stage.setScene(scene);
        stage.show();
    }
}