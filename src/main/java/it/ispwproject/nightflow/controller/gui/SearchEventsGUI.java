package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.controller.applicativo.EventController;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.view.gui.SearchEventsGUIView;
import it.ispwproject.nightflow.util.logger.AppLogger;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.List;

public class SearchEventsGUI {
    private final Stage stage;
    private final SearchEventsGUIView view = new SearchEventsGUIView();

    public SearchEventsGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        Scene scene = new Scene(view.buildRoot(
                () -> new DashboardClientGUI(stage).show(), // Torna indietro pulito
                () -> {
                    SessionManager.getInstance().setLoggedUser(null);
                    MainGUI.showLogin();
                },
                nomeLocaleSelezionato -> {
                    AppLogger.logInfo("Hai cliccato su: " + nomeLocaleSelezionato);

                    try {
                        // 1. Chiamiamo il Controller Applicativo per avere i VERI eventi
                        EventController eventController = new EventController();
                        List<EventBean> eventiFuturi = eventController.getAllUpcomingEvents();

                        // 2. Cerchiamo l'evento corrispondente al locale cliccato
                        EventBean veroEvento = null;
                        for (EventBean evento : eventiFuturi) {
                            if (evento.getLocalName().equalsIgnoreCase(nomeLocaleSelezionato)) {
                                veroEvento = evento;
                                break; // Trovato! Prendiamo il primo evento in programma per questo locale
                            }
                        }

                        // 3. Se esiste, andiamo al checkout, altrimenti avvisiamo l'utente
                        if (veroEvento != null) {
                            new BookTicketGUI(stage, veroEvento).show();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Nessun Evento");
                            alert.setHeaderText("Nessuna serata in programma");
                            alert.setContentText("Al momento non ci sono eventi imminenti per il locale: " + nomeLocaleSelezionato);
                            alert.showAndWait();
                        }

                    } catch (Exception e) {
                        AppLogger.logError("Errore durante il caricamento degli eventi: " + e.getMessage());
                    }
                }
        ), MainGUI.WINDOW_WIDTH, MainGUI.WINDOW_HEIGHT);

        if (view.profileBtn != null) view.profileBtn.setOnAction(e -> new ProfileGUI(stage).show());
        if (view.homeBtn != null) view.homeBtn.setOnAction(e -> new DashboardClientGUI(stage).show());

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/nightflow.css").toExternalForm());
        } catch (Exception e) {
            AppLogger.logWarning("CSS non trovato: " + e.getMessage());
        }

        stage.setScene(scene);
        stage.show();
    }
}