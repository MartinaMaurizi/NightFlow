package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.view.gui.SearchEventsGUIView;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.time.LocalDateTime;

public class SearchEventsGUI {
    private final Stage stage;
    private final SearchEventsGUIView view = new SearchEventsGUIView();

    public SearchEventsGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        Scene scene = new Scene(view.buildRoot(
                () -> new DashboardClientGUI(stage).show(), // Tasto Back
                () -> { // Tasto Logout
                    SessionManager.getInstance().setLoggedUser(null);
                    MainGUI.showLogin();
                },
                (String nomeLocaleSelezionato) -> {
                    System.out.println("Hai cliccato su: " + nomeLocaleSelezionato);

                    // 🌟 RISOLTO: Creiamo un evento "finto" usando i setter
                    EventBean fintoEvento = new EventBean();
                    fintoEvento.setId(99);
                    fintoEvento.setName("Serata a " + nomeLocaleSelezionato);
                    fintoEvento.setDescription("Descrizione evento");
                    fintoEvento.setDateTime(LocalDateTime.now().plusDays(2));
                    fintoEvento.setLocation("Roma");
                    fintoEvento.setLocalName(nomeLocaleSelezionato);
                    fintoEvento.setAvailableTickets(100);
                    fintoEvento.setPrice(15.0);

                    // Ora passiamo l'evento correttamente alla schermata dei biglietti!
                    new BookTicketGUI(stage, fintoEvento).show();
                }
        ), MainGUI.WINDOW_WIDTH, MainGUI.WINDOW_HEIGHT);

        // Azioni di navigazione fisse
        if (view.profileBtn != null) view.profileBtn.setOnAction(e -> new ProfileGUI(stage).show());
        if (view.homeBtn != null) view.homeBtn.setOnAction(e -> new DashboardClientGUI(stage).show());

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/nightflow.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("CSS non trovato");
        }

        stage.setScene(scene);
        stage.show();
    }
}