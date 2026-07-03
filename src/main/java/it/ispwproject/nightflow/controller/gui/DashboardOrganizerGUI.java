package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.view.gui.DashboardOrganizerGUIView;
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
                    // 1. Azione di Logout
                    SessionManager.getInstance().setLoggedUser(null);
                    MainGUI.showLogin();
                },
                () -> {
                    // 2. Azione: Crea Evento
                    System.out.println(" Navigazione: Apertura pagina 'Crea Evento'...");
                    new CreateEventGUI(stage).show();
                },
                () -> {
                    // 3. Azione: Modifica Evento
                    System.out.println(" Navigazione: Apertura pagina 'Modifica Evento'...");
                    // new ModifyEventGUI(stage).show(); <-- Lo scommenteremo dopo
                },
                () -> {
                    // 4. Azione: Gestisci Lista Clienti
                    System.out.println(" Navigazione: Apertura pagina 'Gestione Lista'...");
                    // new ManageListGUI(stage).show(); <-- Lo scommenteremo dopo
                }
        ), MainGUI.WINDOW_WIDTH, MainGUI.WINDOW_HEIGHT);

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/nightflow.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("CSS non trovato");
        }

        stage.setScene(scene);
        stage.show();
    }
}