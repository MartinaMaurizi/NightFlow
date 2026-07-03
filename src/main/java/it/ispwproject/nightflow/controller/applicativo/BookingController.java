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
    private final ClientDAO  clientDAO;

    public BookingController() {
        this.bookingDAO = DAOFactory.getBookingDAO();
        this.eventDAO   = DAOFactory.getEventDAO();
        this.clientDAO  = DAOFactory.getClientDAO();
    }

    public List<EventBean> getAvailableEvents() throws DAOException {
        List<EventBean> result = new ArrayList<>();
        for (Event event : eventDAO.getAllUpcomingEvents()) {
            result.add(new EventBean(
                    event.getId(),
                    event.getName(),
                    event.getDescription(),
                    event.getDateTime(),
                    event.getLocation(),
                    event.getLocalName(),
                    event.getAvailableTickets(),
                    event.getPrice()
            ));
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

        // Recuperiamo l'utente loggato in modo sicuro
        User loggedUser = SessionManager.getInstance().getLoggedUser();

        // Verifichiamo se esistono già prenotazioni per questo evento
        for (Booking b : bookingDAO.findAll()) {
            // Se la prenotazione riguarda lo stesso evento E non è cancellata...
            if (b.getEvent().getId() == event.getId() && b.getStatus() != BookingStatus.CANCELLED) {

                // ...MA verifichiamo che non sia la prenotazione dello stesso utente (che è già in corso)
                // Se l'ID utente è diverso, allora c'è un conflitto con un altro cliente!
                if (loggedUser != null && b.getClient().getId() != loggedUser.getId()) {
                    throw new BookingException("Biglietto già in fase di prenotazione o occupato!");
                }
            }
        }

        return new BookingResponseBean(
                0, BookingStatus.PENDING, null, null,
                new EventBean(event.getId(), event.getName(), event.getDescription(),
                        event.getDateTime(), event.getLocation(), event.getLocalName(),
                        event.getAvailableTickets(), event.getPrice()),
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

        // Verifica prenotazione duplicata
        for (Booking b : bookingDAO.findByClient(client.getId())) {
            if (b.getStatus() == BookingStatus.CANCELLED) continue;
            if (b.getEvent().getId() == event.getId()) {
                throw new BookingException("Hai già una prenotazione attiva per questo evento.");
            }
        }

        Booking booking = new Booking(client, event);
        booking.setPaymentMethod(method);
        booking.setTicketType(request.getTicketType()); // Salvataggio del tipo scelto
        booking.setTicketCode(TicketCodeService.generate());

        bookingDAO.save(booking);

        booking.attach(new BookingConfirmationObserver(booking));
        booking.confirm();

        return new BookingResponseBean(
                booking.getId(), booking.getStatus(), booking.getTicketCode(),
                new ClientBean(client.getId(), client.getName(), client.getSurname(), client.getEmail()),
                new EventBean(event.getId(), event.getName(), event.getDescription(),
                        event.getDateTime(), event.getLocation(), event.getLocalName(),
                        event.getAvailableTickets(), event.getPrice()),
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
                    new EventBean(event.getId(), event.getName(), event.getDescription(), event.getDateTime(), event.getLocation(), event.getLocalName(), event.getAvailableTickets(), event.getPrice()),
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
                    new EventBean(event.getId(), event.getName(), event.getDescription(), event.getDateTime(), event.getLocation(), event.getLocalName(), event.getAvailableTickets(), event.getPrice()),
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

        // Recupera tutte le prenotazioni
        for (Booking booking : bookingDAO.findAll()) {

            // CAMBIO QUI: Usiamo User al posto di Client, oppure var se usi Java 10+
            User client = booking.getClient();

            // Se il cliente esiste e non è ancora stato aggiunto alla lista
            if (client != null && !addedClientIds.contains(client.getId())) {
                result.add(new ClientBean(
                        client.getId(),
                        client.getName(),
                        client.getSurname(),
                        client.getEmail()
                ));
                addedClientIds.add(client.getId()); // Memorizziamo l'ID per non metterlo due volte
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
                    new EventBean(event.getId(), event.getName(), event.getDescription(), event.getDateTime(), event.getLocation(), event.getLocalName(), event.getAvailableTickets(), event.getPrice()),
                    booking.getTicketType()
            ));
        }
        return result;
    }
}