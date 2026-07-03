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

    public BorderPane buildRoot(Runnable onBack) {
        // buildShell è ereditato dalla tua PageGUIView base
        return buildShell("I Miei Eventi", onBack);
    }

    /**
     * Costruisce il contenuto centrale con i toggle button e la lista degli eventi.
     */
    public void buildContent(BorderPane root,
                             List<EventBean> upcoming,
                             List<EventBean> past,
                             Consumer<EventBean> onDelete) {

        VBox content = new VBox(12);
        content.setPadding(new Insets(24));
        content.setAlignment(Pos.TOP_CENTER);

        // Toggle per filtrare gli eventi
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

        // Logica di refresh lista: svuota e ricarica in base al bottone selezionato
        Runnable refreshList = () -> {
            listBox.getChildren().clear();
            if (btnUpcoming.isSelected()) {
                if (upcoming.isEmpty()) {
                    listBox.getChildren().add(emptyLabel("Nessun evento in programma."));
                } else {
                    for (EventBean e : upcoming) {
                        listBox.getChildren().add(buildEventCard(e, true, onDelete));
                    }
                }
            } else {
                if (past.isEmpty()) {
                    listBox.getChildren().add(emptyLabel("Nessun evento passato."));
                } else {
                    for (EventBean e : past) {
                        // Gli eventi passati non possono essere eliminati, quindi passiamo false e null
                        listBox.getChildren().add(buildEventCard(e, false, null));
                    }
                }
            }
        };

        // Esegue il refresh iniziale
        refreshList.run();

        // Collega i bottoni all'azione di refresh
        btnUpcoming.setOnAction(e -> refreshList.run());
        btnPast.setOnAction(e -> refreshList.run());

        content.getChildren().addAll(toggleBar, listBox, errorLabel);

        // transparentScroll è ereditato dalla PageGUIView base
        root.setCenter(transparentScroll(content));
    }

    /**
     * Costruisce la singola "Card" grafica per un evento.
     */
    private VBox buildEventCard(EventBean eventBean, boolean canDelete, Consumer<EventBean> onDelete) {
        VBox card = new VBox(8);
        card.getStyleClass().add("info-card");
        card.setMaxWidth(640);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        Label eventName = new Label(eventBean.getName());
        eventName.getStyleClass().add("title-label");

        Label info = new Label("Location: " + eventBean.getLocation() + " | " + eventBean.getDateTime().format(fmt));
        info.getStyleClass().add("info-text");

        // Formattazione per mostrare due cifre decimali nel prezzo (es. €15.50)
        String formattedPrice = String.format("%.2f", eventBean.getPrice());
        Label capacity = new Label("Disponibilità: " + eventBean.getAvailableTickets() + " biglietti | Prezzo: €" + formattedPrice);
        capacity.getStyleClass().add("small-label");
        capacity.setStyle("-fx-text-fill: #9b59b6;"); // Viola scuro (NightFlow theme)

        HBox actions = new HBox();
        actions.setAlignment(Pos.CENTER_RIGHT);

        if (canDelete && onDelete != null) {
            Button deleteBtn = new Button("Elimina Evento");
            deleteBtn.getStyleClass().add("danger-button"); // Classe CSS per bottoni rossi
            deleteBtn.setOnAction(e -> onDelete.accept(eventBean));
            actions.getChildren().add(deleteBtn);
        }

        card.getChildren().addAll(eventName, info, capacity, actions);
        return card;
    }

    private Label emptyLabel(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("register-label");
        return lbl;
    }
}