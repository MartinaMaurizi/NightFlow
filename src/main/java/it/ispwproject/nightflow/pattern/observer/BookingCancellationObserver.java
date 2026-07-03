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

        // Aggiungi localName come parametro!
        EventBean eventBean = new EventBean(
                booking.getEvent().getId(),
                booking.getEvent().getName(),
                booking.getEvent().getDescription(),
                booking.getEvent().getDateTime(),
                booking.getEvent().getLocation(),
                booking.getEvent().getLocalName(),
                booking.getEvent().getAvailableTickets(),
                booking.getEvent().getPrice()
        );

        // Aggiungi anche il ticketType qui, per avere 6 parametri
        return new BookingResponseBean(
                booking.getId(),
                booking.getStatus(),
                booking.getTicketCode(),
                clientBean,
                eventBean,
                booking.getTicketType()
        );
    }
}