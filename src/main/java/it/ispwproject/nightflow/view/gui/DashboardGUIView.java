package it.ispwproject.nightflow.view.gui;

import it.ispwproject.nightflow.model.User;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.function.Consumer;

public abstract class DashboardGUIView {

    // ── Costanti calendario NightFlow (Serali/Notturni) ─────────────────────
    public static final int HOUR_START  = 18; // Inizia alle 18:00
    public static final int HOUR_END    = 29; // Finisce alle 05:00 (18 + 11 ore)
    public static final int HOUR_HEIGHT = 32; // Leggermente più alto per respiro
    public static final int LABEL_WIDTH = 55; // Più largo per accogliere "12 AM" comodamente
    public static final int HEADER_H    = 72;
    public static final int DAYS        = 7;

    // ────────────────────────────────────────────────────────────────────────
    // Navbar comune
    // ────────────────────────────────────────────────────────────────────────

    public HBox buildNavbar(String ruoloText, Runnable onLogout) {
        HBox navbar = new HBox();
        navbar.getStyleClass().add("navbar");
        navbar.setAlignment(Pos.CENTER_LEFT);

        Image logoImg = new Image(
                getClass().getResourceAsStream("/images/logo.png"), 80, 80, true, true);
        ImageView logoView = new ImageView(logoImg);
        logoView.setFitHeight(56); logoView.setFitWidth(56);
        logoView.setPreserveRatio(true); logoView.setSmooth(true);

        String nome = SessionManager.getInstance().getLoggedUser().getName();
        Label welcome = new Label("Bentornato\n" + nome + "!");
        welcome.getStyleClass().add("welcome-label");

        HBox left = new HBox(10, logoView, welcome);
        left.setAlignment(Pos.CENTER_LEFT);

        Label ruolo = new Label(ruoloText);
        ruolo.getStyleClass().add("role-label");
        ruolo.setMaxWidth(Double.MAX_VALUE);
        ruolo.setAlignment(Pos.CENTER);

        Button logoutBtn = new Button("Log out");
        logoutBtn.getStyleClass().add("button");
        logoutBtn.setPadding(new Insets(6, 18, 6, 18));
        logoutBtn.setOnAction(e -> onLogout.run());

        HBox right = new HBox(logoutBtn);
        right.setAlignment(Pos.CENTER_RIGHT);

        HBox.setHgrow(left,  Priority.ALWAYS);
        HBox.setHgrow(ruolo, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);

        navbar.getChildren().addAll(left, ruolo, right);
        return navbar;
    }

    // ────────────────────────────────────────────────────────────────────────
    // Struttura calendario comune
    // ────────────────────────────────────────────────────────────────────────

    public VBox buildCalendarSection(Runnable onPrev, Runnable onNext,
                                     Runnable onToday, ScrollPane scroll) {
        VBox section = new VBox(10);
        section.setPrefWidth(620);
        section.setMaxWidth(620);
        section.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(section, Priority.ALWAYS);

        // Modificato per il nuovo dominio
        Label title = new Label("Calendario Eventi");
        title.getStyleClass().add("calendar-title");

        scroll.getStyleClass().add("transparent-scroll");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setFitToWidth(true);
        scroll.setMinHeight(200);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Button prevBtn  = makeNavBtn("‹", e -> onPrev.run());
        Button nextBtn  = makeNavBtn("›", e -> onNext.run());
        Button todayBtn = makeTodayBtn(e  -> onToday.run());

        HBox navBar = new HBox(8, prevBtn, todayBtn, nextBtn);
        navBar.setAlignment(Pos.CENTER_LEFT);

        HBox titleRow = new HBox(16, title, navBar);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        section.getChildren().addAll(titleRow, scroll);
        return section;
    }

    public Pane buildCalendarPane(LocalDate monday, int colW) {
        int totalHours = HOUR_END - HOUR_START;
        int gridHeight = totalHours * HOUR_HEIGHT;

        Pane pane = new Pane();
        pane.setPrefSize(LABEL_WIDTH + DAYS * colW + 8, gridHeight + HEADER_H);
        pane.getStyleClass().add("calendar-pane");
        return pane;
    }

    public void addMonthRow(Pane pane, LocalDate firstDay, int colW) {
        LocalDate lastDay = firstDay.plusDays(DAYS - 1);
        String month = firstDay.getMonth() == lastDay.getMonth()
                ? cap(firstDay.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN))
                + " " + firstDay.getYear()
                : cap(firstDay.getMonth().getDisplayName(TextStyle.SHORT, Locale.ITALIAN))
                + " – "
                + cap(lastDay.getMonth().getDisplayName(TextStyle.SHORT, Locale.ITALIAN))
                + " " + lastDay.getYear();

