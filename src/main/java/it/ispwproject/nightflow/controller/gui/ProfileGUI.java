package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.controller.applicativo.UserController;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.view.gui.ProfileGUIView;
import it.ispwproject.nightflow.util.logger.AppLogger;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// Nota: User non è importato qui. Usiamo un riferimento generico tramite il SessionManager
// o, se necessario, manteniamo il minimo indispensabile nel controller.
public class ProfileGUI {
    private final Stage stage;
    private final ProfileGUIView view = new ProfileGUIView();

    public ProfileGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        // Recuperiamo l'utente dalla sessione
        var loggedUser = SessionManager.getInstance().getLoggedUser();

        // 1. Logica di navigazione
        Runnable goBack = () -> {
            // Qui usiamo il nome della classe per evitare accoppiamenti forti
            String className = loggedUser.getClass().getSimpleName();
            if (className.equals("Organizer")) {
                new DashboardOrganizerGUI(stage).show();
            } else if (className.equals("Client")) {
                new DashboardClientGUI(stage).show();
            } else {
                MainGUI.showLogin();
            }
        };

        // 2. Preparazione dati "puri" per la View
        String nomeCompleto = loggedUser.getName() + " " + loggedUser.getSurname();
        String email = loggedUser.getEmail();
        boolean isOrganizer = loggedUser.getClass().getSimpleName().equals("Organizer");
        String ruolo = isOrganizer ? "Organizzatore" : "Cliente";
        String stringaLocali = isOrganizer ? calcolaLocali() : "N/A";

        // 3. Chiamata alla View (Solo dati, nessuna Entity)
        Scene scene = new Scene(view.buildRoot(
                nomeCompleto,
                email,
                ruolo,
                isOrganizer,
                stringaLocali,
                goBack,
                () -> {
                    SessionManager.getInstance().setLoggedUser(null);
                    MainGUI.showLogin();
                },
                () -> this.show()
        ), MainGUI.WINDOW_WIDTH, MainGUI.WINDOW_HEIGHT);

        // 4. Caricamento CSS e Azioni
        try {
            scene.getStylesheets().add(getClass().getResource("/styles/nightflow.css").toExternalForm());
        } catch (Exception e) {
            AppLogger.logWarning("CSS non trovato: " + e.getMessage());
        }

        if (!isOrganizer && view.myBookingsBtn != null) {
            view.myBookingsBtn.setOnAction(e -> new ViewBookingsGUI(stage).show());
        }

        if (view.changePwdBtn != null) {
            view.changePwdBtn.setOnAction(e -> mostraDialogCambioPassword());
        }

        if (view.profileBtn != null) {
            view.profileBtn.setOnAction(e -> AppLogger.logInfo("Sei già nel profilo!"));
        }
        if (view.homeBtn != null) {
            view.homeBtn.setOnAction(e -> goBack.run());
        }

        stage.setScene(scene);
        stage.show();
    }

    private String calcolaLocali() {
        try {
            var ec = new it.ispwproject.nightflow.controller.applicativo.EventController();
            var eventi = ec.getOrganizerEvents();
            if (eventi.isEmpty()) return "Nessun locale";
            return eventi.stream()
                    .map(it.ispwproject.nightflow.bean.EventBean::getLocalName)
                    .distinct()
                    .collect(java.util.stream.Collectors.joining(", "));
        } catch (Exception e) {
            return "Errore caricamento";
        }
    }

    private void mostraDialogCambioPassword() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Cambia Password");

        PasswordField oldPwdField = new PasswordField(); oldPwdField.setPromptText("Vecchia Password");
        PasswordField newPwdField = new PasswordField(); newPwdField.setPromptText("Nuova Password");
        PasswordField confirmPwdField = new PasswordField(); confirmPwdField.setPromptText("Conferma Nuova Password");

        VBox vbox = new VBox(10, oldPwdField, newPwdField, confirmPwdField);
        vbox.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(new ButtonType("Salva", ButtonBar.ButtonData.OK_DONE), ButtonType.CANCEL);

        final Button saveBtn = (Button) dialog.getDialogPane().lookupButton(dialog.getDialogPane().getButtonTypes().get(0));
        saveBtn.addEventFilter(ActionEvent.ACTION, event -> {
            if (!newPwdField.getText().equals(confirmPwdField.getText())) {
                mostraErrore("Le nuove password non coincidono.");
                event.consume();
                return;
            }
            try {
                new UserController().updatePassword(oldPwdField.getText(), newPwdField.getText());
            } catch (DAOException e) {
                mostraErrore(e.getMessage());
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    private void mostraErrore(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}