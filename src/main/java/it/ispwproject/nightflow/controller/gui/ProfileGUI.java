package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.model.Client;
import it.ispwproject.nightflow.model.Organizer;
import it.ispwproject.nightflow.model.User;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.view.gui.ProfileGUIView;
import it.ispwproject.nightflow.util.logger.AppLogger;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProfileGUI {
    private final Stage stage;
    private final ProfileGUIView view = new ProfileGUIView();

    public ProfileGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        User loggedUser = SessionManager.getInstance().getLoggedUser();

        // 🌟 0. LOGICA DI NAVIGAZIONE INTELLIGENTE PER IL TASTO INDIETRO E LA HOME
        Runnable goBack = () -> {
            if (loggedUser instanceof Organizer) {
                new DashboardOrganizerGUI(stage).show();
            } else if (loggedUser instanceof Client) {
                // Sostituisci "DashboardClientGUI" con il nome esatto della schermata principale del Cliente se è diverso!
                new DashboardClientGUI(stage).show();
            } else {
                MainGUI.showLogin();
            }
        };

        // 🌟 1. CALCOLIAMO I LOCALI DINAMICAMENTE
        String stringaLocali = "Nessun locale";

        if (loggedUser instanceof Organizer) {
            try {
                // Chiediamo all'EventController tutti gli eventi creati da questo organizzatore
                it.ispwproject.nightflow.controller.applicativo.EventController ec = new it.ispwproject.nightflow.controller.applicativo.EventController();
                java.util.List<it.ispwproject.nightflow.bean.EventBean> eventi = ec.getOrganizerEvents();

                if (!eventi.isEmpty()) {
                    // Magia di Java: Estraiamo solo i nomi dei locali, rimuoviamo i doppioni e li uniamo con una virgola!
                    java.util.Set<String> nomiLocali = eventi.stream()
                            .map(it.ispwproject.nightflow.bean.EventBean::getLocalName)
                            .collect(java.util.stream.Collectors.toSet());

                    stringaLocali = String.join(", ", nomiLocali);
                }
            } catch (Exception e) {
                stringaLocali = "Errore caricamento locali";
            }
        }

        // 🌟 2. PASSIAMO LA STRINGA ALLA VIEW E IL GOBACK CORRETTO
        Scene scene = new Scene(view.buildRoot(
                loggedUser,
                stringaLocali,
                goBack, // <--- ORA USA IL NOSTRO GOBACK INTELLIGENTE!
                () -> {
                    SessionManager.getInstance().setLoggedUser(null);
                    MainGUI.showLogin();
                },
                () -> this.show()
        ), MainGUI.WINDOW_WIDTH, MainGUI.WINDOW_HEIGHT);

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/nightflow.css").toExternalForm());
        } catch (Exception e) {
            AppLogger.logWarning("CSS non trovato: " + e.getMessage());
        }

        // Azioni navigazione specifica
        if (loggedUser instanceof Client && view.myBookingsBtn != null) {
            view.myBookingsBtn.setOnAction(e -> new ViewBookingsGUI(stage).show());
        }

        // scommentare quando si ha la view
        // if (loggedUser instanceof Organizer && view.myEventsBtn != null) {
        //     view.myEventsBtn.setOnAction(e -> new ViewEventsGUI(stage).show());
        // }

        // Azione del Cambio Password
        if (view.changePwdBtn != null) {
            view.changePwdBtn.setOnAction(e -> mostraDialogCambioPassword(loggedUser));
        }

        // Azioni globali Navbar
        if (view.profileBtn != null) {
            view.profileBtn.setOnAction(e -> AppLogger.logInfo("Sei già nel profilo!"));
        }

        // 🌟 ORA QUESTO FUNZIONA PERFETTAMENTE
        if (view.homeBtn != null) {
            view.homeBtn.setOnAction(e -> goBack.run());
        }

        stage.setScene(scene);
        stage.show();
    }

    // ─── LOGICA DEL POPUP CAMBIO PASSWORD ───────────────────────────────────

    private void mostraDialogCambioPassword(User user) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Cambia Password");
        dialog.setHeaderText("Inserisci i dati per cambiare la tua password.");

        // Campi di input (oscurati per le password)
        PasswordField oldPwdField = new PasswordField();
        oldPwdField.setPromptText("Vecchia Password");

        PasswordField newPwdField = new PasswordField();
        newPwdField.setPromptText("Nuova Password");

        PasswordField confirmPwdField = new PasswordField();
        confirmPwdField.setPromptText("Conferma Nuova Password");

        VBox vbox = new VBox(10, oldPwdField, newPwdField, confirmPwdField);
        vbox.setPadding(new Insets(20, 10, 10, 10));
        dialog.getDialogPane().setContent(vbox);

        // Bottoni del popup
        ButtonType saveButtonType = new ButtonType("Salva", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Intercettiamo il click su "Salva" per fare i controlli prima di chiudere il popup
        final Button saveBtn = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveBtn.addEventFilter(ActionEvent.ACTION, event -> {

            String oldPwd = oldPwdField.getText();
            String newPwd = newPwdField.getText();
            String confirmPwd = confirmPwdField.getText();

            // 1. Controllo vecchia password
            if (!oldPwd.equals(user.getPassword())) {
                mostraErrore("La vecchia password non è corretta.");
                event.consume(); // Blocca la chiusura del popup
                return;
            }

            // 2. Controllo campo vuoto
            if (newPwd.isEmpty()) {
                mostraErrore("La nuova password non può essere vuota.");
                event.consume();
                return;
            }

            // 3. Controllo corrispondenza nuove password
            if (!newPwd.equals(confirmPwd)) {
                mostraErrore("Le nuove password non coincidono.");
                event.consume();
            }
        });

        // Se tutti i controlli passano e l'utente clicca Salva
        dialog.showAndWait().ifPresent(result -> {
            if (result == saveButtonType) {
                // Aggiorniamo la password nell'oggetto utente
                user.setPassword(newPwdField.getText());

                // (Se usi un Database, qui andrebbe chiamato il controller per salvare sul DB)
                // userController.updatePassword(user);

                AppLogger.logInfo("✅ Password cambiata con successo per: " + user.getEmail());

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Successo");
                success.setHeaderText(null);
                success.setContentText("Password aggiornata con successo!");
                success.showAndWait();
            }
        });
    }

    private void mostraErrore(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}