package it.ispwproject.nightflow.bean;

public class BookingRequestBean {

    private ClientBean client;
    private EventBean  event;
    private String     ticketType; // "Senza drink", "Con drink", "VIP"

    public BookingRequestBean() {}

    public BookingRequestBean(ClientBean client, EventBean event, String ticketType) {
        this.client     = client;
        this.event      = event;
        this.ticketType = ticketType;
    }

    // Getter e Setter
    public ClientBean getClient() { return client; }
    public void setClient(ClientBean client) { this.client = client; }

    public EventBean getEvent() { return event; }
    public void setEvent(EventBean event) { this.event = event; }

    public String getTicketType() { return ticketType; }
    public void setTicketType(String ticketType) { this.ticketType = ticketType; }
}