package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.controller.applicativo.EventController;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.view.gui.CreateEventGUIView;
import it.ispwproject.nightflow.util.logger.AppLogger;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Clock;

public class CreateEventGUI {
    private final Stage stage;
    private final CreateEventGUIView view = new CreateEventGUIView();
    private final EventController eventController = new EventController();

    public CreateEventGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        Scene scene = new Scene(view.buildRoot(
                // 1. onBack
                () -> new DashboardOrganizerGUI(stage).show(),

                // 2. onLogout
                () -> {
                    SessionManager.getInstance().setLoggedUser(null);
                    MainGUI.showLogin();
                },

                // 3. onProfile
                () -> new ProfileGUI(stage).show(),

                // 4. onHome
                () -> new DashboardOrganizerGUI(stage).show(),

                // 5. onCreate (Tutta la tua fantastica logica di salvataggio)
                () -> {
                    AppLogger.logInfo("Creazione evento in corso...");

                    String nome = view.nameFld.getText().isEmpty() ? "Nuovo Evento" : view.nameFld.getText();
                    String locale = view.venueFld.getText().isEmpty() ? "Jolie Club" : view.venueFld.getText();
                    String location = view.locationFld.getText().isEmpty() ? "Indirizzo non specificato" : view.locationFld.getText();
                    String desc = view.descArea.getText().isEmpty() ? "Nessuna descrizione" : view.descArea.getText();

                    double price = 15.0;
                    try { price = Double.parseDouble(view.priceFld.getText()); }
                    catch(NumberFormatException ex) { AppLogger.logWarning("Prezzo non valido, uso default"); }

                    int capacity = 100;
                    try { capacity = Integer.parseInt(view.capacityFld.getText()); }
                    catch(NumberFormatException ex) { AppLogger.logWarning("Capacità non valida, uso default"); }

                    // Gestione dinamica di Data e Ora
                    LocalDate selectedDate = view.datePicker.getValue();
                    if (selectedDate == null) {
                        selectedDate = LocalDate.now(Clock.systemDefaultZone()).plusDays(7); // Default
                    }

                    LocalTime startTime = LocalTime.of(22, 30); // Default
                    String timeInput = view.timeFld.getText();
                    try {
                        if (!timeInput.isEmpty()) {
                            // Se scrive "22:00-00:30", prendiamo le prime 5 lettere "22:00"
                            String startStr = timeInput.contains("-") ? timeInput.split("-")[0].trim() : timeInput.trim();
                            startTime = LocalTime.parse(startStr);
                        }
                    } catch (Exception e) {
                        AppLogger.logWarning("Formato ora non valido. Uso 22:30.");
                    }

                    LocalDateTime eventDateTime = LocalDateTime.of(selectedDate, startTime);

                    EventBean nuovoEvento = new EventBean();
                    nuovoEvento.setId(0);
                    nuovoEvento.setName(nome);
                    // Aggiungiamo l'orario completo testuale alla descrizione così non si perde!
                    nuovoEvento.setDescription(desc + " | Orario: " + (timeInput.isEmpty() ? "22:30" : timeInput));
                    nuovoEvento.setDateTime(eventDateTime);
                    nuovoEvento.setLocation(location);
                    nuovoEvento.setLocalName(locale);
                    nuovoEvento.setTotalCapacity(capacity);
                    nuovoEvento.setAvailableTickets(capacity);
                    nuovoEvento.setPrice(price);

                    if (SessionManager.getInstance().getLoggedUser() != null) {
                        nuovoEvento.setOrganizerId(SessionManager.getInstance().getLoggedUser().getId());
                    }

                    try {
                        eventController.createEvent(nuovoEvento);
                        AppLogger.logInfo("✅ Evento salvato con successo: " + nuovoEvento.getName());
                        new DashboardOrganizerGUI(stage).show();
                    } catch (Exception e) {
                        AppLogger.logError("Errore durante il salvataggio: " + e.getMessage());
                    }
                }
        ), MainGUI.WINDOW_WIDTH, MainGUI.WINDOW_HEIGHT);

        // Applichiamo il CSS per assicurarci che i bottoni della navbar si vedano bene
        try {
            scene.getStylesheets().add(getClass().getResource("/styles/nightflow.css").toExternalForm());
        } catch (Exception e) {
            AppLogger.logWarning("Impossibile caricare il CSS in CreateEventGUI: " + e.getMessage());
        }

        stage.setScene(scene);
        stage.show();
    }
}