package it.ispwproject.nightflow.view.gui;

import it.ispwproject.nightflow.bean.EventBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class ViewEventsGUIView extends PageGUIView {

    public final Label errorLabel = buildErrorLabel();

    public void setError(String message) { errorLabel.setText(message); }
    public void clearError()             { errorLabel.setText(""); }

    public BorderPane buildRoot(Runnable onBack, Runnable onLogout, Runnable onProfile, Runnable onHome) {

        return buildShell("I Miei Eventi", onBack, onLogout, onProfile, onHome);
    }

    /**
     * Costruisce il contenuto centrale con i toggle button e la lista degli eventi.
     * Aggiunto Consumer per gestire la modifica della data.
     */
    public void buildContent(BorderPane root,
                             List<EventBean> upcoming,
                             List<EventBean> past,
                             Consumer<EventBean> onEditDate,
                             Consumer<EventBean> onDelete) {

        VBox content = new VBox(12);
        content.setPadding(new Insets(24));
        content.setAlignment(Pos.TOP_CENTER);

        ToggleButton btnUpcoming = new ToggleButton("In Programma (" + upcoming.size() + ")");
        ToggleButton btnPast     = new ToggleButton("Passati (" + past.size() + ")");

        btnUpcoming.getStyleClass().add("toggle-card");
        btnPast.getStyleClass().addAll("toggle-card", "expired");

        ToggleGroup group = new ToggleGroup();
        btnUpcoming.setToggleGroup(group);
        btnPast.setToggleGroup(group);
        btnUpcoming.setSelected(true);

        HBox toggleBar = new HBox(8, btnUpcoming, btnPast);
        toggleBar.setAlignment(Pos.CENTER);
        toggleBar.setMaxWidth(640);

        VBox listBox = new VBox(12);
        listBox.setAlignment(Pos.TOP_CENTER);

        Runnable refreshList = () -> {
            listBox.getChildren().clear();
            if (btnUpcoming.isSelected()) {
                if (upcoming.isEmpty()) {
                    listBox.getChildren().add(emptyLabel("Nessun evento in programma."));
                } else {
                    for (EventBean e : upcoming) {
                        // Passiamo onEditDate alla card
                        listBox.getChildren().add(buildEventCard(e, true, onEditDate, onDelete));
                    }
                }
            } else {
                if (past.isEmpty()) {
                    listBox.getChildren().add(emptyLabel("Nessun evento passato."));
                } else {
                    for (EventBean e : past) {
                        listBox.getChildren().add(buildEventCard(e, false, null, null));
                    }
                }
            }
        };

        refreshList.run();
        btnUpcoming.setOnAction(e -> refreshList.run());
        btnPast.setOnAction(e -> refreshList.run());

        content.getChildren().addAll(toggleBar, listBox, errorLabel);
        root.setCenter(transparentScroll(content));
    }

    /**
     * Costruisce la singola "Card" grafica.
     */
    private VBox buildEventCard(EventBean eventBean, boolean canModify, Consumer<EventBean> onEditDate, Consumer<EventBean> onDelete) {
        VBox card = new VBox(8);
        card.getStyleClass().add("info-card");
        card.setMaxWidth(640);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        Label eventName = new Label(eventBean.getName());
        eventName.getStyleClass().add("title-label");

        // La data diventa un "link" cliccabile se l'evento è modificabile
        Label dateLabel = new Label("Data: " + eventBean.getDateTime().format(fmt));
        dateLabel.getStyleClass().add("info-text");

        if (canModify && onEditDate != null) {
            dateLabel.setStyle("-fx-text-fill: #9b59b6; -fx-cursor: hand; -fx-underline: true; -fx-font-weight: bold;");
            dateLabel.setTooltip(new Tooltip("Clicca per modificare la data"));
            dateLabel.setOnMouseClicked(e -> onEditDate.accept(eventBean));
        }

        Label info = new Label("Location: " + eventBean.getLocation());
        info.getStyleClass().add("info-text");

        String formattedPrice = String.format("%.2f", eventBean.getPrice());
        Label capacity = new Label("Disponibilità: " + eventBean.getAvailableTickets() + " biglietti | Prezzo: €" + formattedPrice);
        capacity.getStyleClass().add("small-label");

        HBox actions = new HBox();
        actions.setAlignment(Pos.CENTER_RIGHT);

        if (canModify && onDelete != null) {
            Button deleteBtn = new Button("Elimina Evento");
            deleteBtn.getStyleClass().add("btn-viola-small");
            deleteBtn.setOnAction(e -> onDelete.accept(eventBean));
            actions.getChildren().add(deleteBtn);
        }

        card.getChildren().addAll(eventName, dateLabel, info, capacity, actions);
        return card;
    }

    private Label emptyLabel(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("register-label");
        return lbl;
    }
}