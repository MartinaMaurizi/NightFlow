package it.ispwproject.nightflow.view.gui;

import it.ispwproject.nightflow.bean.EventBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.util.function.Consumer;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class DashboardClientGUIView extends DashboardGUIView {

    public final Button logoutBtn = new Button("Log out");
    // --- IL CAMPO DI RICERCA ORA È PUBBLICO PER ESSERE CLICCATO ---
    public final TextField searchField = new TextField();
    public Button profileBtn = new Button();
    public Button homeBtn = new Button();
    private final VBox leftColumn = new VBox(50);
    private final VBox rightColumn = new VBox(50);

    // Sostituisci l'inizio del buildRoot per accettare anche onProfile
    public BorderPane buildRoot(Runnable onLogout, Runnable onProfile) {
        BorderPane root = new BorderPane();
        root.setId("Dashboard-Root");

        // Passiamo l'azione della navigazione alla navbar
        root.setTop(buildCustomNavbar(onLogout, onProfile));


        // 2. Container eventi
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30, 50, 30, 50));

        Label titleLbl = new Label("Prossimi eventi");
        titleLbl.setStyle("-fx-font-size: 26px; -fx-text-fill: #1a1a1a; -fx-font-weight: bold;");
        mainContainer.getChildren().add(titleLbl);

        // 3. Colonne
        HBox columnsBox = new HBox(60);
        columnsBox.setAlignment(Pos.TOP_CENTER);

        Region centerLine = new Region();
        centerLine.setPrefWidth(2);
        centerLine.setStyle("-fx-background-color: #651fff;");

        columnsBox.getChildren().addAll(leftColumn, centerLine, rightColumn);
        mainContainer.getChildren().add(columnsBox);

        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.getStyleClass().add("transparent-scroll");

        root.setCenter(scrollPane);
        return root;
    }

    private BorderPane buildCustomNavbar(Runnable onLogout, Runnable onProfile) {
        BorderPane nav = new BorderPane();

        nav.setPadding(new Insets(15, 30, 15, 30));
        nav.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        // 1. SINISTRA: Logo NightFlow
        Label logo = new Label("NightFlow");
        logo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #651fff; -fx-font-family: 'Brush Script MT', cursive;");
        logo.setMinWidth(Region.USE_PREF_SIZE);
        nav.setLeft(logo);
        BorderPane.setAlignment(logo, Pos.CENTER_LEFT);

        // 2. CENTRO: Barra di ricerca (Contenitore bianco con Icona + Campo di testo)
        HBox searchContainer = new HBox(8); // 8px di spazio tra icona e testo
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.setPrefWidth(350);
        searchContainer.setMaxWidth(350);
        // Sfondo BIANCO e bordo rotondo
        searchContainer.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #651fff; -fx-padding: 4 15;");

        // Caricamento dell'icona cerca.png
        ImageView searchIcon = new ImageView();
        try {
            searchIcon.setImage(new Image(getClass().getResourceAsStream("/icons/cerca.png")));
            searchIcon.setFitHeight(16);
            searchIcon.setFitWidth(16);
        } catch (Exception e) {
            System.err.println("Icona cerca non trovata");
        }

        // Il searchField (che ora è definito in alto) viene configurato
        searchField.setPromptText("Cerca");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: #5e17eb; -fx-prompt-text-fill: #5e17eb;");

        searchContainer.getChildren().addAll(searchIcon, searchField);
        nav.setCenter(searchContainer);

        // 3. DESTRA: Icone + Logout
        HBox rightBox = new HBox(15);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        profileBtn = createIconButton("/icons/profileButton.png");
    // 🌟 COLLEGIAMO DIRETTAMENTE IL CLIC QUI DENTRO!
        profileBtn.setOnAction(e -> onProfile.run());

        homeBtn = createIconButton("/icons/homeButton.png");
    // ... (tasto logout)
        logoutBtn.setText("Log out");
        logoutBtn.setPrefWidth(100);
        logoutBtn.setMinWidth(100);
        logoutBtn.setMaxWidth(100);
        logoutBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 20; -fx-cursor: hand; -fx-font-weight: bold;");
        logoutBtn.setOnAction(e -> onLogout.run());

        rightBox.getChildren().addAll(profileBtn, homeBtn, logoutBtn);
        nav.setRight(rightBox);
        BorderPane.setAlignment(rightBox, Pos.CENTER_RIGHT);

        return nav;
    }

    private Button createIconButton(String path) {
        Button btn = new Button();
        try {
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(path)));
            icon.setFitHeight(20);
            icon.setFitWidth(20);
            btn.setGraphic(icon);
        } catch (Exception e) {
            btn.setText("?");
        }
        btn.setPrefWidth(35);
        btn.setMinWidth(35);
        btn.setMaxWidth(35);
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0;");
        return btn;
    }


    public void updateEventList(List<EventBean> events, Consumer<EventBean> onBookClick) {
        leftColumn.getChildren().clear();
        rightColumn.getChildren().clear();
        DateTimeFormatter dayFmt = DateTimeFormatter.ofPattern("dd");
        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("MMM").withLocale(Locale.ITALIAN);

        boolean isLeft = true;
        for (EventBean event : events) {
            // Passiamo l'azione alla card
            VBox card = createEventCard(event, dayFmt, monthFmt, onBookClick);
            if (isLeft) leftColumn.getChildren().add(card);
            else rightColumn.getChildren().add(card);
            isLeft = !isLeft;
        }
    }

    private VBox createEventCard(EventBean event, DateTimeFormatter dayFmt, DateTimeFormatter monthFmt, Consumer<EventBean> onBookClick) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.TOP_CENTER);
        card.setMaxWidth(350);

        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(350, 160);
        imageContainer.setStyle("-fx-background-color: #4a00e0;");

        try {
            var stream = getClass().getResourceAsStream(getImagePathForEvent(event.getName()));
            if (stream != null) {
                ImageView imgView = new ImageView(new Image(stream));
                imgView.setFitWidth(350); imgView.setFitHeight(160);
                imgView.setPreserveRatio(false);
                imageContainer.getChildren().add(imgView);
            }
        } catch (Exception e) { e.printStackTrace(); }

        Rectangle clip = new Rectangle(350, 160);
        clip.setArcWidth(40); clip.setArcHeight(40);
        imageContainer.setClip(clip);

        HBox infoRow = new HBox(20);
        infoRow.setAlignment(Pos.CENTER);

        VBox dateBox = new VBox(0);
        dateBox.setAlignment(Pos.CENTER);
        dateBox.getChildren().addAll(
                new Label(event.getDateTime().format(dayFmt)) {{ setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: black;"); }},
                new Label(event.getDateTime().format(monthFmt).toUpperCase()) {{ setStyle("-fx-font-size: 18px; -fx-text-fill: black;"); }}
        );

        VBox detailsBox = new VBox(5);
        detailsBox.setAlignment(Pos.CENTER_LEFT);
        detailsBox.getChildren().addAll(
                new Label(event.getName()) {{ setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;"); }},
                new Label(event.getLocation()) {{ setStyle("-fx-font-size: 12px; -fx-text-fill: #333333;"); setWrapText(true); setMaxWidth(180); }}
        );

        infoRow.getChildren().addAll(dateBox, new Region() {{ setPrefWidth(2); setMinHeight(50); setStyle("-fx-background-color: black;"); }}, detailsBox);

        Button bookBtn = new Button("PRENOTA BIGLIETTO");
        bookBtn.setPrefWidth(250);
        bookBtn.setStyle("-fx-background-color: #651fff; -fx-text-fill: white; -fx-background-radius: 20; -fx-cursor: hand;");

        // 🌟 ECCO IL COLLEGAMENTO: Quando clicco, chiamo la funzione passata dal Controller!
        bookBtn.setOnAction(e -> onBookClick.accept(event));

        Button editBtn = new Button("MODIFICA PRENOTAZIONE");
        editBtn.setPrefWidth(250);
        editBtn.setStyle("-fx-background-color: #651fff; -fx-text-fill: white; -fx-background-radius: 20; -fx-cursor: hand;");

        card.getChildren().addAll(imageContainer, infoRow, new Label("Ingresso in lista") {{ setStyle("-fx-text-fill: black;"); }}, bookBtn, editBtn);
        return card;
    }

    private String getImagePathForEvent(String name) {
        String n = name.toLowerCase();
        if (n.contains("jolie")) return "/locali/jolieclub.png";
        if (n.contains("jer")) return "/locali/jerorestaurant.png";
        return "/locali/default.png";
    }
}