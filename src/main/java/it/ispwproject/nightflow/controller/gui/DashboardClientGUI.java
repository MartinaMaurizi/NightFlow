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

// Caricamento Dati a prova di SonarCloud
        List<EventBean> eventi = new ArrayList<>();

        EventBean e1 = new EventBean();
        e1.setId(1);
        e1.setName("Jolie Club");
        e1.setDescription("Desc");
        e1.setDateTime(LocalDateTime.now());
        e1.setLocation("Via Velletri, 13, Roma");
        e1.setLocalName("Jolie Club");
        e1.setAvailableTickets(50);
        e1.setPrice(15.0);
        eventi.add(e1);

        EventBean e2 = new EventBean();
        e2.setId(2);
        e2.setName("Jerò restaurant");
        e2.setDescription("Desc");
        e2.setDateTime(LocalDateTime.now());
        e2.setLocation("Via Torrita Tiberina, 22, Roma");
        e2.setLocalName("Jerò");
        e2.setAvailableTickets(30);
        e2.setPrice(20.0);
        eventi.add(e2);
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