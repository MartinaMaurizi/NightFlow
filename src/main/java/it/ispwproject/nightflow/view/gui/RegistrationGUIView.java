package it.ispwproject.nightflow.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class RegistrationGUIView {

    // Componenti della UI
    public final Button backBtn = new Button("< Indietro");
    public final TextField nameField = new TextField();
    public final TextField surnameField = new TextField();
    public final DatePicker dobPicker = new DatePicker();
    public final ComboBox<String> genderBox = new ComboBox<>();
    public final ComboBox<String> countryBox = new ComboBox<>();
    public final ComboBox<String> cityBox = new ComboBox<>();
    public final TextField emailField = new TextField();
    public final PasswordField passField = new PasswordField();
    public final PasswordField confirmPassField = new PasswordField();
    public final Button registerBtn = new Button("Registrami");
    public final CheckBox isOrganizerCheck = new CheckBox("Registrati come Organizzatore");
    public final Label errorLabel = new Label();

    public VBox buildRoot() {
        // 1. Setup Placeholder
        nameField.setPromptText("Nome*");
        surnameField.setPromptText("Cognome*");
        dobPicker.setPromptText("Data di nascita*");
        genderBox.setPromptText("Sesso*");
        countryBox.setPromptText("Nazione*");
        cityBox.setPromptText("Città*");
        emailField.setPromptText("Email*");
        passField.setPromptText("Password*");
        confirmPassField.setPromptText("Conferma password*");

        // 2. Popolamento delle liste statiche
        genderBox.getItems().addAll("Uomo", "Donna", "Altro");
        countryBox.getItems().addAll("Italia", "Spagna", "Francia", "Germania", "Regno Unito");

        // 3. Logica dinamica per le Città
        countryBox.setOnAction(event -> {
            String selectedCountry = countryBox.getValue();
            cityBox.getItems().clear();

            if (selectedCountry != null) {
                switch (selectedCountry) {
                    case "Italia":
                        cityBox.getItems().addAll("Roma", "Milano", "Napoli", "Torino", "Palermo");
                        break;
                    case "Spagna":
                        cityBox.getItems().addAll("Madrid", "Barcellona", "Valencia", "Siviglia");
                        break;
                    case "Francia":
                        cityBox.getItems().addAll("Parigi", "Lione", "Marsiglia", "Nizza");
                        break;
                    case "Germania":
                        cityBox.getItems().addAll("Berlino", "Monaco", "Francoforte", "Amburgo");
                        break;
                    case "Regno Unito":
                        cityBox.getItems().addAll("Londra", "Manchester", "Edimburgo", "Liverpool");
                        break;
                }
            }
        });

        // 4. Tasto Indietro
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #651fff; -fx-font-weight: bold; -fx-cursor: hand;");
        backBtn.setFocusTraversable(false); // AGGIUNGI QUESTA RIGA: Impedisce al bottone di confondersi col focus
        HBox topBar = new HBox(backBtn);
        topBar.setPadding(new Insets(10, 0, 0, 10));
        topBar.setAlignment(Pos.CENTER_LEFT);

        // 5. Titoli
        Label titleLabel = new Label("NightFlow");
        titleLabel.setId("NightFlow-Neon");

        Label subtitleLabel = new Label("Registrazione");
        subtitleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: black;");

        // 6. Simmetria dei campi
        nameField.setMaxWidth(Double.MAX_VALUE);
        surnameField.setMaxWidth(Double.MAX_VALUE);
        emailField.setMaxWidth(Double.MAX_VALUE);
        passField.setMaxWidth(Double.MAX_VALUE);
        confirmPassField.setMaxWidth(Double.MAX_VALUE);

        dobPicker.setMaxWidth(Double.MAX_VALUE);
        genderBox.setMaxWidth(Double.MAX_VALUE);
        countryBox.setMaxWidth(Double.MAX_VALUE);
        cityBox.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(dobPicker, Priority.ALWAYS);
        HBox.setHgrow(genderBox, Priority.ALWAYS);
        HBox.setHgrow(countryBox, Priority.ALWAYS);
        HBox.setHgrow(cityBox, Priority.ALWAYS);

        HBox row1 = new HBox(15, dobPicker, genderBox);
        HBox row2 = new HBox(15, countryBox, cityBox);

        // 7. Testi di aiuto per le password
        Label passHelper = new Label("Scegli una password con almeno 8 caratteri");
        passHelper.setStyle("-fx-font-size: 11px; -fx-text-fill: #333333;");
        VBox passBox = new VBox(2, passField, passHelper);

        Label confirmHelper = new Label("*campi obbligatori");
        confirmHelper.setStyle("-fx-font-size: 11px; -fx-text-fill: #651fff;");
        VBox confirmBox = new VBox(2, confirmPassField, confirmHelper);

        // 8. Contenitore Form
        VBox formGroup = new VBox(10);
        formGroup.setMaxWidth(400);
        formGroup.setAlignment(Pos.CENTER);

        formGroup.getChildren().addAll(
                titleLabel,
                subtitleLabel,
                nameField,
                surnameField,
                row1,
                row2,
                emailField,
                passBox,
                confirmBox,
                registerBtn,
                isOrganizerCheck,
                errorLabel
        );

        // 9. Centratura Form tramite StackPane
        StackPane centerWrapper = new StackPane(formGroup);
        VBox.setVgrow(centerWrapper, Priority.ALWAYS);

        // 10. Root Principale
        VBox root = new VBox();
        root.setId("Registration-Root");
        root.getChildren().addAll(topBar, centerWrapper);

        return root;
    }
}