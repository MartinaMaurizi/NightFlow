package it.ispwproject.nightflow.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CreateEventGUIView {

    public final Button backBtn = new Button("<");
    public final Button logoutBtn = new Button("Log out");
    public final Button createBtn = new Button("Crea Evento");

    // Campi del form
    public final TextField nameFld = new TextField();
    public final DatePicker datePicker = new DatePicker();
    public final TextField timeFld = new TextField();
    public final TextField venueFld = new TextField();
    public final TextField priceFld = new TextField();
    public final TextField capacityFld = new TextField();
    public final TextArea descArea = new TextArea();
    public final TextField locationFld = new TextField();

    public BorderPane buildRoot(Runnable onBack, Runnable onLogout, Runnable onCreate) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #ede7f6;");

        // Navbar
        root.setTop(buildNavbar(onBack, onLogout));

        // Contenuto Centrale
        VBox mainContent = new VBox(20);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setMaxWidth(600);
        mainContent.setPadding(new Insets(30, 20, 50, 20));

        Label title = new Label("Crea un Nuovo Evento");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: black;");

        // Stile per i campi di input
        String fieldStyle = "-fx-background-color: #bbaaf2; -fx-background-radius: 10; -fx-padding: 10 15; -fx-prompt-text-fill: #444444; -fx-text-fill: black; -fx-font-size: 14px;";

        nameFld.setPromptText("Nome Evento (es. Latin Saturday)");
        nameFld.setStyle(fieldStyle);

        venueFld.setPromptText("Nome del Locale (es. Jolie Club)");
        venueFld.setStyle(fieldStyle);

        // 🌟 SPOSTATO QUI IN ALTO: Configuriamo l'indirizzo
        locationFld.setPromptText("Indirizzo completo (es. Via Roma 1)");
        locationFld.setStyle(fieldStyle);

        HBox dateTimeBox = new HBox(20);
        datePicker.setPromptText("Data Evento");
        datePicker.setStyle("-fx-font-size: 14px;");
        timeFld.setPromptText("Ora (es. 22:30-00:30)");
        timeFld.setStyle(fieldStyle);
        dateTimeBox.getChildren().addAll(datePicker, timeFld);

        HBox numbersBox = new HBox(20);
        priceFld.setPromptText("Prezzo Base (€)");
        priceFld.setStyle(fieldStyle);
        capacityFld.setPromptText("Capacità max (es. 50)");
        capacityFld.setStyle(fieldStyle);
        numbersBox.getChildren().addAll(priceFld, capacityFld);

        descArea.setPromptText("Descrizione dell'evento...");
        descArea.setPrefRowCount(4);
        descArea.setStyle("-fx-control-inner-background: #bbaaf2; -fx-background-radius: 10; -fx-prompt-text-fill: #444444; -fx-text-fill: black; -fx-font-size: 14px;");

        createBtn.setStyle("-fx-background-color: #651fff; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 16px; -fx-padding: 10 40; -fx-cursor: hand;");
        createBtn.setOnAction(e -> onCreate.run());

        // 🌟 AGGIUNTO 'locationFld' QUI DENTRO PER FARLO COMPARIRE A SCHERMO
        mainContent.getChildren().addAll(title, nameFld, venueFld, locationFld, dateTimeBox, numbersBox, descArea, createBtn);

        // ScrollPane per schermi più piccoli
        ScrollPane scrollPane = new ScrollPane(new StackPane(mainContent));
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.getStyleClass().add("transparent-scroll");

        root.setCenter(scrollPane);
        return root;
    }

    private BorderPane buildNavbar(Runnable onBack, Runnable onLogout) {
        BorderPane nav = new BorderPane();
        nav.setPadding(new Insets(15, 30, 15, 0));
        nav.setStyle("-fx-background-color: #ede7f6; -fx-border-color: #651fff; -fx-border-width: 0 0 2 0;");

        HBox leftBox = new HBox(0);
        leftBox.setAlignment(Pos.CENTER_LEFT);

        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #651fff; -fx-font-size: 24px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0 4 0 10;");
        backBtn.setOnAction(e -> onBack.run());

        Label logo = new Label("NightFlow - Area Organizer");
        logo.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #b39eff; -fx-font-family: 'Brush Script MT', cursive;");
        logo.setMinWidth(Region.USE_PREF_SIZE);

        leftBox.getChildren().addAll(backBtn, logo);
        nav.setLeft(leftBox);

        logoutBtn.setText("Log out");
        logoutBtn.getStyleClass().clear(); // Pulisce lo stile grigio di default
        logoutBtn.getStyleClass().add("logout-btn"); // Applica lo stile nero che abbiamo nel CSS
        logoutBtn.setOnAction(e -> onLogout.run());

        nav.setRight(logoutBtn);

        return nav;
    }
}