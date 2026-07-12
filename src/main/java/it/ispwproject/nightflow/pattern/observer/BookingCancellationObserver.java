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
            // 1. Controllo sicurezza Cliente
            if (booking.getClient() == null || booking.getClient().getEmail() == null) {
                AppLogger.logWarning("Dati cliente mancanti. Impossibile inviare email.");
                return;
            }

            BookingResponseBean response = buildResponse();

            // 2. Invio email al Cliente (QUESTA ORA PARTIRÀ SICURAMENTE)
            NotificationService.sendBookingCancellation(
                    booking.getClient().getEmail(),
                    response);

            // 3. Controllo sicurezza Organizzatore anti-crash
            if (booking.getOrganizer() != null && booking.getOrganizer().getEmail() != null) {
                NotificationService.sendBookingCancellationToOrganizer(
                        booking.getOrganizer().getEmail(),
                        response);
            } else {
                AppLogger.logWarning("Email organizzatore non trovata nell'oggetto Booking, notifica saltata.");
            }

        } catch (Exception e) {
            // L'errore viene loggato correttamente qui
            AppLogger.logWarning("Notifica cancellazione non inviata: " + e.getMessage());
            // Rimosso e.printStackTrace() per risolvere l'errore di SonarQube
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