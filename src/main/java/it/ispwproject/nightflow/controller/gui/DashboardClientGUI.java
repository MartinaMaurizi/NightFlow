package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.view.gui.DashboardClientGUIView;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DashboardClientGUI {
    private final Stage stage;
    private final DashboardClientGUIView view = new DashboardClientGUIView();

    public DashboardClientGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        // 🌟 AGGIORNAMENTO: Passiamo sia l'azione di Logout che l'azione di apertura Profilo!
        Scene scene = new Scene(view.buildRoot(
                () -> { // 1a Azione: Logout
                    SessionManager.getInstance().setLoggedUser(null);
                    MainGUI.showLogin();
                },
                () -> { // 2a Azione: Profilo (Ecco cosa succede quando clicchi l'omino!)
                    new ProfileGUI(stage).show();
                }
        ), MainGUI.WINDOW_WIDTH, MainGUI.WINDOW_HEIGHT);

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/nightflow.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("CSS non trovato");
        }

        // Caricamento Dati
        List<EventBean> eventi = new ArrayList<>();
        eventi.add(new EventBean(1, "Jolie Club", "Desc", LocalDateTime.now(), "Via Velletri, 13, Roma", "Jolie Club", 50, 15.0));
        eventi.add(new EventBean(2, "Jerò restaurant", "Desc", LocalDateTime.now(), "Via Torrita Tiberina, 22, Roma", "Jerò", 30, 20.0));

        view.updateEventList(eventi, (EventBean event) -> {
            new BookTicketGUI(stage, event).show();
        });

        view.searchField.setOnMouseClicked(e -> {
            new SearchEventsGUI(stage).show();
        });
// --- AZIONI DI NAVIGAZIONE GLOBALI ---
        if (view.profileBtn != null) {
            view.profileBtn.setOnAction(e -> new ProfileGUI(stage).show());
        }
        if (view.homeBtn != null) {
            view.homeBtn.setOnAction(e -> new DashboardClientGUI(stage).show());
        }
        stage.setScene(scene);
        stage.show();
    }
}