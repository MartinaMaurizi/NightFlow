package it.ispwproject.nightflow.dao.db;

import it.ispwproject.nightflow.dao.ConnectionFactory;
import it.ispwproject.nightflow.dao.EventDAO;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAODB implements EventDAO {

    private static final String FIND_BY_ID =
            "SELECT * FROM events WHERE id = ?";

    private static final String GET_ALL_UPCOMING =
            "SELECT * FROM events WHERE date_time > NOW() ORDER BY date_time ASC";

    private static final String FIND_BY_ORGANIZER =
            "SELECT * FROM events WHERE organizer_id = ? ORDER BY date_time DESC";

    private static final String INSERT_EVENT =
            "INSERT INTO events (organizer_id, name, description, date_time, location, total_capacity, available_tickets, price) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_EVENT =
            "UPDATE events SET name = ?, description = ?, date_time = ?, location = ?, total_capacity = ?, available_tickets = ?, price = ? " +
                    "WHERE id = ?";

    private static final String DELETE_EVENT =
            "DELETE FROM events WHERE id = ?";

    @Override
    public Event findById(int id) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapToEvent(rs);
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento dell'evento: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Event> getAllUpcomingEvents() throws DAOException {
        List<Event> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_ALL_UPCOMING);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapToEvent(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento degli eventi futuri: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<Event> findByOrganizer(int organizerId) throws DAOException {
        List<Event> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ORGANIZER)) {

            ps.setInt(1, organizerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapToEvent(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento degli eventi dell'organizzatore: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public void save(Event event) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_EVENT, Statement.RETURN_GENERATED_KEYS)) {

            // Assumiamo che l'evento abbia un riferimento all'organizzatore che l'ha creato
            ps.setInt(1, event.getOrganizerId());
            ps.setString(2, event.getName());
            ps.setString(3, event.getDescription());
            ps.setTimestamp(4, Timestamp.valueOf(event.getDateTime()));
            ps.setString(5, event.getLocation());
            ps.setInt(6, event.getTotalCapacity());
            ps.setInt(7, event.getAvailableTickets());
            ps.setDouble(8, event.getPrice());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) event.setId(keys.getInt(1));
            }

        } catch (SQLException e) {
            throw new DAOException("Errore durante la creazione dell'evento: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Event event) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_EVENT)) {

            ps.setString(1, event.getName());
            ps.setString(2, event.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(event.getDateTime()));
            ps.setString(4, event.getLocation());
            ps.setInt(5, event.getTotalCapacity());
            ps.setInt(6, event.getAvailableTickets());
            ps.setDouble(7, event.getPrice());
            ps.setInt(8, event.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Errore durante l'aggiornamento dell'evento: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int eventId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_EVENT)) {

            ps.setInt(1, eventId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Errore durante l'eliminazione dell'evento: " + e.getMessage(), e);
        }
    }

    // Metodo di utilità per non ripetere la lettura dei campi in ogni query
    private Event mapToEvent(ResultSet rs) throws SQLException {
        // Usa il costruttore della tua classe Event.java (che ha 9 parametri)
        Event event = new Event(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getTimestamp("date_time").toLocalDateTime(),
                rs.getString("location"),
                rs.getString("local_name"), // Assicurati che nel DB si chiami così
                rs.getInt("total_capacity"),
                rs.getDouble("price"),
                rs.getInt("organizer_id")   // Questo è l'organizerId che hai nella classe Event
        );

        // Imposta manualmente i biglietti disponibili perché non passano dal costruttore
        event.setAvailableTickets(rs.getInt("available_tickets"));

        return event;
    }

    @Override
    public List<Event> findByLocalName(String localName) throws DAOException {
        // Implementazione necessaria perché presente nell'interfaccia
        List<Event> result = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE local_name = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, localName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapToEvent(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore ricerca per locale: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<Event> findByOrganizerId(int organizerId) throws DAOException {
        // Usa lo stesso metodo che hai già scritto per findByOrganizer
        return findByOrganizer(organizerId);
    }

    @Override
    public void delete(int eventId, int organizerId) throws DAOException {
        // Implementazione sicura richiesta dall'interfaccia
        String sql = "DELETE FROM events WHERE id = ? AND organizer_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, organizerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Errore durante l'eliminazione sicura: " + e.getMessage(), e);
        }
    }

}