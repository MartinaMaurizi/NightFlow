package it.ispwproject.nightflow.view.gui;

import it.ispwproject.nightflow.enumerator.PaymentMethod;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

public class CheckoutGUIView {

    public final Button backBtn = new Button("<");
    public final Button logoutBtn = new Button("Log out");
    public final Button confirmBtn = new Button("Conferma Prenotazione");
    public Button profileBtn = new Button();
    public Button homeBtn = new Button();

    public final TextField nameFld = new TextField();
    public final TextField emailFld = new TextField();
    public final TextField dateFld = new TextField();

    //  RESO PUBBLICO PER I CONTROLLI NEL CONTROLLER
    public final TextField phoneFld = new TextField();

    //  CAMPI CARTA RESI PUBBLICI PER I CONTROLLI NEL CONTROLLER
    public final TextField cardNameFld = new TextField();
    public final TextField cardNumFld = new TextField();
    public final TextField cardExpFld = new TextField();
    public final TextField cardCvvFld = new TextField();

    public PaymentMethod selectedPaymentMethod = null;

    //  RICEVE SOLO STRINGHE: userName, userEmail, userDob
    public BorderPane buildRoot(String userName, String userEmail, String userDob, Runnable onBack, Runnable onLogout, Runnable onConfirm,
                                String eventTitle, String eventDate, String ticketDetails, String imagePath) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #ede7f6;");
        root.setTop(buildNavbar(onBack, onLogout));

        VBox mainContent = new VBox(30);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setPadding(new Insets(30, 50, 50, 50));

        // --- HEADER CARD RIEPILOGO ---
        HBox summaryCard = new HBox(20);
        summaryCard.setMaxWidth(800);
        summaryCard.setStyle("-fx-background-color: #bbaaf2; -fx-background-radius: 20; -fx-padding: 15;");
        summaryCard.setAlignment(Pos.CENTER_LEFT);

        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(200, 100);
        try {
            ImageView imgView = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
            imgView.setFitWidth(200); imgView.setFitHeight(100); imgView.setPreserveRatio(false);
            Rectangle clip = new Rectangle(200, 100); clip.setArcWidth(30); clip.setArcHeight(30);
            imgView.setClip(clip);
            imageContainer.getChildren().add(imgView);
        } catch (Exception e) { imageContainer.setStyle("-fx-background-color: #4a00e0; -fx-background-radius: 15;"); }

        VBox summaryTexts = new VBox(5);
        summaryTexts.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(eventTitle);
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: black;");
        Label date = new Label(eventDate);
        date.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;");
        Label detail = new Label("Riepilogo : " + ticketDetails);
        detail.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");

        summaryTexts.getChildren().addAll(title, date, detail);
        summaryCard.getChildren().addAll(imageContainer, summaryTexts);

        // --- SEZIONE INFERIORE A DUE COLONNE ---
        HBox columns = new HBox(60);
        columns.setAlignment(Pos.TOP_CENTER);

        // COLONNA SINISTRA: I TUOI DATI
        VBox leftCol = new VBox(15);
        leftCol.setPrefWidth(350);
        Label datiTitle = new Label("I tuoi Dati");
        datiTitle.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");

        String fieldStyle = "-fx-background-color: #bbaaf2; -fx-background-radius: 15; -fx-padding: 8 15; -fx-prompt-text-fill: #444444; -fx-text-fill: black;";

        nameFld.setStyle(fieldStyle);
        emailFld.setStyle(fieldStyle);
        dateFld.setStyle(fieldStyle);

        phoneFld.setPromptText("Telefono*");
        phoneFld.setStyle(fieldStyle);

        // RIEMPIMENTO TRAMITE STRINGHE PURIFICATE
        nameFld.setText(userName != null ? userName : "");
        emailFld.setText(userEmail != null ? userEmail : "");
        dateFld.setText(userDob != null ? userDob : "");

        // Blocchiamo i campi anagrafici precompilati per impedire modifiche
        nameFld.setEditable(false); nameFld.setFocusTraversable(false);
        emailFld.setEditable(false); emailFld.setFocusTraversable(false);
        dateFld.setEditable(false); dateFld.setFocusTraversable(false);

        Label reqLbl = new Label("*campi obbligatori"); reqLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");

        leftCol.getChildren().addAll(datiTitle, nameFld, emailFld, dateFld, phoneFld, reqLbl);

        // COLONNA DESTRA: METODO DI PAGAMENTO
        VBox rightCol = new VBox(15);
        rightCol.setPrefWidth(450);
        Label payTitle = new Label("Scegli il metodo di pagamento");
        payTitle.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");

