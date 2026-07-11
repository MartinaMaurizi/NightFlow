package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.bean.ClientBean;
import it.ispwproject.nightflow.bean.OrganizerBean;
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

public class ProfileGUI {
    private final Stage stage;
    private final ProfileGUIView view = new ProfileGUIView();

    public ProfileGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        // 1. La Boundary chiama il Controller Applicativo per farsi dare il Bean
        UserController controller = new UserController();
        Object profileBean = controller.getUserProfile();

        // 2. Estrazione dati "puri" dal Bean
        String nomeCompleto = "";
        String email = "";
        boolean isOrganizer = false;
        String ruolo = "";
        String stringaLocali = "N/A";
        String className = "";

        if (profileBean instanceof OrganizerBean) {
            OrganizerBean orgBean = (OrganizerBean) profileBean;
            nomeCompleto = orgBean.getFullName();
            email = orgBean.getEmail();
            isOrganizer = true;
            ruolo = "Organizzatore";
            className = "Organizer";

            if (orgBean.getLocalNames() == null || orgBean.getLocalNames().isEmpty()) {
                stringaLocali = "Nessun locale";
            } else {
                stringaLocali = String.join(", ", orgBean.getLocalNames());
            }

        } else if (profileBean instanceof ClientBean) {
            ClientBean cliBean = (ClientBean) profileBean;
            nomeCompleto = cliBean.getName() + " " + cliBean.getSurname();
            email = cliBean.getEmail();
            isOrganizer = false;
            ruolo = "Cliente";
            className = "Client";
        }

        // 3. Logica di navigazione basata sulla stringa estratta
        final String userRoleClass = className; // Variabile finale per la lambda
        Runnable goBack = () -> {
            if (userRoleClass.equals("Organizer")) {
                new DashboardOrganizerGUI(stage).show();
            } else if (userRoleClass.equals("Client")) {
                new DashboardClientGUI(stage).show();
            } else {
                MainGUI.showLogin();
            }
        };

        // 4. Chiamata alla View (Solo dati, nessuna Entity)
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
                this::show
        ), MainGUI.WINDOW_WIDTH, MainGUI.WINDOW_HEIGHT);

        // 5. Caricamento CSS e Azioni
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