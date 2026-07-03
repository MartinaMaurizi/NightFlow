package it.ispwproject.nightflow.bean;

import java.time.LocalDateTime;

public class EventBean {
    private int id;
    private String name;
    private String description;
    private LocalDateTime dateTime;
    private String location;
    private String localName; // Aggiunto per collegare subito il locale
    private int availableTickets;
    private double price;

    public EventBean() {}

    public EventBean(int id, String name, String description, LocalDateTime dateTime,
                     String location, String localName, int availableTickets, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dateTime = dateTime;
        this.location = location;
        this.localName = localName;
        this.availableTickets = availableTickets;
        this.price = price;
    }

    // Getters e Setters
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

    public String getLocalName() { return localName; }
    public void setLocalName(String localName) { this.localName = localName; }

    public int getAvailableTickets() { return availableTickets; }
    public void setAvailableTickets(int availableTickets) { this.availableTickets = availableTickets; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}