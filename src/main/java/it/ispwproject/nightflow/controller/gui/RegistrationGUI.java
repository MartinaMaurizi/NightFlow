package it.ispwproject.nightflow.controller.gui;

import it.ispwproject.nightflow.bean.RegistrationBean;
import it.ispwproject.nightflow.controller.applicativo.RegistrationController;
import it.ispwproject.nightflow.model.User;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
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
            System.err.println("Attenzione: file CSS non trovato");
        }

        view.registerBtn.setOnAction(e -> {
            RegistrationBean bean = new RegistrationBean();
            bean.setName(view.nameField.getText());
            bean.setSurname(view.surnameField.getText());
            bean.setEmail(view.emailField.getText());
            bean.setPassword(view.passField.getText());
            bean.setConfirmPassword(view.confirmPassField.getText());
            bean.setRole(view.isOrganizerCheck.isSelected() ? Role.ORGANIZER : Role.CLIENT);
            bean.setDateOfBirth(view.dobPicker.getValue());
            bean.setGender(view.genderBox.getValue());
            bean.setCountry(view.countryBox.getValue());
            bean.setCity(view.cityBox.getValue());
            try {
                controller.register(bean);

                    // AGGIUNGI QUESTA RIGA SPIA:
                    System.out.println("✅ Salvataggio superato! Sto provando ad aprire la Dashboard...");

                // 1. Inserisce l'utente nella sessione (evita il crash della Dashboard)
                User utenteRegistrato = new User();
                utenteRegistrato.setName(bean.getName());
                SessionManager.getInstance().setLoggedUser(utenteRegistrato);

                // 2. Sceglie la dashboard giusta in base alla spunta
                if (bean.getRole() == Role.ORGANIZER) {
                    MainGUI.showDashboardOrganizer();
                } else {
                    MainGUI.showDashboardClient();
                }
            } catch (Exception ex) {
                view.errorLabel.setText(ex.getMessage());
                ex.printStackTrace(); // Se c'è un errore col DB, ora lo vedi rosso in console!
            }
        });

        // Modificato in setOnAction per non farlo "incastrare"
        view.backBtn.setOnAction(e -> {
            MainGUI.showLogin();
        });

        stage.setScene(scene);
        stage.show();
    }
}