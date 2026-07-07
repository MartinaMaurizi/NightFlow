package it.ispwproject.nightflow.dao.db;

import it.ispwproject.nightflow.dao.ConnectionFactory;
import it.ispwproject.nightflow.dao.OrganizerDAO;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.Organizer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrganizerDAODB implements OrganizerDAO {

    // 🌟 ALLINEATO AL TUO DB: Tabella 'user', tolto organizer_details
    private static final String FIND_ALL =
            "SELECT id, name, surname, email FROM user WHERE role = 'ORGANIZER'";

    private static final String FIND_BY_ID =
            "SELECT id, name, surname, email FROM user WHERE id = ? AND role = 'ORGANIZER'";

    @Override
    public List<Organizer> findAll() throws DAOException {
        List<Organizer> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(mapToOrganizer(rs));
        } catch (SQLException e) { throw new DAOException("Errore caricamento: " + e.getMessage(), e); }
        return result;
    }

    @Override
    public Organizer findById(int id) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapToOrganizer(rs);
            }
        } catch (SQLException e) { throw new DAOException("Errore caricamento: " + e.getMessage(), e); }
        return null;
    }

    @Override public void save(Organizer organizer) throws DAOException { throw new DAOException("Non supportato"); }
    @Override public void update(Organizer organizer) throws DAOException { throw new DAOException("Non supportato"); }
    @Override public void delete(int id) throws DAOException { throw new DAOException("Non supportato"); }

    private Organizer mapToOrganizer(ResultSet rs) throws SQLException {
        return new Organizer(
                rs.getInt("id"), rs.getString("name"), rs.getString("surname"),
                rs.getString("email"), null, null, null, null, null, new ArrayList<>()
        );
    }
}