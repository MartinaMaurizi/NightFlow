package it.ispwproject.nightflow.view.gui;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginGUIView {

    // Componenti della UI pubblici per leggerne il testo dal controller
    public final TextField emailField = new TextField();
    public final PasswordField passField = new PasswordField();
    public final TextField plainPassField = new TextField();
    public final CheckBox showPassCheck = new CheckBox("Mostra password");
    public final Button loginBtn = new Button("Login");
    public final Hyperlink registerLink = new Hyperlink("Registrati qui");

    // NUOVO: Etichetta per mostrare gli errori di login in grafica
    private final Label errorLabel = new Label();

    // NUOVO: Passiamo le azioni come parametri (Runnable) per mantenere la classe pura
    public VBox buildRoot(Runnable onLoginClick, Runnable onRegisterClick) {

        emailField.setPromptText("Email");
        passField.setPromptText("Password");
        plainPassField.setPromptText("Password");

        Label titleLabel = new Label("NightFlow");
        titleLabel.setId("NightFlow-Neon");

        // Setup grafico dell'errore (invisibile all'inizio)
        errorLabel.setStyle("-fx-text-fill: #ff4c4c; -fx-font-weight: bold;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false); // Evita che occupi spazio a vuoto

        plainPassField.setVisible(false);
        plainPassField.setManaged(false);
        plainPassField.getStyleClass().add("password-field");
        plainPassField.textProperty().bindBidirectional(passField.textProperty());

        showPassCheck.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            boolean show = isSelected;
            plainPassField.setVisible(show);
            plainPassField.setManaged(show);
            passField.setVisible(!show);
            passField.setManaged(!show);
        });

        StackPane passwordContainer = new StackPane(passField, plainPassField);

        VBox inputGroup = new VBox(10);
        inputGroup.setMaxWidth(350);
        inputGroup.setAlignment(Pos.CENTER);

        HBox checkContainer = new HBox();
        checkContainer.setAlignment(Pos.CENTER_RIGHT);
        checkContainer.getChildren().add(showPassCheck);

        // Aggiunta delle azioni ai bottoni
        loginBtn.setOnAction(e -> {
            errorLabel.setVisible(false); // Pulisce l'errore al nuovo tentativo
            errorLabel.setManaged(false);
            onLoginClick.run();
        });

        registerLink.setOnAction(e -> onRegisterClick.run());

        // 🌟 AGGIUNTO: Forziamo l'uso del nostro CSS per l'effetto hover e click!
        // Rimuoviamo anche il bordo di focus (il fastidioso tratteggio di JavaFX)
        registerLink.getStyleClass().add("hyperlink");
        registerLink.setStyle("-fx-border-color: transparent; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;");


        inputGroup.getChildren().addAll(
                emailField,
                passwordContainer,
                checkContainer,
                errorLabel, // Compare qui in mezzo se c'è un errore
                loginBtn
        );

        Label questionLabel = new Label("Non hai ancora un account?");
        questionLabel.setStyle("-fx-text-fill: white; -fx-opacity: 0.8;");

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(titleLabel, inputGroup, questionLabel, registerLink);

        return root;
    }

    // NUOVO: Metodo che il Controller chiama se il login fallisce
    public void setError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true); // Fa spazio nel layout per far vedere la scritta
    }
}