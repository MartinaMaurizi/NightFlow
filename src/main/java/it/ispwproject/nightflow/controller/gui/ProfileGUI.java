package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.view.gui.ProfileGUIView;
import it.ispwproject.nightflow.util.logger.AppLogger;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProfileGUI {
    private final Stage stage;
    private final ProfileGUIView view = new ProfileGUIView();

    public ProfileGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        Scene scene = new Scene(view.buildRoot(
                () -> new DashboardClientGUI(stage).show(),
                () -> {
                    SessionManager.getInstance().setLoggedUser(null);
                    MainGUI.showLogin();
                },
                // 🌟 Rimosse graffe inutili (avviso riga 23)
                () -> AppLogger.logInfo("Sei già nel profilo!")
        ), MainGUI.WINDOW_WIDTH, MainGUI.WINDOW_HEIGHT);

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/nightflow.css").toExternalForm());
        } catch (Exception e) {
            // 🌟 Logger invece di System.err (avviso riga 31)
            AppLogger.logWarning("CSS non trovato: " + e.getMessage());
        }

        // Azioni specifiche
        if (view.myBookingsBtn != null) {
            view.myBookingsBtn.setOnAction(e -> new ViewBookingsGUI(stage).show());
        }

        // Azioni globali
        if (view.profileBtn != null) {
            // 🌟 Logger invece di System.out (avviso riga 24)
            view.profileBtn.setOnAction(e -> AppLogger.logInfo("Sei già nel profilo!"));
        }
        if (view.homeBtn != null) {
            view.homeBtn.setOnAction(e -> new DashboardClientGUI(stage).show());
        }

        stage.setScene(scene);
        stage.show();
    }
}