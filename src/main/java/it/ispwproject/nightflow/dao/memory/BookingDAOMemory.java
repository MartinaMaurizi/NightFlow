package it.ispwproject.nightflow.dao.memory;

import it.ispwproject.nightflow.demo.DemoDataStore;
import it.ispwproject.nightflow.dao.BookingDAO;
import it.ispwproject.nightflow.enumerator.BookingStatus;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.Booking;
import it.ispwproject.nightflow.model.Event;
import it.ispwproject.nightflow.model.User;

import java.time.LocalDateTime;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingDAOMemory implements BookingDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public void save(Booking booking) {
        booking.setId(store.nextBookingId());
        booking.setStatus(BookingStatus.CONFIRMED);

        if (booking.getTicketCode() == null) {
            booking.setTicketCode("TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        store.getBookings().add(booking);

        Event event = booking.getEvent();
        if (event != null) {
            event.setAvailableTickets(Math.max(0, event.getAvailableTickets() - 1));
        }
    }

    // 🌟 ECCO IL NUOVO METODO UPDATE PER IL DATABASE IN MEMORIA
    @Override
    public void update(Booking booking) throws DAOException {
        boolean found = false;
        List<Booking> bookings = store.getBookings();

        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).getId() == booking.getId()) {
                // Aggiorniamo la prenotazione esistente con quella nuova
                bookings.set(i, booking);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new DAOException("Prenotazione non trovata in memoria (ID: " + booking.getId() + ")");
        }
    }

    @Override
    public List<Booking> findByClient(int clientId) {
        return store.getBookings().stream()
                .filter(b -> b.getClient() != null && b.getClient().getId() == clientId)
                .toList();
    }

    public List<Booking> findByEvent(int eventId) {
        return store.getBookings().stream()
                .filter(b -> b.getEvent() != null && b.getEvent().getId() == eventId
                        && b.getStatus() == BookingStatus.CONFIRMED)
                .toList();
    }

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(store.getBookings());
    }

    @Override
    public List<Booking> findCompletedByClientAndOrganizer(int clientId, int organizerId) {
        LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());
        return store.getBookings().stream()
                .filter(b -> b.getClient() != null && b.getClient().getId() == clientId
                        && b.getEvent() != null && b.getEvent().getOrganizerId() == organizerId
                        && b.getStatus() == BookingStatus.CONFIRMED
                        && b.getEvent().getDateTime().isBefore(now))
                .toList();
    }

    @Override
    public List<Booking> findUpcomingByClientAndOrganizer(int clientId, int organizerId) {
        LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());
        return store.getBookings().stream()
                .filter(b -> b.getClient() != null && b.getClient().getId() == clientId
                        && b.getEvent() != null && b.getEvent().getOrganizerId() == organizerId
                        && b.getStatus() == BookingStatus.CONFIRMED
                        && b.getEvent().getDateTime().isAfter(now))
                .toList();
    }

    @Override
    public List<Booking> findPastByClient(int clientId) {
        LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());
        return store.getBookings().stream()
                .filter(b -> b.getClient() != null && b.getClient().getId() == clientId
                        && b.getStatus() == BookingStatus.CONFIRMED
                        && b.getEvent() != null
                        && b.getEvent().getDateTime().isBefore(now))
                .toList();
    }

    @Override
    public List<Booking> findUpcomingByClient(int clientId) {
        LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());
        return store.getBookings().stream()
                .filter(b -> b.getClient() != null && b.getClient().getId() == clientId
                        && b.getStatus() == BookingStatus.CONFIRMED
                        && b.getEvent() != null
                        && b.getEvent().getDateTime().isAfter(now))
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

        User client = booking.getClient();

        if (client == null || client.getId() != clientId) {
            throw new DAOException("Non puoi annullare un biglietto che non ti appartiene.");
        }

        booking.cancel();

        Event event = booking.getEvent();
        if (event != null) {
            event.setAvailableTickets(event.getAvailableTickets() + 1);
        }
    }
}