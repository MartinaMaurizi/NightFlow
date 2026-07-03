package it.ispwproject.nightflow.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class DashboardOrganizerGUIView {

    public final Button logoutBtn = new Button("Log out");
    public final Button createEventBtn = new Button("➕ Crea Evento");
    public final Button modifyEventBtn = new Button("✏️ Modifica Evento");
    public final Button manageListBtn = new Button("📋 Gestisci Lista");

    public BorderPane buildRoot(Runnable onLogout, Runnable onCreate, Runnable onModify, Runnable onManage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #ede7f6;"); // Sfondo lilla chiaro

        // Navbar specifica per l'organizzatore
        root.setTop(buildNavbar(onLogout));

        // Contenuto Centrale
        VBox mainContent = new VBox(40);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPadding(new Insets(50));

        Label welcomeLabel = new Label("Benvenuto, Organizzatore!");
        welcomeLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #5e17eb;");

        Label subTitle = new Label("Cosa vuoi fare oggi?");
        subTitle.setStyle("-fx-font-size: 18px; -fx-text-fill: #333333;");

        // Contenitore per le 3 grandi schede (Cards)
        HBox cardsContainer = new HBox(40);
        cardsContainer.setAlignment(Pos.CENTER);

        // Stile base e stile hover per i bottoni
        String cardStyle = "-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5); -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #651fff; -fx-cursor: hand;";
        String cardHoverStyle = "-fx-background-color: #f3e8ff; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(101,31,255,0.3), 10, 0, 0, 5); -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #651fff; -fx-cursor: hand;";

        setupCardButton(createEventBtn, cardStyle, cardHoverStyle, onCreate);
        setupCardButton(modifyEventBtn, cardStyle, cardHoverStyle, onModify);
        setupCardButton(manageListBtn, cardStyle, cardHoverStyle, onManage);

        cardsContainer.getChildren().addAll(createEventBtn, modifyEventBtn, manageListBtn);
        mainContent.getChildren().addAll(welcomeLabel, subTitle, cardsContainer);

        root.setCenter(mainContent);
        return root;
    }

    private void setupCardButton(Button btn, String baseStyle, String hoverStyle, Runnable action) {
        btn.setPrefSize(250, 180); // Bottoni grandi e quadrati
        btn.setStyle(baseStyle);
        // Effetti di illuminazione al passaggio del mouse
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
        btn.setOnAction(e -> action.run());
    }

    private BorderPane buildNavbar(Runnable onLogout) {
        BorderPane nav = new BorderPane();
        nav.setPadding(new Insets(15, 30, 15, 30));
        nav.setStyle("-fx-background-color: white; -fx-border-color: #651fff; -fx-border-width: 0 0 2 0;");

        // Logo leggermente diverso per far capire che siamo nell'area business
        Label logo = new Label("NightFlow - Area Organizer");
        logo.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #b39eff; -fx-font-family: 'Brush Script MT', cursive;");
        nav.setLeft(logo);
        BorderPane.setAlignment(logo, Pos.CENTER_LEFT);

        logoutBtn.setPrefWidth(100);
        logoutBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 5 15; -fx-font-size: 12px; -fx-font-weight: bold; -fx-cursor: hand;");
        logoutBtn.setOnAction(e -> onLogout.run());

        nav.setRight(logoutBtn);
        BorderPane.setAlignment(logoutBtn, Pos.CENTER_RIGHT);

        return nav;
    }
}