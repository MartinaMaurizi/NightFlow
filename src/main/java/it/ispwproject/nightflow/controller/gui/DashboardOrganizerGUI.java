package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.view.gui.DashboardOrganizerGUIView;
import it.ispwproject.nightflow.util.logger.AppLogger;
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
                    AppLogger.logInfo("Navigazione: Apertura pagina 'Crea Evento'...");
                    new CreateEventGUI(stage).show();
                },
                () -> {
                    AppLogger.logInfo("Navigazione: Apertura pagina 'Modifica Evento'...");
                    new ViewEventsGUI(stage).show();
                },
                () -> {
                    AppLogger.logInfo("Navigazione: Apertura pagina 'Gestione Lista'...");
                    new ManageParticipantsGUI(stage).show();
                }
        ), MainGUI.WINDOW_WIDTH, MainGUI.WINDOW_HEIGHT);

        // =================================================================
        // AGGIUNTA AZIONI PER I BOTTONI PROFILO E HOME
        // =================================================================
        if (view.profileBtn != null) {
            view.profileBtn.setOnAction(e -> {
                AppLogger.logInfo("Navigazione: Apertura pagina 'Profilo'...");
                new ProfileGUI(stage).show();
            });
        }

        if (view.homeBtn != null) {
            view.homeBtn.setOnAction(e -> {
                AppLogger.logInfo("Navigazione: Ricarica pagina 'Dashboard'...");
                this.show(); // Ricarica semplicemente questa stessa pagina
            });
        }
        // =================================================================

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/nightflow.css").toExternalForm());
        } catch (Exception e) {
            AppLogger.logWarning("CSS non trovato");
        }

        stage.setScene(scene);
        stage.show();
    }
}