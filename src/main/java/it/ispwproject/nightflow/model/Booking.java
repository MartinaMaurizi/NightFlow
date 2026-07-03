package it.ispwproject.nightflow.model;

import it.ispwproject.nightflow.enumerator.BookingStatus;
import it.ispwproject.nightflow.enumerator.PaymentMethod;
import it.ispwproject.nightflow.pattern.observer.Observable;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class Booking extends Observable {

    private int id;
    private User client;
    private User organizer;
    private Event event;

    private BookingStatus status;
    private String ticketCode;
    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt;

    // Blocco del biglietto per 15 minuti (Soft Lock temporale)
    private LocalDateTime reservedUntil;

    public Booking() {}

    public Booking(User client, Event event) {
        this.client    = client;
        this.event     = event;
        this.status    = BookingStatus.PENDING;
        this.createdAt = LocalDateTime.now(ZoneId.systemDefault());

        // Imposta un blocco di 15 minuti per permettere il pagamento online
        this.reservedUntil = this.createdAt.plusMinutes(15);
    }

    /**
     * Verifica se il blocco temporale per il pagamento è ancora valido.
     */
    public boolean isReserved() {
        return reservedUntil != null && reservedUntil.isAfter(LocalDateTime.now(ZoneId.systemDefault()));
    }

    public void confirm() {
        this.status = BookingStatus.CONFIRMED;
        notifyObservers(); // Notifica Mail API / TicketCode Service
    }

    public void cancel() {
        this.status = BookingStatus.CANCELLED;
        notifyObservers();
    }

    public boolean isExpired() {
        return this.status == BookingStatus.EXPIRED || !isReserved();
    }

    public boolean belongsTo(User u) {
        return this.client != null && this.client.getEmail().equals(u.getEmail());
    }

    // ─── Getters & Setters ────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public User getClient() { return client; }
    public void setClient(User client) { this.client = client; }
    public User getOrganizer() { return organizer; }
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public String getTicketCode() { return ticketCode; }
    public void setTicketCode(String ticketCode) { this.ticketCode = ticketCode; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getReservedUntil() { return reservedUntil; }
    public void setReservedUntil(LocalDateTime reservedUntil) { this.reservedUntil = reservedUntil; }
    private String ticketType;

    public String getTicketType() { return ticketType; }
    public void setTicketType(String ticketType) { this.ticketType = ticketType; }
}