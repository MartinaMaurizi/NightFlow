package it.ispwproject.nightflow.view.gui;

import it.ispwproject.nightflow.bean.BookingResponseBean;
import it.ispwproject.nightflow.bean.ClientBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.function.Consumer;

public class ManageParticipantsGUIView extends PageGUIView {

    public final ComboBox<ClientBean> participantCombo = new ComboBox<>();
    public final Label                errorLabel       = buildErrorLabel();

    public ManageParticipantsGUIView() {
        participantCombo.getStyleClass().add("combo-box");
        participantCombo.setPromptText("Cerca partecipante...");
        participantCombo.setMaxWidth(Double.MAX_VALUE);
        participantCombo.setButtonCell(participantCell());
        participantCombo.setCellFactory(lv -> participantCell());
    }

    public BorderPane buildRoot(Runnable onBack) {
        BorderPane root = buildShell("Gestione Partecipanti", onBack);

        VBox content = new VBox(12);
        content.setPadding(new Insets(28, 48, 28, 48));
        content.setAlignment(Pos.TOP_CENTER);

        VBox selectorCard = new VBox(10);
        selectorCard.getStyleClass().add("info-card");
        selectorCard.setMaxWidth(720);
        selectorCard.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label("Seleziona partecipante");
        label.getStyleClass().add("small-label");
        selectorCard.getChildren().addAll(label, participantCombo);

        VBox participantCard = new VBox(8);
        participantCard.setMaxWidth(720);
        participantCard.setVisible(false);
        participantCard.setManaged(false);

        participantCombo.setUserData(participantCard);
        content.getChildren().addAll(selectorCard, participantCard, errorLabel);

        ScrollPane scroll = new ScrollPane(content);
        scroll.getStyleClass().add("transparent-scroll");
        scroll.setFitToWidth(true);
        root.setCenter(scroll);
        return root;
    }

    public VBox getParticipantCard() {
        return (VBox) participantCombo.getUserData();
    }

    public void buildParticipantCard(VBox card, ClientBean client,
                                     List<BookingResponseBean> bookings,
                                     Consumer<BookingResponseBean> onCheckIn) {
        card.getChildren().clear();
        card.setVisible(true);
        card.setManaged(true);

        // Header Partecipante
        HBox header = new HBox(12);
        header.getStyleClass().add("info-card");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12));

        Label avatar = new Label(String.valueOf(client.getName().charAt(0)).toUpperCase());
        avatar.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 20; -fx-min-width: 40; -fx-alignment: center;");

        VBox info = new VBox(2, new Label(client.getFullName()), new Label(client.getEmail()));
        header.getChildren().addAll(avatar, info);

        // Lista Prenotazioni
        VBox bookingsBox = new VBox(8);
        bookingsBox.getStyleClass().add("info-card");
        bookingsBox.getChildren().add(new Label("Prenotazioni ai tuoi eventi"));

        if (bookings.isEmpty()) {
            bookingsBox.getChildren().add(new Label("Nessuna prenotazione trovata."));
        } else {
            for (BookingResponseBean b : bookings) {
                HBox bRow = new HBox(10);
                bRow.setAlignment(Pos.CENTER_LEFT);
                Label bInfo = new Label(b.getEvent().getName() + " - " + b.getTicketType());

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Button checkInBtn = new Button("Check-in");
                checkInBtn.getStyleClass().add("save-button");
                checkInBtn.setOnAction(e -> onCheckIn.accept(b));

                bRow.getChildren().addAll(bInfo, spacer, checkInBtn);
                bookingsBox.getChildren().add(bRow);
            }
        }

        card.getChildren().addAll(header, bookingsBox);
    }

    private ListCell<ClientBean> participantCell() {
        return new ListCell<>() {
            @Override protected void updateItem(ClientBean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFullName() + " (" + item.getEmail() + ")");
            }
        };
    }
}