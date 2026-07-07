package it.ispwproject.nightflow.pattern.observer;

import it.ispwproject.nightflow.bean.*;
import it.ispwproject.nightflow.dao.DAOFactory; // Necessario per recuperare l'OrganizerDAO
import it.ispwproject.nightflow.service.NotificationService;
import it.ispwproject.nightflow.model.Booking;
import it.ispwproject.nightflow.model.Organizer; // Importa il modello Organizer
import it.ispwproject.nightflow.util.logger.AppLogger;

public class BookingConfirmationObserver implements Observer {

    private final Booking booking;

    public BookingConfirmationObserver(Booking booking) {
        this.booking = booking;
    }

    @Override
    public void update() {
        try {
            BookingResponseBean response = buildResponse();

            // 1. Invia conferma al cliente
            NotificationService.sendBookingConfirmation(
                    booking.getClient().getEmail(),
                    response);

            // 2. Recupera l'organizzatore tramite ID e invia notifica
            int organizerId = booking.getEvent().getOrganizerId();
            Organizer organizer = DAOFactory.getOrganizerDAO().findById(organizerId);

            if (organizer != null) {
                NotificationService.sendBookingConfirmationToOrganizer(
                        organizer.getEmail(),
                        response);
            }

        } catch (Exception e) {
            AppLogger.logWarning("Notifica di conferma non inviata: " + e.getMessage());
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