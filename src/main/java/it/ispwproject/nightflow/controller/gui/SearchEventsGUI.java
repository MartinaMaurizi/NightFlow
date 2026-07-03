package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.view.gui.SearchEventsGUIView;
import it.ispwproject.nightflow.util.logger.AppLogger;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.Clock;

public class SearchEventsGUI {
    private final Stage stage;
    private final SearchEventsGUIView view = new SearchEventsGUIView();

    public SearchEventsGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        Scene scene = new Scene(view.buildRoot(
                () -> new DashboardClientGUI(stage).show(),
                () -> {
                    SessionManager.getInstance().setLoggedUser(null);
                    MainGUI.showLogin();
                },
                nomeLocaleSelezionato -> {
                    AppLogger.logInfo("Hai cliccato su: " + nomeLocaleSelezionato);

                    EventBean fintoEvento = new EventBean();
                    fintoEvento.setId(99);
                    fintoEvento.setName("Serata a " + nomeLocaleSelezionato);
                    fintoEvento.setDescription("Descrizione evento");
                    // 🌟 Timezone esplicita
                    fintoEvento.setDateTime(LocalDateTime.now(Clock.systemDefaultZone()).plusDays(2));
                    fintoEvento.setLocation("Roma");
                    fintoEvento.setLocalName(nomeLocaleSelezionato);
                    fintoEvento.setAvailableTickets(100);
                    fintoEvento.setPrice(15.0);

                    new BookTicketGUI(stage, fintoEvento).show();
                }
        ), MainGUI.WINDOW_WIDTH, MainGUI.WINDOW_HEIGHT);

        if (view.profileBtn != null) view.profileBtn.setOnAction(e -> new ProfileGUI(stage).show());
        if (view.homeBtn != null) view.homeBtn.setOnAction(e -> new DashboardClientGUI(stage).show());

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/nightflow.css").toExternalForm());
        } catch (Exception e) {
            AppLogger.logWarning("CSS non trovato: " + e.getMessage()); // 🌟 Logger
        }

        stage.setScene(scene);
        stage.show();
    }
}