        // Box Carta di Credito
        VBox cardBox = new VBox(15);
        String defaultBoxStyle = "-fx-background-color: #bbaaf2; -fx-background-radius: 15; -fx-padding: 15; -fx-cursor: hand;";
        cardBox.setStyle(defaultBoxStyle);

        Label cardTitle = new Label("💳 Carta di debito o credito");
        cardTitle.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
        Region separator = new Region();
        separator.setStyle("-fx-border-color: #651fff; -fx-border-width: 0 0 1 0;");

        String inputStyle = "-fx-background-color: transparent; -fx-border-color: transparent; -fx-prompt-text-fill: #333333; -fx-text-fill: black;";
        cardNameFld.setPromptText("Nome sulla carta"); cardNameFld.setStyle(inputStyle);
        cardNumFld.setPromptText("Numero della carta"); cardNumFld.setStyle(inputStyle);

        HBox bottomCardRow = new HBox(20);
        cardExpFld.setPromptText("Scadenza(MM/AA)"); cardExpFld.setStyle(inputStyle);
        cardCvvFld.setPromptText("CVV"); cardCvvFld.setStyle(inputStyle);
        bottomCardRow.getChildren().addAll(cardExpFld, cardCvvFld);

        cardBox.getChildren().addAll(cardTitle, separator, cardNameFld, cardNumFld, bottomCardRow);

        // Pillole PayPal e Contanti
        String defaultLabelStyle = "-fx-background-color: #bbaaf2; -fx-background-radius: 15; -fx-padding: 10 15; -fx-font-size: 16px; -fx-text-fill: black; -fx-cursor: hand;";

        Label paypalLbl = new Label("PayPal");
        paypalLbl.setStyle(defaultLabelStyle);
        paypalLbl.setMaxWidth(Double.MAX_VALUE);

        Label cashLbl = new Label("Paga all'ingresso");
        cashLbl.setStyle(defaultLabelStyle);
        cashLbl.setMaxWidth(Double.MAX_VALUE);

        // GESTIONE CLICK SUI PAGAMENTI
        String selectedBoxStyle = "-fx-background-color: #651fff; -fx-background-radius: 15; -fx-padding: 15; -fx-cursor: hand;";
        String selectedLabelStyle = "-fx-background-color: #651fff; -fx-background-radius: 15; -fx-padding: 10 15; -fx-font-size: 16px; -fx-text-fill: white; -fx-cursor: hand;";

        Runnable resetPaymentStyles = () -> {
            cardBox.setStyle(defaultBoxStyle);
            cardTitle.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
            paypalLbl.setStyle(defaultLabelStyle);
            cashLbl.setStyle(defaultLabelStyle);
        };

        cardBox.setOnMouseClicked(e -> {
            resetPaymentStyles.run();
            cardBox.setStyle(selectedBoxStyle);
            cardTitle.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
            selectedPaymentMethod = PaymentMethod.CREDIT_CARD;
        });

        paypalLbl.setOnMouseClicked(e -> {
            resetPaymentStyles.run();
            paypalLbl.setStyle(selectedLabelStyle);
            selectedPaymentMethod = PaymentMethod.PAYPAL;
        });

        cashLbl.setOnMouseClicked(e -> {
            resetPaymentStyles.run();
            cashLbl.setStyle(selectedLabelStyle);
            selectedPaymentMethod = PaymentMethod.PAY_ON_SITE;
        });

        rightCol.getChildren().addAll(payTitle, cardBox, paypalLbl, cashLbl);
        columns.getChildren().addAll(leftCol, rightCol);

        // --- BOTTONE CONFERMA ---
        confirmBtn.getStyleClass().add("danger-button");
        confirmBtn.setOnAction(e -> onConfirm.run());

        mainContent.getChildren().addAll(summaryCard, columns, confirmBtn);

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        root.setCenter(scrollPane);
        return root;
    }

    private BorderPane buildNavbar(Runnable onBack, Runnable onLogout) {
        BorderPane nav = new BorderPane();
        nav.setPadding(new Insets(15, 30, 15, 0));
        nav.setStyle("-fx-background-color: #ede7f6; -fx-border-color: #651fff; -fx-border-width: 0 0 2 0;");

        HBox leftBox = new HBox(5);
        leftBox.setAlignment(Pos.CENTER_LEFT);
        leftBox.setPrefWidth(180);

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
        logoutBtn.getStyleClass().add("logout-btn");
        logoutBtn.setOnAction(e -> onLogout.run());

        rightBox.getChildren().addAll(profileBtn, homeBtn, logoutBtn);
        nav.setRight(rightBox);

        return nav;
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

        btn.getStyleClass().add("icon-btn");

        return btn;
    }
}