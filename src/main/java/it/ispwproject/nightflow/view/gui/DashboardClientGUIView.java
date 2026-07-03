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
    public final TextField searchField = new TextField();
    public Button profileBtn = new Button();
    public Button homeBtn = new Button();
    private final VBox leftColumn = new VBox(50);
    private final VBox rightColumn = new VBox(50);

    public BorderPane buildRoot(Runnable onLogout, Runnable onProfile) {
        BorderPane root = new BorderPane();
        root.setId("Dashboard-Root");

        root.setTop(buildCustomNavbar(onLogout, onProfile));

        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30, 50, 30, 50));

        Label titleLbl = new Label("Prossimi eventi");
        titleLbl.setStyle("-fx-font-size: 26px; -fx-text-fill: #1a1a1a; -fx-font-weight: bold;");
        mainContainer.getChildren().add(titleLbl);

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

        Label logo = new Label("NightFlow");
        logo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #651fff; -fx-font-family: 'Brush Script MT', cursive;");
        logo.setMinWidth(Region.USE_PREF_SIZE);
        nav.setLeft(logo);
        BorderPane.setAlignment(logo, Pos.CENTER_LEFT);

        HBox searchContainer = new HBox(8);
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.setPrefWidth(350);
        searchContainer.setMaxWidth(350);
        searchContainer.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #651fff; -fx-padding: 4 15;");

        ImageView searchIcon = new ImageView();
        try {
            searchIcon.setImage(new Image(getClass().getResourceAsStream("/icons/cerca.png")));
            searchIcon.setFitHeight(16);
            searchIcon.setFitWidth(16);
        } catch (Exception e) {
            // Blocco vuoto per non sporcare la console
        }

        searchField.setPromptText("Cerca");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: #5e17eb; -fx-prompt-text-fill: #5e17eb;");

        searchContainer.getChildren().addAll(searchIcon, searchField);
        nav.setCenter(searchContainer);

        HBox rightBox = new HBox(15);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        profileBtn = createIconButton("/icons/profileButton.png");
        profileBtn.setOnAction(e -> onProfile.run());

        homeBtn = createIconButton("/icons/homeButton.png");

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
        } catch (Exception e) {
            // Nessun printStackTrace, l'immagine non viene caricata ma l'app non crasha
        }

        Rectangle clip = new Rectangle(350, 160);
        clip.setArcWidth(40); clip.setArcHeight(40);
        imageContainer.setClip(clip);

        HBox infoRow = new HBox(20);
        infoRow.setAlignment(Pos.CENTER);

        VBox dateBox = new VBox(0);
        dateBox.setAlignment(Pos.CENTER);

        // CORREZIONE 1: Etichette data senza doppie graffe
        Label dayLabel = new Label(event.getDateTime().format(dayFmt));
        dayLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: black;");

        Label monthLabel = new Label(event.getDateTime().format(monthFmt).toUpperCase());
        monthLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");

        dateBox.getChildren().addAll(dayLabel, monthLabel);

        VBox detailsBox = new VBox(5);
        detailsBox.setAlignment(Pos.CENTER_LEFT);

        // CORREZIONE 2: Etichette dettagli senza doppie graffe
        Label nameLabel = new Label(event.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;");

        Label locationLabel = new Label(event.getLocation());
        locationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #333333;");
        locationLabel.setWrapText(true);
        locationLabel.setMaxWidth(180);

        detailsBox.getChildren().addAll(nameLabel, locationLabel);

        // CORREZIONE 3: Region separatore senza doppie graffe
        Region separator = new Region();
        separator.setPrefWidth(2);
        separator.setMinHeight(50);
        separator.setStyle("-fx-background-color: black;");

        infoRow.getChildren().addAll(dateBox, separator, detailsBox);

        Button bookBtn = new Button("PRENOTA BIGLIETTO");
        bookBtn.setPrefWidth(250);
        bookBtn.setStyle("-fx-background-color: #651fff; -fx-text-fill: white; -fx-background-radius: 20; -fx-cursor: hand;");

        bookBtn.setOnAction(e -> onBookClick.accept(event));

        Button editBtn = new Button("MODIFICA PRENOTAZIONE");
        editBtn.setPrefWidth(250);
        editBtn.setStyle("-fx-background-color: #651fff; -fx-text-fill: white; -fx-background-radius: 20; -fx-cursor: hand;");

        // CORREZIONE 4: Etichetta lista senza doppie graffe
        Label listLabel = new Label("Ingresso in lista");
        listLabel.setStyle("-fx-text-fill: black;");

        card.getChildren().addAll(imageContainer, infoRow, listLabel, bookBtn, editBtn);
        return card;
    }

    private String getImagePathForEvent(String name) {
        String n = name.toLowerCase();
        if (n.contains("jolie")) return "/locali/jolieclub.png";
        if (n.contains("jer")) return "/locali/jerorestaurant.png";
        return "/locali/default.png";
    }
}