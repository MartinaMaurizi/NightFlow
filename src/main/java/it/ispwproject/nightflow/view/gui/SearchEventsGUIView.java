package it.ispwproject.nightflow.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import java.util.List;
import java.util.function.Consumer;

public class SearchEventsGUIView {

    public final Button backBtn = new Button("<");
    public final Button logoutBtn = new Button("Log out");
    public final TextField searchField = new TextField();
    public Button profileBtn = new Button();
    public Button homeBtn = new Button();
    // Dati simulati per la ricerca
    private final List<String[]> allVenues = List.of(
            new String[]{"Jolie Club", "/locali/jolieclub.png"},
            new String[]{"Jerò restaurant", "/locali/jerorestaurant.png"},
            new String[]{"Satyrus", "/locali/satyrus.png"},
            new String[]{"The sanctuary eco retreat", "/locali/sanctuary.png"},
            new String[]{"Amazonia", "/locali/amazonia.png"}
    );

    private VBox historyList;

    public BorderPane buildRoot(Runnable onBack, Runnable onLogout, Consumer<String> onVenueClick) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #ede7f6;");

        // 1. NAVBAR VIOLA (Ora con le stesse misure della Dashboard)
        root.setTop(buildPurpleNavbar(onBack, onLogout));

        // 2. FOGLIO BIANCO
        VBox whiteSheet = new VBox(20);
        whiteSheet.setStyle("-fx-background-color: white; -fx-padding: 40;");
        whiteSheet.setMaxWidth(700);

        Label title = new Label("Trova Eventi");
        title.setStyle("-fx-font-size: 18px; -fx-text-fill: #5e17eb; -fx-font-weight: bold;");

        Label subtitle = new Label("Ultime Ricerche");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");

        historyList = new VBox(10);
        populateList("", onVenueClick);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            populateList(newValue, onVenueClick);
        });

        whiteSheet.getChildren().addAll(title, subtitle, historyList);

        // 3. TRUCCO PER LO SCROLL A DESTRA
        // Invece di ingabbiare lo scroll, creiamo un VBox che fa da "sfondo lilla" e centra il foglio bianco
        VBox centerWrapper = new VBox(whiteSheet);
        centerWrapper.setAlignment(Pos.TOP_CENTER);
        centerWrapper.setPadding(new Insets(30, 0, 30, 0)); // Spazio sopra e sotto il foglio bianco

        // Mettiamo il centerWrapper nello ScrollPane
        ScrollPane scrollPane = new ScrollPane(centerWrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.getStyleClass().add("transparent-scroll");

        // Mettendo lo ScrollPane DIRETTAMENTE al centro del root, la barra sarà attaccata al bordo destro dello schermo!
        root.setCenter(scrollPane);

        return root;
    }

    private void populateList(String filter, Consumer<String> onVenueClick) {
        historyList.getChildren().clear();
        for (String[] venue : allVenues) {
            if (venue[0].toLowerCase().contains(filter.toLowerCase())) {
                historyList.getChildren().add(createHistoryRow(venue[0], venue[1], onVenueClick));
            }
        }
    }

    private BorderPane buildPurpleNavbar(Runnable onBack, Runnable onLogout) {
        BorderPane nav = new BorderPane();
        // Ho ridotto il margine sinistro (ultimo numero a 15) per avvicinarlo al bordo
        nav.setPadding(new Insets(15, 30, 15, 15));
        nav.setStyle("-fx-background-color: #651fff;");

        // BLOCCO SINISTRA: Spaziatura ridotta tra freccia e logo (5px)
        HBox leftBox = new HBox(5);
        leftBox.setAlignment(Pos.CENTER_LEFT);

        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0;");
        backBtn.setOnAction(e -> onBack.run());

        Label logo = new Label("NightFlow");
        // Aggiunto il font corsivo elegante, dimensione leggermente aumentata per proporzione
        logo.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Brush Script MT', cursive;");
        logo.setMinWidth(Region.USE_PREF_SIZE);

        leftBox.getChildren().addAll(backBtn, logo);
        nav.setLeft(leftBox);
        BorderPane.setAlignment(leftBox, Pos.CENTER_LEFT);

        // BLOCCO CENTRO: Barra di ricerca
        HBox searchContainer = new HBox(8);
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.setPrefWidth(350);
        searchContainer.setMaxWidth(350);
        searchContainer.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: white; -fx-padding: 4 15;");

        try {
            ImageView searchIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/cerca.png")));
            searchIcon.setFitWidth(16); searchIcon.setFitHeight(16);
            searchContainer.getChildren().add(searchIcon);
        } catch (Exception e) {}

        searchField.setPromptText("Cerca");
        searchField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-prompt-text-fill: #5e17eb; -fx-text-fill: #5e17eb;");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchContainer.getChildren().add(searchField);

        nav.setCenter(searchContainer);

        // BLOCCO DESTRA
        HBox rightBox = new HBox(15);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        profileBtn = createIconButton("/icons/profileButton.png");
        homeBtn = createIconButton("/icons/homeButton.png");

        logoutBtn.setText("Log out");
        logoutBtn.setPrefWidth(100);
        logoutBtn.setMinWidth(100);
        logoutBtn.setMaxWidth(100);
        logoutBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 5 15; -fx-font-size: 12px; -fx-font-weight: bold; -fx-cursor: hand;");
        logoutBtn.setOnAction(e -> onLogout.run());

        rightBox.getChildren().addAll(profileBtn, homeBtn, logoutBtn);
        nav.setRight(rightBox);
        BorderPane.setAlignment(rightBox, Pos.CENTER_RIGHT);

        return nav;
    }

    private HBox createHistoryRow(String nomeLocale, String imagePath, Consumer<String> onClick) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 10, 10, 10));
        row.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-cursor: hand;");

        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #f3e8ff; -fx-background-radius: 10; -fx-cursor: hand;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-cursor: hand;"));
        row.setOnMouseClicked(e -> onClick.accept(nomeLocale));

        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(50, 50);
        imageContainer.setStyle("-fx-background-color: #dddddd; -fx-background-radius: 25;");

        try {
            var stream = getClass().getResourceAsStream(imagePath);
            if (stream != null) {
                ImageView imgView = new ImageView(new Image(stream));
                imgView.setFitWidth(50); imgView.setFitHeight(50);
                imgView.setClip(new Circle(25, 25, 25));
                imageContainer.getChildren().add(imgView);
            }
        } catch (Exception e) { }

        Label nameLbl = new Label(nomeLocale);
        nameLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");

        row.getChildren().addAll(imageContainer, nameLbl);
        return row;
    }

    private Button createIconButton(String path) {
        Button btn = new Button();
        try {
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(path)));
            icon.setFitHeight(20); icon.setFitWidth(20);
            btn.setGraphic(icon);
        } catch (Exception e) { btn.setText("?"); }
        btn.setPrefWidth(35);
        btn.setMinWidth(35);
        btn.setMaxWidth(35);
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0;");
        return btn;
    }
}