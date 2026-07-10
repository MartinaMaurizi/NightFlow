package it.ispwproject.nightflow.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

// 🌟 1. ORA ESTENDE PageGUIView PER PRENDERE LO STESSO SFONDO E NAVBAR!
public class CreateEventGUIView extends PageGUIView {

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

    public final Label errorLabel = buildErrorLabel();

    public BorderPane buildRoot(Runnable onBack, Runnable onLogout, Runnable onProfile, Runnable onHome, Runnable onCreate) {

        BorderPane root = buildShell("Crea un Nuovo Evento", onBack, onLogout, onProfile, onHome);

        VBox mainContent = new VBox(20);
        // 🌟 1. ORA È COMPLETAMENTE CENTRATO IN VERTICALE
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setMaxWidth(580);
        mainContent.setPadding(new Insets(40, 20, 50, 20));

        String fieldStyle = "-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 10 15; -fx-prompt-text-fill: #888888; -fx-text-fill: black; -fx-font-size: 14px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);";

        // 🌟 2. ABBIAMO FISSATO LA LARGHEZZA PER UN ALLINEAMENTO A BLOCCO PERFETTO
        nameFld.setPromptText("Nome Evento (es. Latin Saturday)");
        nameFld.setStyle(fieldStyle);
        nameFld.setMaxWidth(580);

        venueFld.setPromptText("Nome del Locale (es. Jolie Club)");
        venueFld.setStyle(fieldStyle);
        venueFld.setMaxWidth(580);

        locationFld.setPromptText("Indirizzo completo (es. Via Roma 1)");
        locationFld.setStyle(fieldStyle);
        locationFld.setMaxWidth(580);

        HBox dateTimeBox = new HBox(20);
        dateTimeBox.setAlignment(Pos.CENTER);

        datePicker.setPromptText("Data Evento");
        datePicker.setStyle("-fx-font-size: 14px;");
        datePicker.setPrefWidth(280);

        timeFld.setPromptText("Ora (es. 22:30-00:30)");
        timeFld.setStyle(fieldStyle);
        timeFld.setPrefWidth(280);

        dateTimeBox.getChildren().addAll(datePicker, timeFld);

        HBox numbersBox = new HBox(20);
        numbersBox.setAlignment(Pos.CENTER);

        priceFld.setPromptText("Prezzo Base (€)");
        priceFld.setStyle(fieldStyle);
        priceFld.setPrefWidth(280);

        capacityFld.setPromptText("Capacità max (es. 50)");
        capacityFld.setStyle(fieldStyle);
        capacityFld.setPrefWidth(280);

        numbersBox.getChildren().addAll(priceFld, capacityFld);

        descArea.setPromptText("Descrizione dell'evento...");
        descArea.setPrefRowCount(4);
        descArea.setStyle("-fx-control-inner-background: white; -fx-background-radius: 10; -fx-prompt-text-fill: #888888; -fx-text-fill: black; -fx-font-size: 14px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        descArea.setMaxWidth(580);

        createBtn.getStyleClass().add("btn-viola-large");
        createBtn.setPrefWidth(250);
        createBtn.setOnAction(e -> onCreate.run());

        mainContent.getChildren().addAll(
                nameFld, venueFld, locationFld,
                dateTimeBox, numbersBox, descArea,
                createBtn, errorLabel
        );

        // 🌟 3. USIAMO STACKPANE PER FORZARE IL CENTRAGGIO TOTALE
        StackPane centerWrapper = new StackPane(mainContent);
        centerWrapper.setAlignment(Pos.CENTER);

        root.setCenter(transparentScroll(centerWrapper));

        return root;
    }
}