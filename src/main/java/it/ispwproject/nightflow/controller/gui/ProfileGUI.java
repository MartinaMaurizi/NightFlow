package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.view.gui.ProfileGUIView;
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
                () -> new DashboardClientGUI(stage).show(), // Back (<)
                () -> { // Logout
                    SessionManager.getInstance().setLoggedUser(null);
                    MainGUI.showLogin();
                },
                () -> {
                    System.out.println("Sei già nel profilo!"); // Clic sull'omino del profilo
                }
        ), MainGUI.WINDOW_WIDTH, MainGUI.WINDOW_HEIGHT);

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/nightflow.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("CSS non trovato");
        }

        // =================================================================
        // 🌟 AZIONI SPECIFICHE DEL PROFILO 🌟
        // =================================================================
        // Collega il bottone "Le mie prenotazioni" alla nuova View!
        if (view.myBookingsBtn != null) {
            view.myBookingsBtn.setOnAction(e -> new ViewBookingsGUI(stage).show());
        }

        // (Aggiungi qui altri tasti del profilo se ne hai, es. "Modifica Dati")

        // =================================================================
        // --- AZIONI DI NAVIGAZIONE GLOBALI (Navbar) ---
        // =================================================================
        if (view.profileBtn != null) {
            view.profileBtn.setOnAction(e -> System.out.println("Sei già nel profilo!"));
        }
        if (view.homeBtn != null) {
            view.homeBtn.setOnAction(e -> new DashboardClientGUI(stage).show());
        }

        stage.setScene(scene);
        stage.show();
    }
}