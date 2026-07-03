package it.ispwproject.nightflow.dao.db;

import it.ispwproject.nightflow.dao.ConnectionFactory;
import it.ispwproject.nightflow.dao.ClientDAO;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAODB implements ClientDAO {

    // Assumiamo che la tabella degli utenti si chiami "users" e il ruolo sia "CLIENT"
    private static final String FIND_BY_ID =
            "SELECT id, name, surname, email FROM users WHERE id = ? AND role = 'CLIENT'";

    // Recupera tutti i clienti che hanno prenotato un evento gestito da uno specifico organizzatore
    private static final String GET_BY_ORGANIZER =
            "SELECT DISTINCT u.id, u.name, u.surname, u.email " +
                    "FROM users u " +
                    "JOIN bookings b ON u.id = b.client_id " +
                    "JOIN events e ON b.event_id = e.id " +
                    "WHERE e.organizer_id = ? " +
                    "ORDER BY u.name";

    // Tabella ponte per salvare gli eventi preferiti del cliente
    private static final String ADD_FAVOURITE_EVENT =
            "INSERT IGNORE INTO client_favourite_event (client_id, event_id) VALUES (?, ?)";

    private static final String REMOVE_FAVOURITE_EVENT =
            "DELETE FROM client_favourite_event WHERE client_id = ? AND event_id = ?";

    private static final String IS_FAVOURITE_EVENT =
            "SELECT COUNT(*) FROM client_favourite_event WHERE client_id = ? AND event_id = ?";

    @Override
    public Client findById(int id) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapToClient(rs);
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento del cliente: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Client> getByOrganizer(int organizerId) throws DAOException {
        List<Client> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_BY_ORGANIZER)) {

            ps.setInt(1, organizerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapToClient(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento dei clienti: " + e.getMessage(), e);
        }
        return result;
    }

    private Client mapToClient(ResultSet rs) throws SQLException {
        // La password non serve per le operazioni ordinarie, viene messa a null
        return new Client(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("surname"),
                rs.getString("email"),
                null
        );
    }
}