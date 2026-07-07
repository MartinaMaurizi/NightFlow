package it.ispwproject.nightflow.dao.db;

import it.ispwproject.nightflow.dao.ConnectionFactory;
import it.ispwproject.nightflow.dao.ClientDAO;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAODB implements ClientDAO {

    private static final String FIND_BY_ID =
            "SELECT id, name, surname, email FROM user WHERE id = ? AND role = 'CLIENT'";

    private static final String GET_BY_ORGANIZER =
            "SELECT DISTINCT u.id, u.name, u.surname, u.email " +
                    "FROM user u " +
                    "JOIN booking b ON u.id = b.client_id " +
                    "JOIN event e ON b.event_id = e.id " +
                    "WHERE e.organizer_id = ? " +
                    "ORDER BY u.name";

    @Override
    public Client findById(int id) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapToClient(rs);
            }
        } catch (SQLException e) { throw new DAOException("Errore: " + e.getMessage(), e); }
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
        } catch (SQLException e) { throw new DAOException("Errore: " + e.getMessage(), e); }
        return result;
    }

    private Client mapToClient(ResultSet rs) throws SQLException {
        return new Client(rs.getInt("id"), rs.getString("name"), rs.getString("surname"), rs.getString("email"), null);
    }
}