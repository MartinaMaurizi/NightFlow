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

    public HBox buildTopBar(String titleText, Runnable onBack) {
        HBox bar = new HBox();
        bar.getStyleClass().add("navbar");
        bar.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("⟪  Indietro");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> onBack.run());

        HBox left = new HBox(backBtn);
        left.setAlignment(Pos.CENTER_LEFT);
        left.setPrefWidth(150);
        HBox.setHgrow(left, Priority.ALWAYS);

        Label title = new Label(titleText);
        title.getStyleClass().add("page-title");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);
        HBox.setHgrow(title, Priority.ALWAYS);

        HBox right = new HBox();
        right.setAlignment(Pos.CENTER_RIGHT);
        right.setPrefWidth(150);
        HBox.setHgrow(right, Priority.ALWAYS);

        var logoStream = getClass().getResourceAsStream("/images/logo.png");
        if (logoStream != null) {
            ImageView logo = new ImageView(new Image(logoStream, 60, 60, true, true));
            logo.setFitHeight(56);
            logo.setPreserveRatio(true);
            logo.setSmooth(true);
            right.getChildren().add(logo);
        }

        bar.getChildren().addAll(left, title, right);
        return bar;
    }

    // ────────────────────────────────────────────────────────────────────────
    // ScrollPane trasparente comune
    // ────────────────────────────────────────────────────────────────────────

    public ScrollPane transparentScroll(Node content) {
        ScrollPane scroll = new ScrollPane(content);
        // Aggiornato per riflettere il nuovo tema
        scroll.getStyleClass().add("transparent-scroll");
        scroll.setFitToWidth(true);
        return scroll;
    }

    // ────────────────────────────────────────────────────────────────────────
    // Shell comune (BorderPane con top bar)
    // ────────────────────────────────────────────────────────────────────────

    public BorderPane buildShell(String titleText, Runnable onBack) {
        BorderPane shell = new BorderPane();
        // Sostituito il tema BrainBank con quello di NightFlow
        shell.getStyleClass().add("nightflow-background");
        shell.setTop(buildTopBar(titleText, onBack));
        return shell;
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
}