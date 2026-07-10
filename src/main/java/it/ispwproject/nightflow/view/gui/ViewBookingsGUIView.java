package it.ispwproject.nightflow.view.gui;

import it.ispwproject.nightflow.bean.BookingResponseBean;
import it.ispwproject.nightflow.enumerator.PaymentMethod;
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
                             Consumer<BookingResponseBean> onCancel,
                             Consumer<BookingResponseBean> onEdit) {
        VBox content = new VBox(12);
        content.setPadding(new Insets(24));
        content.setAlignment(Pos.TOP_CENTER);

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

        // 🌟 ECCO LA MAGIA: Impedisce alla lista di sformarsi quando aggiungi nuove prenotazioni!
        listBox.setFillWidth(false);

        Runnable refreshList = () -> {
            listBox.getChildren().clear();
            if (btnConfirmed.isSelected()) {
                if (confirmed.isEmpty()) listBox.getChildren().add(emptyLabel("Nessuna prenotazione attiva."));
                else for (BookingResponseBean b : confirmed) listBox.getChildren().add(buildBookingCard(b, true, onCancel, onEdit));
            } else if (btnCancelled.isSelected()) {
                if (cancelled.isEmpty()) listBox.getChildren().add(emptyLabel("Nessuna prenotazione cancellata."));
                else for (BookingResponseBean b : cancelled) listBox.getChildren().add(buildBookingCard(b, false, null, null));
            } else {
                if (past.isEmpty()) listBox.getChildren().add(emptyLabel("Nessuna serata passata."));
                else for (BookingResponseBean b : past) listBox.getChildren().add(buildBookingCard(b, false, null, null));
            }
        };

        refreshList.run();
        btnConfirmed.setOnAction(e -> refreshList.run());
        btnCancelled.setOnAction(e -> refreshList.run());
        btnPast.setOnAction(e -> refreshList.run());

        content.getChildren().addAll(toggleBar, listBox, errorLabel);
        root.setCenter(transparentScroll(content));
    }

    private HBox buildBookingCard(BookingResponseBean b, boolean cancellable, Consumer<BookingResponseBean> onCancel, Consumer<BookingResponseBean> onEdit) {
        HBox card = new HBox(15);
        card.getStyleClass().add("info-card");

        // 🌟 IL SEGRETO 1: Forziamo TUTTE le carte ad avere esattamente questa larghezza!
        card.setPrefWidth(640);
        card.setMaxWidth(640);
        card.setAlignment(Pos.CENTER_LEFT); // Tutto allineato a sinistra (centrato in verticale)
        card.setPadding(new Insets(15, 20, 15, 20));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // ── PARTE SINISTRA: I TESTI ─────────────────────────────────
        VBox textContainer = new VBox(6);
        textContainer.setAlignment(Pos.CENTER_LEFT);

        Label eventName = new Label(b.getEvent().getName());
        eventName.getStyleClass().add("title-label");
        eventName.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label info = new Label("Locale: " + b.getEvent().getLocalName() + " | " + b.getEvent().getDateTime().format(fmt));
        info.getStyleClass().add("info-text");
        info.setStyle("-fx-text-fill: #333333;");

        Label ticketInfo = new Label("Codice Tkt: " + b.getTicketCode() + " | Tipo: " + b.getTicketType());
        ticketInfo.getStyleClass().add("small-label");
        ticketInfo.setStyle("-fx-text-fill: #9b59b6; -fx-font-weight: bold;");

        textContainer.getChildren().addAll(eventName, info, ticketInfo);

        // 🌟 La molla che spinge i bottoni a destra
        HBox.setHgrow(textContainer, Priority.ALWAYS);

// ── PARTE DESTRA: I BOTTONI UNO SOPRA L'ALTRO ───────────────
        VBox actions = new VBox(8);
        actions.setAlignment(Pos.CENTER_RIGHT);

        if (cancellable) {
            // Controlla il metodo di pagamento
            boolean isPaidOnline = b.getPaymentMethod() == PaymentMethod.PAYPAL ||
                    b.getPaymentMethod() == PaymentMethod.CREDIT_CARD;

            if (!isPaidOnline) {
                if (onEdit != null) {
                    Button editBtn = new Button("Modifica Prenotazione");
                    editBtn.getStyleClass().add("btn-viola-small");
                    // 🌟 IL SEGRETO 2: Forziamo i bottoni ad avere la stessa larghezza
                    editBtn.setPrefWidth(160);
                    editBtn.setOnAction(e -> onEdit.accept(b));
                    actions.getChildren().add(editBtn);
                }
            } else {
                Label paidBadge = new Label("✔ Pagato (" + b.getPaymentMethod().name() + ")");
                paidBadge.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold; -fx-font-size: 14px;");
                actions.getChildren().add(paidBadge);
            }

            // 🌟 Aggiungi il pulsante Annulla SOLO SE non è stato pagato online
            if (onCancel != null && !isPaidOnline) {
                Button cancelBtn = new Button("Annulla Prenotazione");
                cancelBtn.getStyleClass().add("danger-button");
                // 🌟 IL SEGRETO 2: Stessa larghezza anche per il tasto rosso!
                cancelBtn.setPrefWidth(160);
                cancelBtn.setOnAction(e -> onCancel.accept(b));
                actions.getChildren().add(cancelBtn);
            }
        }

        card.getChildren().addAll(textContainer, actions);
        return card;
    }
    private Label emptyLabel(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("register-label");
        return lbl;
    }
}