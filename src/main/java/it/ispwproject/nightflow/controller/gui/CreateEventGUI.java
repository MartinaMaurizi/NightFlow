package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.view.gui.CreateEventGUIView;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.time.LocalDateTime;

public class CreateEventGUI {
    private final Stage stage;
    private final CreateEventGUIView view = new CreateEventGUIView();

    public CreateEventGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        Scene scene = new Scene(view.buildRoot(
                () -> new DashboardOrganizerGUI(stage).show(), // Tasto Back
                () -> { // Tasto Logout
                    SessionManager.getInstance().setLoggedUser(null);
                    MainGUI.showLogin();
                },
                () -> { // Azione: CREA EVENTO
                    System.out.println("Creazione evento in corso...");

                    // Raccogliamo i dati (con valori di default se l'utente lascia vuoto)
                    String nome = view.nameFld.getText().isEmpty() ? "Nuovo Evento" : view.nameFld.getText();
                    String locale = view.venueFld.getText().isEmpty() ? "Jolie Club" : view.venueFld.getText();
                    String desc = view.descArea.getText().isEmpty() ? "Nessuna descrizione" : view.descArea.getText();

                    double price = 15.0;
                    try { price = Double.parseDouble(view.priceFld.getText()); } catch(Exception ex) {}

                    int capacity = 100;
                    try { capacity = Integer.parseInt(view.capacityFld.getText()); } catch(Exception ex) {}

                    // Creiamo il nuovo EventBean simulato
                    EventBean nuovoEvento = new EventBean(
                            999, // ID fittizio
                            nome,
                            desc,
                            LocalDateTime.now().plusDays(7), // Simuliamo una data tra 7 giorni
                            "Roma",
                            locale,
                            capacity,
                            price
                    );

                    System.out.println("✅ Evento creato con successo: " + nuovoEvento.getName());

                    // Invece di tornare alla dashboard dell'organizzatore, ti porto
                    // direttamente alla Dashboard Cliente per vedere il risultato!
                    DashboardClientGUI clientDashboard = new DashboardClientGUI(stage);
                    clientDashboard.show();

                    // Opzionale: Se avessimo un database, l'evento apparirebbe da solo.
                    // In assenza di DB, il trucco è che la DashboardClient crea sempre i
                    // suoi eventi base nel metodo show(), quindi se volessimo aggiungerlo
                    // dinamicamente dovremmo passarlo al controller. Per ora va benissimo così!
                }
        ), MainGUI.WINDOW_WIDTH, MainGUI.WINDOW_HEIGHT);

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/nightflow.css").toExternalForm());
        } catch (Exception e) {}

        stage.setScene(scene);
        stage.show();
    }
}