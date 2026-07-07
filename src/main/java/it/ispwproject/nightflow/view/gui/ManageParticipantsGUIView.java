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

        // 🌟 MAGIA: Una sola riga costruisce Sfondo, Navbar, Tasto Indietro e i 3 Bottoni a destra!
        BorderPane root = buildShell("Gestione Partecipanti", onBack, onLogout, onProfile, onHome);

        // -- Contenuto Centrale --
        participantsContainer.setPadding(new Insets(28, 48, 48, 48));
        participantsContainer.setAlignment(Pos.TOP_CENTER);

        VBox wrapper = new VBox(10, participantsContainer, errorLabel);
        wrapper.setAlignment(Pos.TOP_CENTER);

        // Usiamo il metodo transparentScroll del padre
        ScrollPane scroll = transparentScroll(wrapper);
        root.setCenter(scroll);

        return root;
    }

    public void clearList() {
        participantsContainer.getChildren().clear();
    }

    public void showEmptyMessage() {
        Label emptyLabel = new Label("Nessun partecipante ha ancora prenotato le tue serate.");
        emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-style: italic;");
        participantsContainer.getChildren().add(emptyLabel);
    }

    public void addParticipantCard(ClientBean client, List<BookingResponseBean> bookings) {
        VBox card = new VBox(12);
        card.getStyleClass().add("info-card");
        card.setMaxWidth(720);
        card.setPadding(new Insets(20));

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label avatar = new Label(String.valueOf(client.getName().charAt(0)).toUpperCase());
        avatar.setStyle("-fx-background-color: #651fff; -fx-text-fill: white; -fx-padding: 15; -fx-background-radius: 30; -fx-min-width: 50; -fx-min-height: 50; -fx-alignment: center; -fx-font-size: 18px; -fx-font-weight: bold;");

        VBox info = new VBox(3);
        Label nameLabel = new Label(client.getFullName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        Label emailLabel = new Label("Email: " + client.getEmail());
        emailLabel.setStyle("-fx-text-fill: #666666;");

        info.getChildren().addAll(nameLabel, emailLabel);
        header.getChildren().addAll(avatar, info);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #e0e0e0;");

        VBox bookingsBox = new VBox(10);
        Label titleBookings = new Label("Prenotazioni Attive:");
        titleBookings.setStyle("-fx-font-weight: bold; -fx-text-fill: #651fff;");
        bookingsBox.getChildren().add(titleBookings);

        for (BookingResponseBean b : bookings) {
            HBox bRow = new HBox(10);
            bRow.setAlignment(Pos.CENTER_LEFT);
            bRow.setStyle("-fx-background-color: #f3e8ff; -fx-padding: 10; -fx-background-radius: 8;");

            VBox eventInfo = new VBox(2);
            Label eName = new Label(b.getEvent().getName());
            eName.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333; -fx-font-size: 14px;");

            String ticketType = (b.getTicketType() != null && !b.getTicketType().isEmpty()) ? b.getTicketType() : "Standard";
            Label eTicket = new Label("Biglietto: " + ticketType);
            eTicket.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

            eventInfo.getChildren().addAll(eName, eTicket);
            bRow.getChildren().add(eventInfo);

            bookingsBox.getChildren().add(bRow);
        }

        card.getChildren().addAll(header, sep, bookingsBox);
        participantsContainer.getChildren().add(card);
    }
}