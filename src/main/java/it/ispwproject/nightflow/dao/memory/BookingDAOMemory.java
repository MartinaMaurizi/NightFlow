package it.ispwproject.nightflow.dao.memory;

import it.ispwproject.nightflow.demo.DemoDataStore;
import it.ispwproject.nightflow.dao.BookingDAO;
import it.ispwproject.nightflow.enumerator.BookingStatus;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.Booking;
import it.ispwproject.nightflow.model.Event;
import it.ispwproject.nightflow.model.User;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingDAOMemory implements BookingDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public void save(Booking booking) throws DAOException {
        booking.setId(store.nextBookingId());
        booking.setStatus(BookingStatus.CONFIRMED);

        // Genera un codice biglietto univoco se non presente
        if (booking.getTicketCode() == null) {
            booking.setTicketCode("TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        store.getBookings().add(booking);

        // Decrementa i biglietti disponibili nell'evento in memoria
        Event event = booking.getEvent();
        if (event != null) {
            event.setAvailableTickets(Math.max(0, event.getAvailableTickets() - 1));
        }
    }

    @Override
    public List<Booking> findByClient(int clientId) throws DAOException {
        return store.getBookings().stream()
                .filter(b -> b.getClient() != null && b.getClient().getId() == clientId)
                .toList();
    }

    // Adattato per cercare i biglietti di un singolo evento
    public List<Booking> findByEvent(int eventId) throws DAOException {
        return store.getBookings().stream()
                .filter(b -> b.getEvent() != null && b.getEvent().getId() == eventId
                        && b.getStatus() == BookingStatus.CONFIRMED)
                .toList();
    }

    @Override
    public List<Booking> findAll() throws DAOException {
        return new ArrayList<>(store.getBookings());
    }

    @Override
    public List<Booking> findCompletedByClientAndOrganizer(int clientId, int organizerId) throws DAOException {
        return store.getBookings().stream()
                .filter(b -> b.getClient() != null && b.getClient().getId() == clientId
                        && b.getEvent() != null && b.getEvent().getOrganizerId() == organizerId
                        && b.getStatus() == BookingStatus.CONFIRMED
                        && b.getEvent() != null
                        && !b.getEvent().getDateTime().isAfter(LocalDateTime.now(ZoneId.systemDefault())))
                .toList();
    }

    @Override
    public List<Booking> findUpcomingByClientAndOrganizer(int clientId, int organizerId) throws DAOException {
        return store.getBookings().stream()
                .filter(b -> b.getClient() != null && b.getClient().getId() == clientId
                        && b.getEvent() != null && b.getEvent().getOrganizerId() == organizerId
                        && b.getStatus() == BookingStatus.CONFIRMED
                        && b.getEvent() != null
                        && b.getEvent().getDateTime().isAfter(LocalDateTime.now(ZoneId.systemDefault())))
                .toList();
    }

    @Override
    public List<Booking> findPastByClient(int clientId) throws DAOException {
        return store.getBookings().stream()
                .filter(b -> b.getClient() != null && b.getClient().getId() == clientId
                        && b.getStatus() == BookingStatus.CONFIRMED
                        && b.getEvent() != null
                        && b.getEvent().getDateTime().isBefore(LocalDateTime.now(ZoneId.systemDefault())))
                .toList();
    }

    @Override
    public List<Booking> findUpcomingByClient(int clientId) throws DAOException {
        return store.getBookings().stream()
                .filter(b -> b.getClient() != null && b.getClient().getId() == clientId
                        && b.getStatus() == BookingStatus.CONFIRMED
                        && b.getEvent() != null
                        && b.getEvent().getDateTime().isAfter(LocalDateTime.now(ZoneId.systemDefault())))
                .toList();
    }

    @Override
    public void updateStatus(int bookingId, String status) throws DAOException {
        Booking booking = store.getBookings().stream()
                .filter(b -> b.getId() == bookingId)
                .findFirst()
                .orElseThrow(() -> new DAOException("Biglietto non trovato (ID: " + bookingId + ")"));

        try {
            booking.setStatus(BookingStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new DAOException("Stato prenotazione non valido: " + status);
        }
    }

    @Override
    public void cancel(int bookingId, int clientId) throws DAOException {
        Booking booking = store.getBookings().stream()
                .filter(b -> b.getId() == bookingId)
                .findFirst()
                .orElseThrow(() -> new DAOException("Biglietto non trovato (ID: " + bookingId + ")"));

        // CAMBIO QUI: Usiamo User al posto di Client
        User client = booking.getClient();

        if (client == null || client.getId() != clientId) {
            throw new DAOException("Non puoi annullare un biglietto che non ti appartiene.");
        }

        booking.cancel();

        // Rimette a disposizione il biglietto annullato nell'evento
        Event event = booking.getEvent();
        if (event != null) {
            event.setAvailableTickets(event.getAvailableTickets() + 1);
        }
    }
}