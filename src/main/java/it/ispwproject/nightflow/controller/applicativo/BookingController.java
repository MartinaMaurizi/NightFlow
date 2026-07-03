package it.ispwproject.nightflow.controller.applicativo;

import it.ispwproject.nightflow.bean.*;
import it.ispwproject.nightflow.dao.*;
import it.ispwproject.nightflow.enumerator.BookingStatus;
import it.ispwproject.nightflow.enumerator.PaymentMethod;
import it.ispwproject.nightflow.exception.BookingException;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.*;
import it.ispwproject.nightflow.pattern.observer.BookingCancellationObserver;
import it.ispwproject.nightflow.pattern.observer.BookingConfirmationObserver;
import it.ispwproject.nightflow.service.TicketCodeService;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class BookingController {

    private final BookingDAO bookingDAO;
    private final EventDAO   eventDAO;

    public BookingController() {
        this.bookingDAO = DAOFactory.getBookingDAO();
        this.eventDAO   = DAOFactory.getEventDAO();
    }

    // --- METODO AIUTANTE: Evita il codice duplicato e usa i setter di EventBean ---
    private EventBean createEventBean(Event event) {
        if (event == null) return null;
        EventBean bean = new EventBean();
        bean.setId(event.getId());
        bean.setName(event.getName());
        bean.setDescription(event.getDescription());
        bean.setDateTime(event.getDateTime());
        bean.setLocation(event.getLocation());
        bean.setLocalName(event.getLocalName());
        bean.setAvailableTickets(event.getAvailableTickets());
        bean.setPrice(event.getPrice());
        return bean;
    }

    public List<EventBean> getAvailableEvents() throws DAOException {
        List<EventBean> result = new ArrayList<>();
        for (Event event : eventDAO.getAllUpcomingEvents()) {
            // Usa il metodo aiutante!
            result.add(createEventBean(event));
        }
        return result;
    }

    public BookingResponseBean prepareBookingSummary(BookingRequestBean request)
            throws DAOException, BookingException {

        Event event = eventDAO.findById(request.getEvent().getId());
        if (event == null) throw new DAOException("Evento non trovato nel sistema.");

        if (event.getAvailableTickets() <= 0) {
            throw new BookingException("Mi dispiace, i biglietti per questo evento sono esauriti!");
        }

        User loggedUser = SessionManager.getInstance().getLoggedUser();

        for (Booking b : bookingDAO.findAll()) {
            if (b.getEvent().getId() == event.getId() && b.getStatus() != BookingStatus.CANCELLED
                    && loggedUser != null && b.getClient().getId() != loggedUser.getId()) {
                throw new BookingException("Biglietto già in fase di prenotazione o occupato!");
            }
        }

        return new BookingResponseBean(
                0, BookingStatus.PENDING, null, null,
                createEventBean(event), // Usa il metodo aiutante!
                request.getTicketType()
        );
    }

    public BookingResponseBean createBooking(BookingRequestBean request, PaymentMethod method)
            throws DAOException, BookingException {

        User user = SessionManager.getInstance().getLoggedUser();
        if (!(user instanceof Client client)) {
            throw new DAOException("Solo i Clienti registrati possono prenotare i biglietti.");
        }

        Event event = eventDAO.findById(request.getEvent().getId());
        if (event == null) throw new DAOException("Evento non trovato.");

        if (event.getAvailableTickets() <= 0) {
            throw new BookingException("Mi dispiace, i biglietti per questo evento sono esauriti!");
        }

        for (Booking b : bookingDAO.findByClient(client.getId())) {
            if (b.getStatus() == BookingStatus.CANCELLED) continue;
            if (b.getEvent().getId() == event.getId()) {
                throw new BookingException("Hai già una prenotazione attiva per questo evento.");
            }
        }

        Booking booking = new Booking(client, event);
        booking.setPaymentMethod(method);
        booking.setTicketType(request.getTicketType());
        booking.setTicketCode(TicketCodeService.generate());

        bookingDAO.save(booking);

        booking.attach(new BookingConfirmationObserver(booking));
        booking.confirm();

        return new BookingResponseBean(
                booking.getId(), booking.getStatus(), booking.getTicketCode(),
                new ClientBean(client.getId(), client.getName(), client.getSurname(), client.getEmail()),
                createEventBean(event), // Usa il metodo aiutante!
                booking.getTicketType()
        );
    }

    public List<BookingResponseBean> getClientBookings(int clientId) throws DAOException {
        List<BookingResponseBean> result = new ArrayList<>();
        for (Booking booking : bookingDAO.findUpcomingByClient(clientId)) {
            Event event = booking.getEvent();
            if (event == null) continue;

            result.add(new BookingResponseBean(
                    booking.getId(), booking.getStatus(), booking.getTicketCode(),
                    new ClientBean(booking.getClient().getId(), booking.getClient().getName(), booking.getClient().getSurname(), booking.getClient().getEmail()),
                    createEventBean(event), // Usa il metodo aiutante!
                    booking.getTicketType()
            ));
        }
        return result;
    }

    public List<BookingResponseBean> getClientPastBookings(int clientId) throws DAOException {
        List<BookingResponseBean> result = new ArrayList<>();
        for (Booking booking : bookingDAO.findPastByClient(clientId)) {
            Event event = booking.getEvent();
            if (event == null) continue;

            result.add(new BookingResponseBean(
                    booking.getId(), booking.getStatus(), booking.getTicketCode(),
                    new ClientBean(booking.getClient().getId(), booking.getClient().getName(), booking.getClient().getSurname(), booking.getClient().getEmail()),
                    createEventBean(event), // Usa il metodo aiutante!
                    booking.getTicketType()
            ));
        }
        return result;
    }

    public void cancelBooking(int bookingId, int clientId) throws DAOException {
        List<Booking> bookings = bookingDAO.findByClient(clientId);
        Booking booking = bookings.stream()
                .filter(b -> b.getId() == bookingId)
                .findFirst()
                .orElse(null);

        if (booking != null) {
            booking.attach(new BookingCancellationObserver(booking));
        }

        bookingDAO.cancel(bookingId, clientId);
    }

    public List<ClientBean> getAllParticipants() throws DAOException {
        List<ClientBean> result = new ArrayList<>();
        List<Integer> addedClientIds = new ArrayList<>();

        for (Booking booking : bookingDAO.findAll()) {
            User client = booking.getClient();
            if (client != null && !addedClientIds.contains(client.getId())) {
                result.add(new ClientBean(
                        client.getId(),
                        client.getName(),
                        client.getSurname(),
                        client.getEmail()
                ));
                addedClientIds.add(client.getId());
            }
        }
        return result;
    }

    public List<BookingResponseBean> getAllClientBookings(int clientId) throws DAOException {
        List<BookingResponseBean> result = new ArrayList<>();
        for (Booking booking : bookingDAO.findByClient(clientId)) {
            Event event = booking.getEvent();
            if (event == null) continue;

            result.add(new BookingResponseBean(
                    booking.getId(), booking.getStatus(), booking.getTicketCode(),
                    new ClientBean(booking.getClient().getId(), booking.getClient().getName(), booking.getClient().getSurname(), booking.getClient().getEmail()),
                    createEventBean(event), // Usa il metodo aiutante!
                    booking.getTicketType()
            ));
        }
        return result;
    }
}