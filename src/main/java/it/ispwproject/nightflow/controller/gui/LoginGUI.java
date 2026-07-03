package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.controller.applicativo.LoginController;
import it.ispwproject.nightflow.controller.applicativo.LoginController.LoginResult;
import it.ispwproject.nightflow.view.gui.LoginGUIView;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginGUI {
    private final LoginGUIView view = new LoginGUIView();
    private final Stage stage;
    private final LoginController controller = new LoginController();

    public LoginGUI(Stage stage) { this.stage = stage; }

    public void show() {
        Scene scene = new Scene(view.buildRoot(), MainGUI.WINDOW_WIDTH, MainGUI.WINDOW_HEIGHT);

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/nightflow.css").toExternalForm());
        } catch (Exception e) {}

        view.loginBtn.setOnAction(e -> {
            System.out.println("Tentativo di login con: " + view.emailField.getText());

            try {
                // Il controller controlla il DB, salva la sessione e ci dice dove andare
                LoginResult result = controller.login(view.emailField.getText(), view.passField.getText());

                // Smistamento alla Dashboard corretta
                if (result == LoginResult.SUCCESSO_ORGANIZER) {
                    MainGUI.showDashboardOrganizer();
                } else if (result == LoginResult.SUCCESSO_CLIENT) {
                    MainGUI.showDashboardClient();
                }

            } catch (Exception ex) {
                System.out.println("Credenziali errate o errore: " + ex.getMessage());
                // Se hai una label di errore nella grafica, puoi scommentare qui sotto:
                // view.errorLabel.setText("Email o password errati");
                ex.printStackTrace();
            }
        });

        view.registerLink.setOnAction(e -> MainGUI.showRegistration());

        stage.setScene(scene);
        stage.show();
    }
}