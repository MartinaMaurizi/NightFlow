package it.ispwproject.nightflow.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ProfileGUIView {

    // 🌟 ECCO I BOTTONI PUBBLICI (inclusi quelli centrali del profilo!)
    public final Button backBtn = new Button("<");
    public final Button logoutBtn = new Button("Log out");
    public Button profileBtn = new Button();
    public Button homeBtn = new Button();

    // I bottoni specifici delle azioni del profilo
    public final Button myBookingsBtn = new Button("Le mie prenotazioni");
    public final Button myEventsBtn = new Button("Le mie serate");
    public final Button editProfileBtn = new Button("Modifica profilo");
    public final Button changePwdBtn = new Button("Cambia password");

    public BorderPane buildRoot(Runnable onBack, Runnable onLogout, Runnable onProfile) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #ede7f6;");

        root.setTop(buildNavbar(onBack, onLogout, onProfile));

        VBox centerColumn = new VBox(20);
        centerColumn.setAlignment(Pos.TOP_CENTER);
        centerColumn.setMaxWidth(700);
        centerColumn.setPrefWidth(700);
        centerColumn.setStyle("-fx-border-color: #651fff; -fx-border-width: 0 1.5 0 1.5; -fx-padding: 30 0;");

        Label title = new Label("Profilo");
        title.setStyle("-fx-font-size: 26px; -fx-text-fill: #b39eff; -fx-font-family: 'Arial';");

        StackPane avatarContainer = new StackPane();
        try {
            ImageView userIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/profileUser.png")));
            userIcon.setFitWidth(140);
            userIcon.setFitHeight(140);

            Circle clip = new Circle(70, 70, 70);
            userIcon.setClip(clip);

            Circle borderCircle = new Circle(72, Color.TRANSPARENT);
            borderCircle.setStroke(Color.web("#651fff"));
            borderCircle.setStrokeWidth(3);

            avatarContainer.getChildren().addAll(borderCircle, userIcon);
        } catch (Exception e) {
            System.err.println("Immagine /icons/profileUser.png non trovata!");
        }

        VBox userInfo = new VBox(8);
        userInfo.setAlignment(Pos.CENTER);
        Label name = new Label("ANNA BIANCHI");
        name.setStyle("-fx-font-size: 14px; -fx-text-fill: black; -fx-font-weight: bold;");
        Label dob = new Label("13/01/2003");
        dob.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        Label city = new Label("Roma, Italia");
        city.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        Label email = new Label("annabianchi@gmail.com");
        email.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        userInfo.getChildren().addAll(name, dob, city, email);

        // 🌟 APPLICO LO STILE AI BOTTONI GIA' CREATI IN ALTO
        styleProfileButton(editProfileBtn);
        styleProfileButton(changePwdBtn);
        styleProfileButton(myBookingsBtn);
        styleProfileButton(myEventsBtn);

        VBox actionButtons = new VBox(15);
        actionButtons.setAlignment(Pos.CENTER);
        actionButtons.setPadding(new Insets(20, 0, 0, 0));

        // Aggiungo i bottoni pubblici alla grafica
        actionButtons.getChildren().addAll(editProfileBtn, changePwdBtn, myBookingsBtn, myEventsBtn);

        centerColumn.getChildren().addAll(title, avatarContainer, userInfo, actionButtons);

        StackPane scrollContent = new StackPane(centerColumn);
        scrollContent.setPadding(new Insets(20, 0, 50, 0));

        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.getStyleClass().add("transparent-scroll");

        root.setCenter(scrollPane);
        return root;
    }

    private BorderPane buildNavbar(Runnable onBack, Runnable onLogout, Runnable onProfile) {
        BorderPane nav = new BorderPane();
        nav.setPadding(new Insets(15, 30, 15, 0));
        nav.setStyle("-fx-background-color: #ede7f6; -fx-border-color: #651fff; -fx-border-width: 0 0 2 0;");

        HBox leftBox = new HBox(0);
        leftBox.setAlignment(Pos.CENTER_LEFT);

        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #651fff; -fx-font-size: 24px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0 4 0 10;");
        backBtn.setOnAction(e -> onBack.run());

        Label logo = new Label("NightFlow");
        logo.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #b39eff; -fx-font-family: 'Brush Script MT', cursive;");
        logo.setMinWidth(Region.USE_PREF_SIZE);

        leftBox.getChildren().addAll(backBtn, logo);
        nav.setLeft(leftBox);

        HBox rightBox = new HBox(15);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        profileBtn = createIconButton("/icons/profileButton.png");
        profileBtn.setOnAction(e -> onProfile.run());

        homeBtn = createIconButton("/icons/homeButton.png");

        logoutBtn.setText("Log out");
        logoutBtn.setPrefWidth(100);
        logoutBtn.setMinWidth(100);
        logoutBtn.setMaxWidth(100);
        logoutBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 5 15; -fx-font-size: 12px; -fx-font-weight: bold; -fx-cursor: hand;");
        logoutBtn.setOnAction(e -> onLogout.run());

        rightBox.getChildren().addAll(profileBtn, homeBtn, logoutBtn);
        nav.setRight(rightBox);

        return nav;
    }

    // Cambiato nome del metodo: non "crea" più il bottone, gli dà solo lo stile
    private void styleProfileButton(Button btn) {
        btn.setPrefWidth(220);
        // Usa una classe CSS invece dello stile inline fisso, per evitare conflitti
        btn.getStyleClass().add("profile-action-button");
    }

    private Button createIconButton(String path) {
        Button btn = new Button();
        try {
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(path)));
            icon.setFitHeight(20); icon.setFitWidth(20);
            btn.setGraphic(icon);
        } catch (Exception e) { btn.setText("?"); }
        btn.setPrefWidth(35); btn.setMinWidth(35); btn.setMaxWidth(35);
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0;");
        return btn;
    }
}