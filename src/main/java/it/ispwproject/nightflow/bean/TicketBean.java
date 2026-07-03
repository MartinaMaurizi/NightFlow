package it.ispwproject.nightflow.bean;

import it.ispwproject.nightflow.exception.BookingException;

public class TicketBean {

    private String typeName;
    private Double price;
    private Integer minimumAge;
    private String description;

    public TicketBean() {}

    // ─── Setters con validazione ──────────────────────────────────────────

    public void setTypeName(String typeName) throws BookingException {
        if (typeName == null || typeName.isBlank()) {
            throw new BookingException("Il nome del tipo di biglietto non può essere vuoto.");
        } else if (typeName.length() > 45) {
            throw new BookingException("Il nome del tipo di biglietto è troppo lungo (max 45 caratteri).");
        }
        this.typeName = typeName;
    }

    public void setPrice(Double price) throws BookingException {
        if (price == null || price < 0) {
            throw new BookingException("Il prezzo non può essere negativo.");
        }
        this.price = price;
    }

    public void setMinimumAge(Integer minimumAge) throws BookingException {
        if (minimumAge != null && minimumAge < 0) {
            throw new BookingException("L'età minima non può essere negativa.");
        }
        this.minimumAge = minimumAge;
    }

    public void setDescription(String description) throws BookingException {
        if (description == null || description.isBlank()) {
            throw new BookingException("La descrizione non può essere vuota.");
        } else if (description.length() > 255) {
            throw new BookingException("La descrizione è troppo lunga (max 255 caratteri).");
        }
        this.description = description;
    }

    // ─── Getters ──────────────────────────────────────────────────────────

    public String getTypeName() { return typeName; }
    public Double getPrice() { return price; }
    public Integer getMinimumAge() { return minimumAge; }
    public String getDescription() { return description; }
}