package it.ispwproject.nightflow.pattern.observer;


import it.ispwproject.nightflow.bean.BookingResponseBean;
import it.ispwproject.nightflow.bean.ClientBean;
import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.model.Booking;
import it.ispwproject.nightflow.service.NotificationService;
import it.ispwproject.nightflow.util.logger.AppLogger;


public class EventModificationObserver implements Observer {
    private final Booking booking;
    private final String changeDescription; // es: "La data è stata spostata al 15 Luglio"

    public EventModificationObserver(Booking booking, String changeDescription) {
        this.booking = booking;
        this.changeDescription = changeDescription;
    }

    @Override
    public void update() {
        try {
            BookingResponseBean response = buildResponse();
            // Passiamo anche la descrizione della modifica al template
            NotificationService.sendEventModification(
                    booking.getClient().getEmail(),
                    response,
                    changeDescription);
        } catch (Exception e) {
            AppLogger.logWarning("Notifica modifica non inviata: " + e.getMessage());
        }
    }
    private BookingResponseBean buildResponse() {
        ClientBean clientBean = new ClientBean(
                booking.getClient().getId(),
                booking.getClient().getName(),
                booking.getClient().getSurname(),
                booking.getClient().getEmail()
        );

        // Creazione dell'EventBean a prova di SonarCloud (usando i setter)
        EventBean eventBean = new EventBean();
        eventBean.setId(booking.getEvent().getId());
        eventBean.setName(booking.getEvent().getName());
        eventBean.setDescription(booking.getEvent().getDescription());
        eventBean.setDateTime(booking.getEvent().getDateTime());
        eventBean.setLocation(booking.getEvent().getLocation());
        eventBean.setLocalName(booking.getEvent().getLocalName());
        eventBean.setAvailableTickets(booking.getEvent().getAvailableTickets());
        eventBean.setPrice(booking.getEvent().getPrice());

        // Assicurati di passare anche il ticketType (6° parametro)
        return new BookingResponseBean(
                booking.getId(),
                booking.getStatus(),
                booking.getTicketCode(),
                clientBean,
                eventBean,
                booking.getTicketType(),
                booking.getPaymentMethod()
        );
    }
}