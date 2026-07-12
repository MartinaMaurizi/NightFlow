package it.ispwproject.nightflow.dao.db;

import it.ispwproject.nightflow.dao.AbstractBookingDAO;
import it.ispwproject.nightflow.dao.ConnectionFactory;
import it.ispwproject.nightflow.enumerator.BookingStatus;
import it.ispwproject.nightflow.enumerator.PaymentMethod;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAODB extends AbstractBookingDAO {

    // INSERIMENTO CON TUTTI I CAMPI: ticket_type, payment_method, ticket_code
    private static final String INSERT_BOOKING =
            "INSERT INTO booking (client_id, event_id, ticket_type, price_paid, payment_method, ticket_code, status, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, 'CONFIRMED', ?)";

    private static final String CANCEL_BOOKING =
            "UPDATE booking SET status = 'CANCELLED' WHERE id = ? AND client_id = ?";

    private static final String UPDATE_BOOKING =
            "UPDATE booking SET status = ?, payment_method = ? WHERE id = ?";

    private static final String RESTORE_TICKET =
            "UPDATE event SET available_tickets = available_tickets + 1 " +
                    "WHERE id = (SELECT event_id FROM booking WHERE id = ? AND client_id = ?)";

    private static final String REDUCE_TICKET_AVAILABILITY =
            "UPDATE event SET available_tickets = available_tickets - 1 WHERE id = ?";

    // SELEZIONE CON TUTTI I CAMPI: b.payment_method, b.ticket_code
    private static final String SELECT_BOOKINGS =
            "SELECT b.id, b.status, b.ticket_type, b.price_paid, b.payment_method, b.ticket_code, b.created_at, " +
                    "       u_c.id c_id, u_c.name c_name, u_c.surname c_surname, u_c.email c_email, " +
                    "       e.id e_id, e.name e_name, e.description e_desc, e.date_time e_date, " +
                    "       e.location e_loc, e.club_name e_localname, e.total_tickets e_cap, " +
                    "       e.available_tickets e_avail, e.base_price e_price, e.organizer_id e_orgid " +
                    "FROM booking b " +
                    "JOIN user u_c ON b.client_id = u_c.id " +
                    "JOIN event e ON b.event_id = e.id ";

    private static final String FIND_BY_CLIENT = SELECT_BOOKINGS + "WHERE b.client_id = ? ORDER BY b.created_at DESC";
    private static final String FIND_ALL = SELECT_BOOKINGS + "ORDER BY b.created_at DESC";
    private static final String FIND_UPCOMING_BY_CLIENT = SELECT_BOOKINGS + "WHERE b.client_id = ? AND b.status = 'CONFIRMED' AND e.date_time > NOW() ORDER BY e.date_time ASC";
    private static final String FIND_PAST_BY_CLIENT = SELECT_BOOKINGS + "WHERE b.client_id = ? AND b.status = 'CONFIRMED' AND e.date_time <= NOW() ORDER BY e.date_time DESC";
    private static final String FIND_COMPLETED_BY_CLIENT_AND_ORGANIZER = SELECT_BOOKINGS + "WHERE b.client_id = ? AND e.organizer_id = ? AND b.status = 'CONFIRMED' AND e.date_time <= NOW() ORDER BY e.date_time DESC";
    private static final String FIND_UPCOMING_BY_CLIENT_AND_ORGANIZER = SELECT_BOOKINGS + "WHERE b.client_id = ? AND e.organizer_id = ? AND b.status = 'CONFIRMED' AND e.date_time > NOW() ORDER BY e.date_time ASC";
    private static final String FIND_CANCELLED_BY_CLIENT = SELECT_BOOKINGS + "WHERE b.client_id = ? AND b.status = 'CANCELLED' ORDER BY b.created_at DESC";

    @Override
    public void save(Booking booking) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_BOOKING, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, booking.getClient().getId());
            ps.setInt(2, booking.getEvent().getId());

            ps.setString(3, booking.getTicketType() != null ? booking.getTicketType() : "Ingresso Base");
            ps.setDouble(4, booking.getEvent().getPrice());

            ps.setString(5, booking.getPaymentMethod() != null ? booking.getPaymentMethod().name() : null);
            ps.setString(6, booking.getTicketCode());

            ps.setTimestamp(7, Timestamp.valueOf(booking.getCreatedAt()));

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) booking.setId(keys.getInt(1));
            }

            booking.setStatus(BookingStatus.CONFIRMED);
            updateTicketAvailability(conn, booking.getEvent().getId());
            addToCache(booking);

        } catch (SQLException e) {
            throw new DAOException("Errore salvataggio prenotazione nel DB: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Booking booking) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_BOOKING)) {

            ps.setString(1, booking.getStatus().name());
            ps.setString(2, booking.getPaymentMethod() != null ? booking.getPaymentMethod().name() : null);
            ps.setInt(3, booking.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Errore durante l'aggiornamento della prenotazione: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Booking> findByClient(int clientId) throws DAOException {
        List<Booking> cached = findInCacheByClient(clientId);
        if (!cached.isEmpty()) return cached;
        List<Booking> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement ps = conn.prepareStatement(FIND_BY_CLIENT)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking b = mapToBooking(rs);
                    addToCache(b);
                    result.add(b);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore caricamento: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<Booking> findAll() throws DAOException {
        List<Booking> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement ps = conn.prepareStatement(FIND_ALL); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(mapToBooking(rs));
        } catch (SQLException e) {
            throw new DAOException("Errore caricamento: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<Booking> findUpcomingByClient(int clientId) throws DAOException {
        List<Booking> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement ps = conn.prepareStatement(FIND_UPCOMING_BY_CLIENT)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapToBooking(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore caricamento: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<Booking> findPastByClient(int clientId) throws DAOException {
        List<Booking> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement ps = conn.prepareStatement(FIND_PAST_BY_CLIENT)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapToBooking(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore caricamento: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<Booking> findCompletedByClientAndOrganizer(int clientId, int organizerId) throws DAOException {
        List<Booking> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement ps = conn.prepareStatement(FIND_COMPLETED_BY_CLIENT_AND_ORGANIZER)) {
            ps.setInt(1, clientId);
            ps.setInt(2, organizerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapToBooking(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore caricamento: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<Booking> findUpcomingByClientAndOrganizer(int clientId, int organizerId) throws DAOException {
        List<Booking> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement ps = conn.prepareStatement(FIND_UPCOMING_BY_CLIENT_AND_ORGANIZER)) {
            ps.setInt(1, clientId);
            ps.setInt(2, organizerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapToBooking(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore caricamento: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public void cancel(int bookingId, int clientId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psR = conn.prepareStatement(RESTORE_TICKET); PreparedStatement psC = conn.prepareStatement(CANCEL_BOOKING)) {
                psR.setInt(1, bookingId);
                psR.setInt(2, clientId);
                psR.executeUpdate();
                psC.setInt(1, bookingId);
                psC.setInt(2, clientId);
                if (psC.executeUpdate() == 0) throw new DAOException("Prenotazione non trovata.");
            }
            conn.commit();
            updateInCache(bookingId);
            identityMap.removeIf(b -> b.getClient() != null && b.getClient().getId() == clientId);
        } catch (SQLException e) {
            throw new DAOException("Errore annullamento: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateStatus(int bookingId, String status) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement ps = conn.prepareStatement("UPDATE booking SET status = ? WHERE id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, bookingId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Errore aggiornamento stato: " + e.getMessage(), e);
        }
    }

    private void updateTicketAvailability(Connection conn, int eventId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(REDUCE_TICKET_AVAILABILITY)) {
            ps.setInt(1, eventId);
            ps.executeUpdate();
        }
    }

    private Booking mapToBooking(ResultSet rs) throws SQLException {
        Client client = new Client(rs.getInt("c_id"), rs.getString("c_name"), rs.getString("c_surname"), rs.getString("c_email"), null);

        Event event = new Event(rs.getInt("e_id"), rs.getString("e_name"), rs.getString("e_desc"), rs.getTimestamp("e_date").toLocalDateTime(),
                rs.getString("e_loc"), rs.getString("e_localname"), rs.getInt("e_cap"), rs.getDouble("e_price"), rs.getInt("e_orgid"));
        event.setAvailableTickets(rs.getInt("e_avail"));

        Booking booking = new Booking(client, event);
        booking.setId(rs.getInt("id"));
        booking.setStatus(BookingStatus.valueOf(rs.getString("status")));
        booking.setTicketCode(rs.getString("ticket_code"));
        booking.setTicketType(rs.getString("ticket_type"));

        String paymentStr = rs.getString("payment_method");
        if (paymentStr != null && !paymentStr.isEmpty()) {
            booking.setPaymentMethod(PaymentMethod.valueOf(paymentStr));
        }

        if (rs.getTimestamp("created_at") != null) {
            booking.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }

        return booking;
    }

    @Override
    public List<Booking> getBookingsByEventId(int eventId) throws DAOException {
        List<Booking> bookings = new ArrayList<>();

        // 🌟 DEVI USARE LA QUERY CON LE JOIN!
        // Concateniamo la base SELECT_BOOKINGS con la condizione specifica
        String sql = SELECT_BOOKINGS + " WHERE b.event_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // mapToBooking troverà tutte le colonne perché la query è identica alle altre
                    bookings.add(mapToBooking(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore recupero prenotazioni evento: " + e.getMessage(), e);
        }
        return bookings;
    }
    @Override
    public List<Booking> findCancelledByClient(int clientId) throws DAOException {
        List<Booking> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_CANCELLED_BY_CLIENT)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapToBooking(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore caricamento: " + e.getMessage(), e);
        }
        return result;
    }
}