package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.bean.RegistrationBean;
import it.ispwproject.nightflow.controller.applicativo.RegistrationController;
import it.ispwproject.nightflow.view.gui.RegistrationGUIView;
import it.ispwproject.nightflow.enumerator.Role;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RegistrationGUI {

    private final RegistrationGUIView view = new RegistrationGUIView();
    private final RegistrationController controller = new RegistrationController();
    private final Stage stage;

    public RegistrationGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        var root = view.buildRoot();
        Scene scene = new Scene(root, MainGUI.WINDOW_WIDTH, MainGUI.WINDOW_HEIGHT);

        try {
            scene.getStylesheets().add(getClass().getResource("/styles/nightflow.css").toExternalForm());
        } catch (Exception e) {
            // Blocco vuoto tollerato per non sporcare la console se manca il CSS
        }

        // Cablaggio pulito dei bottoni
        view.registerBtn.setOnAction(e -> handleRegistration());
        view.backBtn.setOnAction(e -> MainGUI.showLogin());

        stage.setScene(scene);
        stage.show();
    }

    // Tutta la logica estratta in un metodo dedicato
    private void handleRegistration() {
        if (view.checkMandatoryFields()) {
            RegistrationBean bean = new RegistrationBean();
            bean.setName(view.nameField.getText());
            bean.setSurname(view.surnameField.getText());
            bean.setEmail(view.emailField.getText());
            bean.setPassword(view.passField.getText());
            bean.setConfirmPassword(view.confirmPassField.getText());
            bean.setRole(view.isOrganizerCheck.isSelected() ? Role.ORGANIZER : Role.CLIENT);

            // Assicuriamoci che la data sia formattata bene
            bean.setDateOfBirth(view.dobPicker.getValue());
            bean.setGender(view.genderBox.getValue());
            bean.setCountry(view.countryBox.getValue());
            bean.setCity(view.cityBox.getValue());

            try {
                controller.register(bean);
                if (bean.getRole() == Role.ORGANIZER) {
                    MainGUI.showDashboardOrganizer();
                } else {
                    MainGUI.showDashboardClient();
                }
            } catch (Exception ex) {
                ex.printStackTrace(); // Stampa l'errore completo in console
                view.errorLabel.setText("Errore: " + ex.getMessage());
                view.errorLabel.setStyle("-fx-text-fill: #ff1744; -fx-font-weight: bold;");
            }
        }
    }
}