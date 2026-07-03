package it.ispwproject.nightflow.model;

import java.time.LocalDateTime;

public class ClientRequest {

    private int id;

    private Organizer organizer;
    private Client client;

    private String description;
    private boolean completed;
    private LocalDateTime createdAt;

    public ClientRequest() {}

    public ClientRequest(Organizer organizer, Client client, String description) {
        this.organizer   = organizer;
        this.client      = client;
        this.description = description; // Es: "Richiesta tavolo VIP per 5 persone"
        this.completed   = false;
    }

    // Segna la richiesta come gestita/risolta
    public void complete() {
        this.completed = true;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Organizer getOrganizer() { return organizer; }
    public void setOrganizer(Organizer organizer) { this.organizer = organizer; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}