        Label lbl = new Label(month);
        lbl.getStyleClass().add("calendar-month-label");
        lbl.setLayoutX(LABEL_WIDTH); lbl.setLayoutY(8);
        lbl.setPrefWidth(DAYS * colW); lbl.setAlignment(Pos.CENTER);
        pane.getChildren().add(lbl);
    }

    public void addDayHeaders(Pane pane, LocalDate firstDay, LocalDate today, int colW) {
        for (int d = 0; d < DAYS; d++) {
            LocalDate date    = firstDay.plusDays(d);
            boolean   isToday = date.equals(today);
            String dayName = date.getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, Locale.ITALIAN).toUpperCase();
            double x = LABEL_WIDTH + d * colW;

            Label dayAbbr = new Label(dayName);
            dayAbbr.getStyleClass().add("calendar-day-label");
            dayAbbr.setLayoutX(x); dayAbbr.setLayoutY(32);
            dayAbbr.setPrefWidth(colW); dayAbbr.setAlignment(Pos.CENTER);
            pane.getChildren().add(dayAbbr);

            if (isToday) {
                Label badge = new Label(String.valueOf(date.getDayOfMonth()));
                badge.getStyleClass().add("calendar-today-badge");
                badge.setPrefSize(22, 22); badge.setAlignment(Pos.CENTER);
                badge.setLayoutX(x + (colW - 22) / 2.0);
                badge.setLayoutY(HEADER_H - 30);
                pane.getChildren().add(badge);
            } else {
                Label num = new Label(String.valueOf(date.getDayOfMonth()));
                num.getStyleClass().add("calendar-day-label");
                num.setLayoutX(x); num.setLayoutY(HEADER_H - 28);
                num.setPrefWidth(colW); num.setAlignment(Pos.CENTER);
                pane.getChildren().add(num);
            }
        }
    }

    public void addHourRows(Pane pane, int totalHours, int colW, int gridHeight) {
        Region sep = new Region();
        sep.setPrefSize(DAYS * colW, 1);
        sep.getStyleClass().add("calendar-separator");
        sep.setLayoutX(LABEL_WIDTH); sep.setLayoutY(HEADER_H);
        pane.getChildren().add(sep);

        for (int h = 0; h < totalHours; h++) {
            int hour = HOUR_START + h;
            int y    = HEADER_H + h * HOUR_HEIGHT;

            // Logica per gestire gli orari oltre la mezzanotte per NightFlow
            int displayHour = hour % 24;
            String ht;
            if (displayHour == 0) {
                ht = "12 AM";
            } else if (displayHour < 12) {
                ht = displayHour + " AM";
            } else if (displayHour == 12) {
                ht = "12 PM";
            } else {
                ht = (displayHour - 12) + " PM";
            }

            Label lbl = new Label(ht);
            lbl.getStyleClass().add("calendar-hour-label");
            lbl.setPrefWidth(LABEL_WIDTH - 4); lbl.setAlignment(Pos.CENTER_RIGHT);
            lbl.setLayoutX(0); lbl.setLayoutY(y - 7);
            pane.getChildren().add(lbl);

            Region hLine = new Region();
            hLine.setPrefSize(DAYS * colW, 1);
            hLine.getStyleClass().add("calendar-grid-line");
            hLine.setLayoutX(LABEL_WIDTH); hLine.setLayoutY(y);
            pane.getChildren().add(hLine);
        }

        for (int d = 1; d < DAYS; d++) {
            Region vLine = new Region();
            vLine.setPrefSize(1, gridHeight);
            vLine.getStyleClass().add("calendar-grid-line");
            vLine.setLayoutX(LABEL_WIDTH + d * colW); vLine.setLayoutY(HEADER_H);
            pane.getChildren().add(vLine);
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Accordion info utente comune
    // ────────────────────────────────────────────────────────────────────────

    public VBox buildUserInfoAccordion(User user, Consumer<String> onSaveEmail) {
        HBox header = new HBox(8);
        header.getStyleClass().add("accordion-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, 16, 12, 16));

        Label headerLbl = new Label("Le tue informazioni");
        headerLbl.getStyleClass().add("small-label");
        HBox.setHgrow(headerLbl, Priority.ALWAYS);

        Label arrow = new Label("▼");
        arrow.getStyleClass().add("accordion-arrow");
        header.getChildren().addAll(headerLbl, arrow);

        VBox content = new VBox(10);
        content.getStyleClass().add("accordion-content");
        content.setPadding(new Insets(10, 16, 14, 16));
        content.getChildren().addAll(
                new Separator(),
                infoRow("Nome",    user.getName()),
                infoRow("Cognome", user.getSurname()),
                buildEmailRow(user, onSaveEmail));
        content.setVisible(false);
        content.setManaged(false);

        final boolean[] open = {false};
        header.setOnMouseClicked(e -> {
            open[0] = !open[0];
            content.setVisible(open[0]);
            content.setManaged(open[0]);
            arrow.setText(open[0] ? "▲" : "▼");
            header.getStyleClass().setAll(open[0] ? "accordion-header-open" : "accordion-header");
        });

        VBox accordion = new VBox(0, header, content);
        accordion.getStyleClass().add("accordion-panel");
        return accordion;
    }

    public HBox buildEmailRow(User user, Consumer<String> onSave) {
        Label emailLbl = new Label(user.getEmail() != null ? user.getEmail() : "—");
        emailLbl.getStyleClass().add("info-text");

        Button editBtn = new Button();
        var pencilStream = getClass().getResourceAsStream("/icons/pencil.png");
        if (pencilStream != null) {
            ImageView pencilIcon = new ImageView(new Image(pencilStream, 32, 32, true, true));
            pencilIcon.setFitHeight(16); pencilIcon.setFitWidth(16);
            pencilIcon.setPreserveRatio(true); pencilIcon.setSmooth(true);
            editBtn.setGraphic(pencilIcon);
        } else {
            editBtn.setText("✏");
        }
        editBtn.getStyleClass().add("edit-button");
        editBtn.setAlignment(Pos.CENTER);

        Label emailKey = new Label("Email:");
        emailKey.getStyleClass().add("small-label");
        emailKey.setPrefWidth(70);

        HBox viewRow = new HBox(8, emailKey, emailLbl, editBtn);
        viewRow.setAlignment(Pos.CENTER_LEFT);

        TextField emailField = new TextField(user.getEmail());
        emailField.getStyleClass().add("text-field");
        emailField.setPrefHeight(32);

        Button saveBtn   = new Button("✓");
        Button cancelBtn = new Button("✗");
        saveBtn.getStyleClass().add("save-button");
        saveBtn.setMinWidth(32); saveBtn.setMinHeight(32);
        cancelBtn.getStyleClass().add("cancel-inline-button");
        cancelBtn.setMinWidth(32); cancelBtn.setMinHeight(32);

        HBox editRow = new HBox(6, emailField, saveBtn, cancelBtn);
        editRow.setAlignment(Pos.CENTER_LEFT);
        editRow.setVisible(false); editRow.setManaged(false);

        VBox emailContainer = new VBox(4, viewRow, editRow);

        editBtn.setOnAction(e -> {
            viewRow.setVisible(false); viewRow.setManaged(false);
            editRow.setVisible(true);  editRow.setManaged(true);
            emailField.requestFocus();
        });
        cancelBtn.setOnAction(e -> {
            emailField.setText(user.getEmail());
            editRow.setVisible(false);  editRow.setManaged(false);
            viewRow.setVisible(true);   viewRow.setManaged(true);
        });
        saveBtn.setOnAction(e -> {
            String newEmail = emailField.getText().trim();
            if (newEmail.isEmpty()) return;
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Conferma cambio email");
            confirm.setHeaderText(null);
            confirm.setContentText("Vuoi cambiare l'email a:\n" + newEmail + "?");
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK) {
                    emailLbl.setText(newEmail);
                    editRow.setVisible(false);  editRow.setManaged(false);
                    viewRow.setVisible(true);   viewRow.setManaged(true);
                    onSave.accept(newEmail);
                }
            });
        });

        emailContainer.setUserData(emailField);
        return new HBox(emailContainer);
    }

    // ────────────────────────────────────────────────────────────────────────
    // Widget comuni
    // ────────────────────────────────────────────────────────────────────────

    public HBox buildActionTile(String iconFile, String text,
                                EventHandler<ActionEvent> handler) {
        HBox tile = new HBox(14);
        tile.getStyleClass().add("action-tile");
        tile.setAlignment(Pos.CENTER_LEFT);
        tile.setPrefHeight(58);
        tile.setMaxWidth(Double.MAX_VALUE);
        tile.setPadding(new Insets(12, 16, 12, 16));
        tile.setOnMouseClicked(e -> handler.handle(new ActionEvent(tile, null)));

        var iconStream = getClass().getResourceAsStream("/icons/" + iconFile);
        if (iconStream != null) {
            ImageView icon = new ImageView(new Image(iconStream, 48, 48, true, true));
            icon.setFitHeight(22); icon.setFitWidth(22);
            icon.setPreserveRatio(true); icon.setSmooth(true);
            javafx.scene.effect.ColorAdjust ca = new javafx.scene.effect.ColorAdjust();
            ca.setBrightness(1.0);
            icon.setEffect(ca);
            tile.getChildren().add(icon);
        }

        Label lbl = new Label(text);
        lbl.getStyleClass().add("action-tile-label");
        lbl.setWrapText(false);
        tile.getChildren().add(lbl);
        return tile;
    }

    public HBox infoRow(String label, String value) {
        Label lbl = new Label(label + ":");
        lbl.getStyleClass().add("small-label");
        lbl.setPrefWidth(70);
        Label val = new Label(value != null ? value : "—");
        val.getStyleClass().add("info-text");
        val.setWrapText(true);
        HBox row = new HBox(8, lbl, val);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    public Button makeNavBtn(String text, EventHandler<ActionEvent> h) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-button");
        btn.setPrefSize(36, 36);
        btn.setMinSize(36, 36);
        btn.setOnAction(h);
        return btn;
    }

    public Button makeTodayBtn(EventHandler<ActionEvent> h) {
        Button btn = new Button("Oggi");
        btn.getStyleClass().add("today-button");
        btn.setOnAction(h);
        return btn;
    }

    public String cap(String s) {
        return (s == null || s.isEmpty()) ? s :
                Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}