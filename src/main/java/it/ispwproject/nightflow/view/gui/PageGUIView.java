package it.ispwproject.nightflow.view.gui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public abstract class PageGUIView {

    // ────────────────────────────────────────────────────────────────────────
    // Top bar comune a tutte le pagine secondarie
    // ────────────────────────────────────────────────────────────────────────

    // 🌟 NUOVO METODO COMPLETO CON I BOTTONI DELLA NAVBAR
    public HBox buildTopBar(String titleText, Runnable onBack, Runnable onLogout, Runnable onProfile, Runnable onHome) {
        HBox bar = new HBox();
        bar.getStyleClass().add("navbar");
        bar.setAlignment(Pos.CENTER_LEFT);

        // -- SINISTRA: Tasto Indietro --
        HBox left = new HBox();
        left.setAlignment(Pos.CENTER_LEFT);
        left.setPrefWidth(200); // Leggermente allargato per bilanciare i 3 bottoni a destra
        HBox.setHgrow(left, Priority.ALWAYS);

        if (onBack != null) {
            Button backBtn = new Button("⟪  Indietro");
            backBtn.getStyleClass().add("back-button");
            backBtn.setOnAction(e -> onBack.run());
            left.getChildren().add(backBtn);
        }

        // -- CENTRO: Titolo --
        Label title = new Label(titleText);
        title.getStyleClass().add("page-title");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);
        HBox.setHgrow(title, Priority.ALWAYS);

        // -- DESTRA: Bottoni Navbar (Addio Logo!) --
        HBox right = new HBox(15);
        right.setAlignment(Pos.CENTER_RIGHT);
        right.setPrefWidth(200);
        HBox.setHgrow(right, Priority.ALWAYS);

        if (onProfile != null) {
            Button profileBtn = createIconButton("/icons/profileButton.png");
            profileBtn.setOnAction(e -> onProfile.run());
            right.getChildren().add(profileBtn);
        }

        if (onHome != null) {
            Button homeBtn = createIconButton("/icons/homeButton.png");
            homeBtn.setOnAction(e -> onHome.run());
            right.getChildren().add(homeBtn);
        }

        if (onLogout != null) {
            Button logoutBtn = new Button("Log out");
            logoutBtn.getStyleClass().add("logout-btn");
            logoutBtn.setOnAction(e -> onLogout.run());
            right.getChildren().add(logoutBtn);
        }

        bar.getChildren().addAll(left, title, right);
        return bar;
    }

    // ────────────────────────────────────────────────────────────────────────
    // ScrollPane trasparente comune
    // ────────────────────────────────────────────────────────────────────────

    public ScrollPane transparentScroll(Node content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.getStyleClass().add("transparent-scroll");
        scroll.setFitToWidth(true);
        return scroll;
    }

    // ────────────────────────────────────────────────────────────────────────
    // Shell comune (BorderPane con top bar)
    // ────────────────────────────────────────────────────────────────────────

    // 🌟 NUOVO SHELL COMPLETO (Passa i bottoni)
    public BorderPane buildShell(String titleText, Runnable onBack, Runnable onLogout, Runnable onProfile, Runnable onHome) {
        BorderPane shell = new BorderPane();
        shell.getStyleClass().add("nightflow-background");
        shell.setTop(buildTopBar(titleText, onBack, onLogout, onProfile, onHome));
        return shell;
    }

    // 🌟 VECCHIO SHELL (Passa solo l'indietro)
    public BorderPane buildShell(String titleText, Runnable onBack) {
        return buildShell(titleText, onBack, null, null, null);
    }

    // ────────────────────────────────────────────────────────────────────────
    // Error label comune
    // ────────────────────────────────────────────────────────────────────────

    public Label buildErrorLabel() {
        Label lbl = new Label("");
        lbl.getStyleClass().add("error-label");
        lbl.setWrapText(true);
        return lbl;
    }

    // ────────────────────────────────────────────────────────────────────────
    // Utility per icone (centralizzato qui così non devi ricopiarlo)
    // ────────────────────────────────────────────────────────────────────────
    public Button createIconButton(String path) {
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

        // 🌟 RIMOSSO il setStyle che bloccava il CSS
        // 🌟 AGGIUNTA la classe CSS che avevi definito prima
        btn.getStyleClass().add("icon-btn");

        return btn;
    }
}