package it.ispwproject.nightflow.view.gui;

import it.ispwproject.nightflow.bean.BookingResponseBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class ViewBookingsGUIView extends PageGUIView {

    public final Label errorLabel = buildErrorLabel();

    public void setError(String message) { errorLabel.setText(message); }
    public void clearError()             { errorLabel.setText(""); }

    public BorderPane buildRoot(Runnable onBack) {
        return buildShell("Le mie prenotazioni", onBack);
    }

    public void buildContent(BorderPane root,
                             List<BookingResponseBean> confirmed,
                             List<BookingResponseBean> cancelled,
                             List<BookingResponseBean> past,
                             Consumer<BookingResponseBean> onCancel) {
        VBox content = new VBox(12);
        content.setPadding(new Insets(24));
        content.setAlignment(Pos.TOP_CENTER);

        // Toggle per filtrare le prenotazioni
        ToggleButton btnConfirmed = new ToggleButton("Confermate (" + confirmed.size() + ")");
        ToggleButton btnCancelled = new ToggleButton("Cancellate (" + cancelled.size() + ")");
        ToggleButton btnPast      = new ToggleButton("Passate (" + past.size() + ")");

        btnConfirmed.getStyleClass().add("toggle-card");
        btnCancelled.getStyleClass().addAll("toggle-card", "cancelled");
        btnPast.getStyleClass().addAll("toggle-card", "expired");

        ToggleGroup group = new ToggleGroup();
        btnConfirmed.setToggleGroup(group);
        btnCancelled.setToggleGroup(group);
        btnPast.setToggleGroup(group);
        btnConfirmed.setSelected(true);

        HBox toggleBar = new HBox(8, btnConfirmed, btnCancelled, btnPast);
        toggleBar.setAlignment(Pos.CENTER);
        toggleBar.setMaxWidth(640);

        VBox listBox = new VBox(12);
        listBox.setAlignment(Pos.TOP_CENTER);

        // Logica di refresh lista
        Runnable refreshList = () -> {
            listBox.getChildren().clear();
            if (btnConfirmed.isSelected()) {
                if (confirmed.isEmpty()) listBox.getChildren().add(emptyLabel("Nessuna prenotazione attiva."));
                else for (BookingResponseBean b : confirmed) listBox.getChildren().add(buildBookingCard(b, true, onCancel));
            } else if (btnCancelled.isSelected()) {
                if (cancelled.isEmpty()) listBox.getChildren().add(emptyLabel("Nessuna prenotazione cancellata."));
                else for (BookingResponseBean b : cancelled) listBox.getChildren().add(buildBookingCard(b, false, null));
            } else {
                if (past.isEmpty()) listBox.getChildren().add(emptyLabel("Nessuna serata passata."));
                else for (BookingResponseBean b : past) listBox.getChildren().add(buildBookingCard(b, false, null));
            }
        };

        refreshList.run();
        btnConfirmed.setOnAction(e -> refreshList.run());
        btnCancelled.setOnAction(e -> refreshList.run());
        btnPast.setOnAction(e -> refreshList.run());

        content.getChildren().addAll(toggleBar, listBox, errorLabel);
        root.setCenter(transparentScroll(content));
    }

    private VBox buildBookingCard(BookingResponseBean b, boolean cancellable, Consumer<BookingResponseBean> onCancel) {
        VBox card = new VBox(8); card.getStyleClass().add("info-card"); card.setMaxWidth(640);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        Label eventName = new Label(b.getEvent().getName());
        eventName.getStyleClass().add("title-label");

        Label info = new Label("Locale: " + b.getEvent().getLocalName() + " | " + b.getEvent().getDateTime().format(fmt));
        info.getStyleClass().add("info-text");

        Label ticketInfo = new Label("Codice Tkt: " + b.getTicketCode() + " | Tipo: " + b.getTicketType());
        ticketInfo.getStyleClass().add("small-label");
        ticketInfo.setStyle("-fx-text-fill: #9b59b6;"); // Viola NightFlow

        HBox actions = new HBox();
        actions.setAlignment(Pos.CENTER_RIGHT);
        if (cancellable && onCancel != null) {
            Button cancelBtn = new Button("Annulla Prenotazione");
            cancelBtn.getStyleClass().add("danger-button");
            cancelBtn.setOnAction(e -> onCancel.accept(b));
            actions.getChildren().add(cancelBtn);
        }

        card.getChildren().addAll(eventName, info, ticketInfo, actions);
        return card;
    }

    private Label emptyLabel(String text) {
        Label lbl = new Label(text); lbl.getStyleClass().add("register-label"); return lbl;
    }
}