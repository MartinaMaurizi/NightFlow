package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.controller.applicativo.LoginController;
import it.ispwproject.nightflow.controller.applicativo.LoginController.LoginResult;
import it.ispwproject.nightflow.exception.LoginException;
import it.ispwproject.nightflow.view.gui.LoginGUIView;
import javafx.stage.Stage;

public class LoginGUI {

    private final Stage stage;
    private final LoginController loginController = new LoginController();
    private final LoginGUIView view = new LoginGUIView();

    public LoginGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        stage.setScene(GUIUtils.createScene(
                view.buildRoot(this::handleLogin, MainGUI::showRegistration)));
        stage.show();
    }

    private void handleLogin() {
        String email = view.emailField.getText().trim();
        String password = view.passField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            view.setError("Inserisci sia email che password.");
            return;
        }

        try {
            LoginResult result = loginController.login(email, password);

            switch (result) {
                case SUCCESSO_ORGANIZER -> MainGUI.showDashboardOrganizer();
                case SUCCESSO_CLIENT    -> MainGUI.showDashboardClient();
            }
        } catch (LoginException e) {
            // L'errore viene mandato alla vista senza stampe in console
            view.setError(e.getMessage());
        }
    }
}