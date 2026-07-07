package it.ispwproject.nightflow.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import java.time.format.DateTimeFormatter;

public class BookTicketGUIView {

    public final Button backBtn = new Button("<");
    public final Button logoutBtn = new Button("Log out");
    public final Button checkoutBtn = new Button("Check Out");
    public Button profileBtn = new Button();
    public Button homeBtn = new Button();
    private final ToggleGroup ticketGroup = new ToggleGroup();

    public BorderPane buildRoot(Runnable onBack, Runnable onLogout, java.util.function.BiConsumer<String, Double> onCheckout, it.ispwproject.nightflow.bean.EventBean event) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #ede7f6;");
        root.setTop(buildWhiteNavbar(onBack, onLogout));

        VBox mainContent = new VBox(20);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setMaxWidth(700);

        Label title = new Label(event.getName());
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: black;");

        Label locationLabel = new Label("📍 " + event.getLocalName() + " - " + event.getLocation());
        locationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");

        StackPane imageContainer = new StackPane();
        imageContainer.setMaxSize(500, 250);
        try {
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

        String eventTime = event.getDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        if (event.getDescription().contains("Orario:")) {
            eventTime = event.getDescription().split("Orario:")[1].trim();
        } else {
            eventTime += " - Late";
        }

        // 🌟 2. CALCOLIAMO I PREZZI COME NUMERI VERI (DOUBLE)
        double basePriceValue = event.getPrice();
        double drinkPriceValue = event.getPrice() + 5.0;
        double vipPriceValue = event.getPrice() + 85.0;

        ticketSection.getChildren().addAll(listeLbl, ingressoLbl,
                createTicketRow(eventTime, "Senza drink", basePriceValue), // 🌟 Passiamo il Double
                createTicketRow(eventTime, "Con drink", drinkPriceValue),
                createTicketRow(eventTime, "Tavolo VIP", vipPriceValue)
        );

        checkoutBtn.getStyleClass().add("btn-viola-large");

        checkoutBtn.setOnAction(e -> {
            RadioButton selected = (RadioButton) ticketGroup.getSelectedToggle();
            if (selected != null) {
                // 🌟 Estraggo la stringa descrittiva E il prezzo finale dall'UserData
                Object[] data = (Object[]) selected.getUserData();
                onCheckout.accept((String) data[0], (Double) data[1]);
            } else {
                onCheckout.accept("Ingresso Base", event.getPrice());
            }
        });

        mainContent.getChildren().addAll(title, locationLabel, imageContainer, ticketSection, checkoutBtn);

        VBox centerWrapper = new VBox(mainContent);
        centerWrapper.setAlignment(Pos.TOP_CENTER);
        centerWrapper.setPadding(new Insets(30, 0, 50, 0));

        ScrollPane scrollPane = new ScrollPane(centerWrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.getStyleClass().add("transparent-scroll");

        root.setCenter(scrollPane);
        return root;
    }

    private BorderPane buildWhiteNavbar(Runnable onBack, Runnable onLogout) {
        BorderPane nav = new BorderPane();
        nav.setPadding(new Insets(15, 30, 15, 0));
        nav.setStyle("-fx-background-color: #ede7f6; -fx-border-color: #651fff; -fx-border-width: 0 0 2 0;");

        HBox leftBox = new HBox(5); // Spazio ridotto tra bottone e logo
        leftBox.setAlignment(Pos.CENTER_LEFT);// Fissiamo una larghezza massima per il blocco sinistro

        backBtn.setText("< Indietro");
        backBtn.getStyleClass().clear();
        backBtn.getStyleClass().add("back-btn");
        backBtn.setOnAction(e -> onBack.run());

        Label logo = new Label("NightFlow");
        logo.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #b39eff; -fx-font-family: 'Brush Script MT', cursive;");
        logo.setMinWidth(Region.USE_PREF_SIZE);

        leftBox.getChildren().addAll(backBtn, logo);
        nav.setLeft(leftBox);

        HBox rightBox = new HBox(15);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        profileBtn = createIconButton("/icons/profileButton.png");
        homeBtn = createIconButton("/icons/homeButton.png");

        logoutBtn.setText("Log out");
        logoutBtn.setPrefWidth(100);
        logoutBtn.setMinWidth(100);
        logoutBtn.setMaxWidth(100);
        logoutBtn.getStyleClass().add("logout-btn");
        logoutBtn.setOnAction(e -> onLogout.run());

        rightBox.getChildren().addAll(profileBtn, homeBtn, logoutBtn);
        nav.setRight(rightBox);

        return nav;
    }

    private HBox createTicketRow(String time, String desc, Double finalPrice) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: white; -fx-padding: 10 20;");

        Label timeLbl = new Label(time); timeLbl.setPrefWidth(100); timeLbl.setStyle("-fx-text-fill: black;");
        Label descLbl = new Label("| " + desc); HBox.setHgrow(descLbl, Priority.ALWAYS); descLbl.setMaxWidth(Double.MAX_VALUE); descLbl.setStyle("-fx-text-fill: black;");

        // Lo mostriamo a schermo con due decimali
        Label priceLbl = new Label(String.format("%.2f €", finalPrice)); priceLbl.setStyle("-fx-text-fill: black;");

        RadioButton radio = new RadioButton();
        radio.setToggleGroup(ticketGroup);
        // 🌟 Salviamo un ARRAY con la descrizione e il prezzo VERO
        radio.setUserData(new Object[]{desc, finalPrice});
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
        } catch (Exception e) {
            btn.setText("?");
        }

        btn.setPrefWidth(35);
        btn.setMinWidth(35);
        btn.setMaxWidth(35);

        // 🌟 IL SEGRETO È QUI:
        // Abbiamo cancellato il vecchio btn.setStyle(...) e messo la classe CSS!
        btn.getStyleClass().add("icon-btn");

        return btn;
    }
}