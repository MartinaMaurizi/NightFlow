package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.util.logger.AppLogger;
import it.ispwproject.nightflow.view.gui.BookTicketGUIView;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BookTicketGUI {
    private final Stage stage;
    private final BookTicketGUIView view = new BookTicketGUIView();
    private final EventBean event; // 🌟 Aggiunto l'evento!

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
                // 🌟 CORREZIONE 1 & 2: Tolte parentesi tonde e graffe per una Lambda pulita
                ticketInfo -> new CheckoutGUI(stage, event, ticketInfo).show(),
                event
        ), MainGUI.WINDOW_WIDTH, MainGUI.WINDOW_HEIGHT);

        if (view.profileBtn != null) view.profileBtn.setOnAction(e -> new ProfileGUI(stage).show());
        if (view.homeBtn != null) view.homeBtn.setOnAction(e -> new DashboardClientGUI(stage).show());

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/nightflow.css").toExternalForm());
        } catch (Exception e) {
            // 🌟 CORREZIONE 3: Non lasciare il catch vuoto!
            AppLogger.logWarning("Impossibile caricare il CSS: " + e.getMessage());
        }

        stage.setScene(scene);
        stage.show();
    }
}