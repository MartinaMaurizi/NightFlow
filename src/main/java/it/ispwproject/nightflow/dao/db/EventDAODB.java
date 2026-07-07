package it.ispwproject.nightflow.dao.db;

import it.ispwproject.nightflow.dao.ConnectionFactory;
import it.ispwproject.nightflow.dao.EventDAO;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAODB implements EventDAO {

    // 🌟 1. ALLINEATO AL TUO DB: Tabella 'event', e colonne 'club_name', 'total_tickets', 'base_price'
    private static final String FIND_BY_ID =
            "SELECT id, organizer_id, name, description, date_time, location, club_name, total_tickets, available_tickets, base_price FROM event WHERE id = ?";

    private static final String GET_ALL_UPCOMING =
            "SELECT id, organizer_id, name, description, date_time, location, club_name, total_tickets, available_tickets, base_price FROM event WHERE date_time > NOW() ORDER BY date_time ASC";

    private static final String FIND_BY_ORGANIZER =
            "SELECT id, organizer_id, name, description, date_time, location, club_name, total_tickets, available_tickets, base_price FROM event WHERE organizer_id = ? ORDER BY date_time DESC";

    private static final String INSERT_EVENT =
            "INSERT INTO event (organizer_id, name, description, date_time, location, club_name, total_tickets, available_tickets, base_price) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_EVENT =
            "UPDATE event SET name = ?, description = ?, date_time = ?, location = ?, club_name = ?, total_tickets = ?, available_tickets = ?, base_price = ? " +
                    "WHERE id = ?";

    private static final String DELETE_EVENT =
            "DELETE FROM event WHERE id = ?";

    @Override
    public Event findById(int id) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapToEvent(rs);
            }
        } catch (SQLException e) { throw new DAOException("Errore nel caricamento: " + e.getMessage(), e); }
        return null;
    }

    @Override
    public List<Event> getAllUpcomingEvents() throws DAOException {
        List<Event> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_ALL_UPCOMING);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(mapToEvent(rs));
        } catch (SQLException e) { throw new DAOException("Errore nel caricamento: " + e.getMessage(), e); }
        return result;
    }

    @Override
    public List<Event> findByOrganizer(int organizerId) throws DAOException {
        List<Event> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ORGANIZER)) {
            ps.setInt(1, organizerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapToEvent(rs));
            }
        } catch (SQLException e) { throw new DAOException("Errore nel caricamento degli eventi dell'organizzatore: " + e.getMessage(), e); }
        return result;
    }

    @Override
    public void save(Event event) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_EVENT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, event.getOrganizerId());
            ps.setString(2, event.getName());
            ps.setString(3, event.getDescription());
            ps.setTimestamp(4, Timestamp.valueOf(event.getDateTime()));
            ps.setString(5, event.getLocation());
            ps.setString(6, event.getLocalName());
            ps.setInt(7, event.getTotalCapacity());
            ps.setInt(8, event.getAvailableTickets());
            ps.setDouble(9, event.getPrice());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) event.setId(keys.getInt(1));
            }
        } catch (SQLException e) { throw new DAOException("Errore durante la creazione: " + e.getMessage(), e); }
    }

    @Override
    public void update(Event event) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_EVENT)) {

            ps.setString(1, event.getName());
            ps.setString(2, event.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(event.getDateTime()));
            ps.setString(4, event.getLocation());
            ps.setString(5, event.getLocalName());
            ps.setInt(6, event.getTotalCapacity());
            ps.setInt(7, event.getAvailableTickets());
            ps.setDouble(8, event.getPrice());
            ps.setInt(9, event.getId());

            ps.executeUpdate();
        } catch (SQLException e) { throw new DAOException("Errore durante l'aggiornamento: " + e.getMessage(), e); }
    }

    @Override
    public void delete(int eventId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_EVENT)) {
            ps.setInt(1, eventId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new DAOException("Errore durante l'eliminazione: " + e.getMessage(), e); }
    }

    private Event mapToEvent(ResultSet rs) throws SQLException {
        Event event = new Event(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getTimestamp("date_time").toLocalDateTime(),
                rs.getString("location"),
                rs.getString("club_name"),
                rs.getInt("total_tickets"),
                rs.getDouble("base_price"),
                rs.getInt("organizer_id")
        );
        event.setAvailableTickets(rs.getInt("available_tickets"));
        return event;
    }

    @Override
    public List<Event> findByLocalName(String localName) throws DAOException {
        List<Event> result = new ArrayList<>();
        String sql = "SELECT id, organizer_id, name, description, date_time, location, club_name, total_tickets, available_tickets, base_price FROM event WHERE club_name = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, localName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapToEvent(rs));
            }
        } catch (SQLException e) { throw new DAOException("Errore ricerca per locale: " + e.getMessage(), e); }
        return result;
    }

    @Override
    public List<Event> findByOrganizerId(int organizerId) throws DAOException {
        return findByOrganizer(organizerId);
    }

    @Override
    public void delete(int eventId, int organizerId) throws DAOException {
        String sql = "DELETE FROM event WHERE id = ? AND organizer_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, organizerId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new DAOException("Errore eliminazione sicura: " + e.getMessage(), e); }
    }
}