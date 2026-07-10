package it.ispwproject.nightflow.pattern.observer;

import it.ispwproject.nightflow.bean.BookingResponseBean;
import it.ispwproject.nightflow.bean.ClientBean;
import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.model.Booking;
import it.ispwproject.nightflow.service.NotificationService;
import it.ispwproject.nightflow.util.logger.AppLogger;

public class EventCancellationObserver implements Observer {
    private final Booking booking;

    public EventCancellationObserver(Booking booking) {
        this.booking = booking;
    }

    @Override
    public void update() {
        try {
            BookingResponseBean response = buildResponse();
            // Richiama il nuovo metodo che abbiamo aggiunto in NotificationService
            NotificationService.sendEventCancellation(
                    booking.getClient().getEmail(),
                    response
            );
        } catch (Exception e) {
            AppLogger.logWarning("Notifica di cancellazione evento non inviata a "
                    + booking.getClient().getEmail() + ": " + e.getMessage());
        }
    }

    private BookingResponseBean buildResponse() {
        ClientBean clientBean = new ClientBean(
                booking.getClient().getId(),
                booking.getClient().getName(),
                booking.getClient().getSurname(),
                booking.getClient().getEmail()
        );

        EventBean eventBean = new EventBean();
        eventBean.setId(booking.getEvent().getId());
        eventBean.setName(booking.getEvent().getName());
        eventBean.setDescription(booking.getEvent().getDescription());
        eventBean.setDateTime(booking.getEvent().getDateTime());
        eventBean.setLocation(booking.getEvent().getLocation());
        eventBean.setLocalName(booking.getEvent().getLocalName());
        eventBean.setAvailableTickets(booking.getEvent().getAvailableTickets());
        eventBean.setPrice(booking.getEvent().getPrice());

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