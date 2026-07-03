package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.view.gui.CreateEventGUIView;
import it.ispwproject.nightflow.util.logger.AppLogger; // 🌟 Logger
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.Clock; // 🌟 Clock

public class CreateEventGUI {
    private final Stage stage;
    private final CreateEventGUIView view = new CreateEventGUIView();

    public CreateEventGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        Scene scene = new Scene(view.buildRoot(
                () -> new DashboardOrganizerGUI(stage).show(),
                () -> {
                    SessionManager.getInstance().setLoggedUser(null);
                    MainGUI.showLogin();
                },
                () -> {
                    AppLogger.logInfo("Creazione evento in corso..."); // 🌟 Logger

                    String nome = view.nameFld.getText().isEmpty() ? "Nuovo Evento" : view.nameFld.getText();
                    String locale = view.venueFld.getText().isEmpty() ? "Jolie Club" : view.venueFld.getText();
                    String desc = view.descArea.getText().isEmpty() ? "Nessuna descrizione" : view.descArea.getText();

                    double price = 15.0;
                    try { price = Double.parseDouble(view.priceFld.getText()); }
                    catch(NumberFormatException ex) { AppLogger.logWarning("Prezzo non valido, uso default"); } // 🌟 Catch loggato

                    int capacity = 100;
                    try { capacity = Integer.parseInt(view.capacityFld.getText()); }
                    catch(NumberFormatException ex) { AppLogger.logWarning("Capacità non valida, uso default"); } // 🌟 Catch loggato

                    EventBean nuovoEvento = new EventBean();
                    nuovoEvento.setId(999);
                    nuovoEvento.setName(nome);
                    nuovoEvento.setDescription(desc);
                    // 🌟 Timezone esplicita
                    nuovoEvento.setDateTime(LocalDateTime.now(Clock.systemDefaultZone()).plusDays(7));
                    nuovoEvento.setLocation("Roma");
                    nuovoEvento.setLocalName(locale);
                    nuovoEvento.setAvailableTickets(capacity);
                    nuovoEvento.setPrice(price);

                    AppLogger.logInfo("✅ Evento creato con successo: " + nuovoEvento.getName()); // 🌟 Logger

                    new DashboardClientGUI(stage).show();
                }
        ), MainGUI.WINDOW_WIDTH, MainGUI.WINDOW_HEIGHT);

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/nightflow.css").toExternalForm());
        } catch (Exception e) {
            AppLogger.logWarning("CSS non trovato: " + e.getMessage()); // 🌟 Catch loggato
        }

        stage.setScene(scene);
        stage.show();
    }
}