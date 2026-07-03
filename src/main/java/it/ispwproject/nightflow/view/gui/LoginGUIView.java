package it.ispwproject.nightflow.view.gui;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginGUIView {

    // Componenti della UI
    public final TextField emailField = new TextField();
    public final PasswordField passField = new PasswordField();
    public final TextField plainPassField = new TextField(); // NUOVO: Campo per la password in chiaro
    public final CheckBox showPassCheck = new CheckBox("Mostra password");
    public final Button loginBtn = new Button("Login");
    public final Hyperlink registerLink = new Hyperlink("Registrati qui");

    public VBox buildRoot() {
        // Configurazione segnaposti
        emailField.setPromptText("Email");
        passField.setPromptText("Password");
        plainPassField.setPromptText("Password");

        // 1. Label Titolo Neon
        Label titleLabel = new Label("NightFlow");
        titleLabel.setId("NightFlow-Neon");

        // 2. LOGICA MOSTRA PASSWORD
        // Nascondiamo il campo in chiaro di default
        plainPassField.setVisible(false);
        plainPassField.setManaged(false); // Evita che occupi spazio quando è nascosto
        // Diciamo al campo in chiaro di usare lo stesso stile CSS del campo password
        plainPassField.getStyleClass().add("password-field");

        // Sincronizziamo il testo tra i due campi (se scrivi in uno, si copia nell'altro)
        plainPassField.textProperty().bindBidirectional(passField.textProperty());

        // Quando clicchi la checkbox, alterna la visibilità dei due campi
        showPassCheck.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                // Mostra in chiaro
                plainPassField.setVisible(true);
                plainPassField.setManaged(true);
                passField.setVisible(false);
                passField.setManaged(false);
            } else {
                // Nascondi
                plainPassField.setVisible(false);
                plainPassField.setManaged(false);
                passField.setVisible(true);
                passField.setManaged(true);
            }
        });

        // Mettiamo i due campi uno sopra l'altro
        StackPane passwordContainer = new StackPane(passField, plainPassField);

        // 3. Gruppo input (Email, Password Container, Checkbox, Login)
        VBox inputGroup = new VBox(10);
        inputGroup.setMaxWidth(350);
        inputGroup.setAlignment(Pos.CENTER);

        // HBox per allineare la checkbox a destra
        HBox checkContainer = new HBox();
        checkContainer.setAlignment(Pos.CENTER_RIGHT);
        checkContainer.getChildren().add(showPassCheck);

        inputGroup.getChildren().addAll(
                emailField,
                passwordContainer, // Inseriamo il contenitore al posto del singolo passField
                checkContainer,
                loginBtn
        );

        // 4. Testo finale e link
        Label questionLabel = new Label("Non hai ancora un account?");
        questionLabel.setStyle("-fx-text-fill: white; -fx-opacity: 0.8;");

        // 5. Layout principale
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);

        root.getChildren().addAll(
                titleLabel,
                inputGroup,
                questionLabel,
                registerLink
        );

        return root;
    }
}