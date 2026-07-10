package it.ispwproject.nightflow.view.gui;

import it.ispwproject.nightflow.bean.BookingResponseBean;
import it.ispwproject.nightflow.bean.ClientBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class ManageParticipantsGUIView extends PageGUIView {

    public final Label errorLabel = buildErrorLabel();
    private final VBox participantsContainer = new VBox(20);

    public BorderPane buildRoot(Runnable onBack, Runnable onLogout, Runnable onProfile, Runnable onHome) {
        BorderPane root = buildShell("Gestione Partecipanti", onBack, onLogout, onProfile, onHome);

        participantsContainer.setPadding(new Insets(28, 48, 48, 48));
        participantsContainer.setAlignment(Pos.TOP_CENTER);

        VBox wrapper = new VBox(10, participantsContainer, errorLabel);
        wrapper.setAlignment(Pos.TOP_CENTER);

        root.setCenter(transparentScroll(wrapper));
        return root;
    }

    public void clearList() { participantsContainer.getChildren().clear(); }

    public void showEmptyMessage() {
        Label emptyLabel = new Label("Nessun partecipante ha ancora prenotato le tue serate.");
        emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-style: italic;");
        participantsContainer.getChildren().add(emptyLabel);
    }

    public void addParticipantCard(ClientBean client, List<BookingResponseBean> active, List<BookingResponseBean> past) {
        VBox card = new VBox(12);
        card.getStyleClass().add("info-card");
        card.setMaxWidth(720);
        card.setPadding(new Insets(20));

        // Header (Avatar e Info)
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        Label avatar = new Label(String.valueOf(client.getName().charAt(0)).toUpperCase());
        avatar.setStyle("-fx-background-color: #651fff; -fx-text-fill: white; -fx-padding: 15; -fx-background-radius: 30; -fx-min-width: 50; -fx-min-height: 50; -fx-alignment: center; -fx-font-size: 18px; -fx-font-weight: bold;");

        VBox info = new VBox(3);

        // 🌟 Nome cliente nero
        Label nameLabel = new Label(client.getFullName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #000000;");

        // 🌟 Email cliente nera
        Label emailLabel = new Label("Email: " + client.getEmail());
        emailLabel.setStyle("-fx-text-fill: #000000;");

        info.getChildren().addAll(nameLabel, emailLabel);
        header.getChildren().addAll(avatar, info);

        // Sezione Attive
        VBox bookingsBox = new VBox(10);

        // 1. Crea la Label
        Label activeTitle = new Label("Prenotazioni Attive:");

        // 2. Imposta il colore nero (e magari anche il grassetto per farla risaltare)
        activeTitle.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");

        // 3. Aggiungila al contenitore
        bookingsBox.getChildren().add(activeTitle);

        for (BookingResponseBean b : active) bookingsBox.getChildren().add(buildBookingRow(b, false));

        // Sezione Passate
        if (!past.isEmpty()) {
            Label pastTitle = new Label("Prenotazioni Passate:");
            pastTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #999; -fx-padding: 10 0 0 0;");
            bookingsBox.getChildren().add(pastTitle);
            for (BookingResponseBean b : past) bookingsBox.getChildren().add(buildBookingRow(b, true));
        }

        card.getChildren().addAll(header, new Separator(), bookingsBox);
        participantsContainer.getChildren().add(card);
    }

    private HBox buildBookingRow(BookingResponseBean b, boolean isPast) {
        HBox bRow = new HBox(10);
        bRow.setAlignment(Pos.CENTER_LEFT);
        // Sfondo leggermente più scuro per le passate per contrastare bene col nero
        bRow.setStyle("-fx-background-color: " + (isPast ? "#e0e0e0" : "#f3e8ff") + "; -fx-padding: 10; -fx-background-radius: 8;");

        VBox info = new VBox(2);

        // Titolo dell'evento: Nero (#000000)
        Label name = new Label(b.getEvent().getName());
        name.setStyle("-fx-font-weight: bold; -fx-text-fill: #000000; -fx-font-size: 14px;");

        // Tipo biglietto: Nero (#000000)
        Label ticket = new Label("Biglietto: " + (b.getTicketType() != null ? b.getTicketType() : "Standard"));
        ticket.setStyle("-fx-text-fill: #000000; -fx-font-size: 12px;");

        info.getChildren().addAll(name, ticket);
        bRow.getChildren().add(info);
        return bRow;
    }
}