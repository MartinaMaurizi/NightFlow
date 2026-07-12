package it.ispwproject.nightflow.view.gui;

import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public abstract class DashboardGUIView {

    public static final int HOUR_START = 18;
    public static final int HOUR_HEIGHT = 32;

    // ── Utility per icone (Questo risolve l'errore rosso!) ────────────────
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

        btn.getStyleClass().add("icon-btn");

        return btn;
    }

    // ── Navbar standardizzata (Il tuo metodo originale) ─────────────────────
    public HBox buildNavbar(String ruoloText, Runnable onLogout) {
        HBox navbar = new HBox();
        navbar.getStyleClass().add("navbar");
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setPadding(new Insets(10, 20, 10, 20));

        // Logo e Nome Utente
        ImageView logoView = new ImageView(new Image(getClass().getResourceAsStream("/images/logo.png"), 50, 50, true, true));

        // Controllo di sicurezza se l'utente non dovesse essere loggato
        String userName = (SessionManager.getInstance().getLoggedUser() != null) ? SessionManager.getInstance().getLoggedUser().getName() : "Ospite";
        Label welcome = new Label("Bentornato\n" + userName + "!");
        welcome.getStyleClass().add("welcome-label");

        HBox left = new HBox(15, logoView, welcome);
        left.setAlignment(Pos.CENTER_LEFT);

        // Ruolo
        Label ruolo = new Label(ruoloText);
        ruolo.getStyleClass().add("role-label");

        // Logout
        Button logoutBtn = new Button("Log out");
        logoutBtn.getStyleClass().add("button");
        logoutBtn.setOnAction(e -> onLogout.run());

        HBox right = new HBox(logoutBtn);
        right.setAlignment(Pos.CENTER_RIGHT);

        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(ruolo, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);

        navbar.getChildren().addAll(left, ruolo, right);
        return navbar;
    }

    // ── Azioni rapide (le "Tile" usate nelle dashboard) ──────────────────────
    public HBox buildActionTile(String iconFile, String text, EventHandler<ActionEvent> handler) {
        HBox tile = new HBox(14);
        tile.getStyleClass().add("action-tile");
        tile.setAlignment(Pos.CENTER_LEFT);
        tile.setPadding(new Insets(10));
        tile.setOnMouseClicked(e -> handler.handle(new ActionEvent(tile, null)));

        var iconStream = getClass().getResourceAsStream("/icons/" + iconFile);
        if (iconStream != null) {
            ImageView icon = new ImageView(new Image(iconStream, 24, 24, true, true));
            ColorAdjust ca = new ColorAdjust(); ca.setBrightness(1.0);
            icon.setEffect(ca);
            tile.getChildren().add(icon);
        }
        tile.getChildren().add(new Label(text));
        return tile;
    }

    // ── Utility per il layout ───────────────────────────────────────────────
    public HBox infoRow(String label, String value) {
        Label lbl = new Label(label + ":");
        lbl.getStyleClass().add("small-label");
        Label val = new Label(value != null ? value : "—");
        val.getStyleClass().add("info-text");
        return new HBox(8, lbl, val);
    }
}