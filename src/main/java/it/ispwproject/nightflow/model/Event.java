package it.ispwproject.nightflow.model;

import java.time.LocalDateTime;

public class Event {

    private int id;
    private String name;
    private String description;
    private LocalDateTime dateTime;
    private String location;

    // --- NUOVI ATTRIBUTI ---
    private String localName;
    private int organizerId;
    // -----------------------

    private int totalCapacity;
    private int availableTickets;
    private double price;

    public Event() {}

    // Costruttore aggiornato con i nuovi campi
    public Event(int id, String name, String description, LocalDateTime dateTime,
                 String location, String localName, int totalCapacity, double price, int organizerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dateTime = dateTime;
        this.location = location;
        this.localName = localName;
        this.totalCapacity = totalCapacity;
        this.availableTickets = totalCapacity;
        this.price = price;
        this.organizerId = organizerId;
    }

    public boolean bookTickets(int quantity) {
        if (this.availableTickets >= quantity) {
            this.availableTickets -= quantity;
            return true;
        }
        return false;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    // --- Getters e Setters per i nuovi attributi ---
    public String getLocalName() { return localName; }
    public void setLocalName(String localName) { this.localName = localName; }

    public int getOrganizerId() { return organizerId; }
    public void setOrganizerId(int organizerId) { this.organizerId = organizerId; }
    // -----------------------------------------------

    public int getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(int totalCapacity) { this.totalCapacity = totalCapacity; }

    public int getAvailableTickets() { return availableTickets; }
    public void setAvailableTickets(int availableTickets) { this.availableTickets = availableTickets; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}