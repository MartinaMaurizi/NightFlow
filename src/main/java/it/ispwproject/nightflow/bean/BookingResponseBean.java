package it.ispwproject.nightflow.bean;

import it.ispwproject.nightflow.enumerator.BookingStatus;

public class BookingResponseBean {

    private int id;
    private BookingStatus status;
    private String ticketCode; // Il codice generato (es. NF-TKT-XXXX)
    private ClientBean client;
    private EventBean event;
    private String ticketType;

    public BookingResponseBean() {}

    public BookingResponseBean(int id, BookingStatus status, String ticketCode,
                               ClientBean client, EventBean event, String ticketType) {
        this.id = id;
        this.status = status;
        this.ticketCode = ticketCode;
        this.client = client;
        this.event = event;
        this.ticketType = ticketType;
    }

    // Getter e Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public String getTicketCode() { return ticketCode; }
    public void setTicketCode(String ticketCode) { this.ticketCode = ticketCode; }

    public ClientBean getClient() { return client; }
    public void setClient(ClientBean client) { this.client = client; }

    public EventBean getEvent() { return event; }
    public void setEvent(EventBean event) { this.event = event; }

    public String getTicketType() { return ticketType; }
    public void setTicketType(String ticketType) { this.ticketType = ticketType; }


}