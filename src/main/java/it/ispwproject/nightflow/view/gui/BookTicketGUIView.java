package it.ispwproject.nightflow.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import java.util.function.Consumer;

public class BookTicketGUIView {

    public final Button backBtn = new Button("<");
    public final Button logoutBtn = new Button("Log out");
    public final Button checkoutBtn = new Button("Check Out");
    public Button profileBtn = new Button();
    public Button homeBtn = new Button();
    // Definiamo un gruppo logico per far selezionare un solo biglietto alla volta
    private final ToggleGroup ticketGroup = new ToggleGroup();

    public BorderPane buildRoot(Runnable onBack, Runnable onLogout, java.util.function.Consumer<String> onCheckout, it.ispwproject.nightflow.bean.EventBean event) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #ede7f6;");
        root.setTop(buildWhiteNavbar(onBack, onLogout));

        VBox mainContent = new VBox(20);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setMaxWidth(700);

        // 🌟 Titolo e Immagine presi dinamicamente dall'evento
        Label title = new Label(event.getName());
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: black;");
        StackPane imageContainer = new StackPane();
        imageContainer.setMaxSize(500, 250);
        try {
            // ECCO LA RIGA MODIFICATA CHE GESTISCE LE LETTERE ACCENTATE:
            String imagePath = "/locali/" + event.getLocalName().toLowerCase().replace(" ", "").replace("ò", "o") + ".png";

            ImageView imgView = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
            imgView.setFitWidth(500); imgView.setFitHeight(250); imgView.setPreserveRatio(false);
            Rectangle clip = new Rectangle(500, 250); clip.setArcWidth(40); clip.setArcHeight(40);
            imgView.setClip(clip);
            imageContainer.getChildren().add(imgView);
        } catch (Exception e) { imageContainer.setStyle("-fx-background-color: #4a00e0; -fx-background-radius: 20;"); }

        VBox ticketSection = new VBox(15);
        ticketSection.setMaxWidth(500);
        ticketSection.setAlignment(Pos.CENTER_LEFT);

        Label listeLbl = new Label("☑ Liste");
        listeLbl.setStyle("-fx-border-color: black; -fx-padding: 5 15; -fx-background-color: white; -fx-text-fill: black;");
        Label ingressoLbl = new Label("Ingresso");
        ingressoLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: black;");

        ticketSection.getChildren().addAll(listeLbl, ingressoLbl,
                createTicketRow("22:00-00:30", "Con drink", "20€"),
                createTicketRow("22:00-00:30", "Senza drink", "15€"),
                createTicketRow("22:00-00:30", "Tavolo VIP", "100€")
        );

        checkoutBtn.setStyle("-fx-background-color: #651fff; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 16px; -fx-padding: 10 40; -fx-cursor: hand;");

        // 🌟 AZIONE CHECKOUT: Legge il biglietto selezionato e lo passa avanti!
        checkoutBtn.setOnAction(e -> {
            RadioButton selected = (RadioButton) ticketGroup.getSelectedToggle();
            String ticketInfo = (selected != null) ? selected.getUserData().toString() : "Ingresso Base";
            onCheckout.accept(ticketInfo);
        });

        mainContent.getChildren().addAll(title, imageContainer, ticketSection, checkoutBtn);

        // 3. TRUCCO PER LO SCROLL A DESTRA (Stessa logica della pagina Ricerca)
        VBox centerWrapper = new VBox(mainContent);
        centerWrapper.setAlignment(Pos.TOP_CENTER);
        centerWrapper.setPadding(new Insets(30, 0, 50, 0));

        ScrollPane scrollPane = new ScrollPane(centerWrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.getStyleClass().add("transparent-scroll"); // Applica la barra viola

        root.setCenter(scrollPane);
        return root;
    }

    private BorderPane buildWhiteNavbar(Runnable onBack, Runnable onLogout) {
        BorderPane nav = new BorderPane();
        // Stesse misure di SearchEventsGUIView
        nav.setPadding(new Insets(15, 30, 15, 15));
        nav.setStyle("-fx-background-color: white; -fx-border-color: #651fff; -fx-border-width: 0 0 2 0;");

        // BLOCCO SINISTRA
        HBox leftBox = new HBox(5);
        leftBox.setAlignment(Pos.CENTER_LEFT);

        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #651fff; -fx-font-size: 24px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0;");
        backBtn.setOnAction(e -> onBack.run());

        Label logo = new Label("NightFlow");
        logo.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #b39eff; -fx-font-family: 'Brush Script MT', cursive;");
        logo.setMinWidth(Region.USE_PREF_SIZE);

        leftBox.getChildren().addAll(backBtn, logo);
        nav.setLeft(leftBox);

        // BLOCCO DESTRA (Icone e Logout sistemati)
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

        return nav;
    }

    private HBox createTicketRow(String time, String desc, String price) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: white; -fx-padding: 10 20;");

        Label timeLbl = new Label(time); timeLbl.setPrefWidth(100); timeLbl.setStyle("-fx-text-fill: black;");
        Label descLbl = new Label("| " + desc); HBox.setHgrow(descLbl, Priority.ALWAYS); descLbl.setMaxWidth(Double.MAX_VALUE); descLbl.setStyle("-fx-text-fill: black;");
        Label priceLbl = new Label(price); priceLbl.setStyle("-fx-text-fill: black;");

        // 🌟 Usiamo RadioButton così scegli un solo biglietto!
        RadioButton radio = new RadioButton();
        radio.setToggleGroup(ticketGroup);
        radio.setUserData(desc + " (" + price + ")"); // Salviamo il testo da mandare al checkout!
        radio.setStyle("-fx-cursor: hand;");

        row.getChildren().addAll(timeLbl, descLbl, priceLbl, radio);
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