package it.ispwproject.nightflow.pattern.observer;

import it.ispwproject.nightflow.bean.*;
import it.ispwproject.nightflow.service.NotificationService;
import it.ispwproject.nightflow.model.Booking;
import it.ispwproject.nightflow.util.logger.AppLogger;

public class BookingCancellationObserver implements Observer {

    private final Booking booking;

    public BookingCancellationObserver(Booking booking) {
        this.booking = booking;
    }

    @Override
    public void update() {
        try {
            BookingResponseBean response = buildResponse();

            // Stile identico a BrainBank
            NotificationService.sendBookingCancellation(
                    booking.getClient().getEmail(),
                    response);

            NotificationService.sendBookingCancellationToOrganizer(
                    booking.getOrganizer().getEmail(),
                    response);

        } catch (Exception e) {
            AppLogger.logWarning("Notifica cancellazione non inviata: " + e.getMessage());
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