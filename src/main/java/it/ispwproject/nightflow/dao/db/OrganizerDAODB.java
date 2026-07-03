package it.ispwproject.nightflow.dao.db;

import it.ispwproject.nightflow.dao.ConnectionFactory;
import it.ispwproject.nightflow.dao.OrganizerDAO;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.Organizer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrganizerDAODB implements OrganizerDAO {

    private static final String FIND_ALL =
            "SELECT u.id, u.name, u.surname, u.email, od.company_name " +
                    "FROM users u " +
                    "JOIN organizer_details od ON u.id = od.user_id " +
                    "WHERE u.role = 'ORGANIZER'";

    private static final String FIND_BY_ID =
            "SELECT u.id, u.name, u.surname, u.email, od.company_name " +
                    "FROM users u " +
                    "JOIN organizer_details od ON u.id = od.user_id " +
                    "WHERE u.id = ? AND u.role = 'ORGANIZER'";

    @Override
    public List<Organizer> findAll() throws DAOException {
        List<Organizer> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapToOrganizer(rs));
            }

        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento degli organizzatori: " + e.getMessage(), e);
        }
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

        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento dell'organizzatore: " + e.getMessage(), e);
        }
        return null;
    }

    // --- METODI AGGIUNTI PER RISPETTARE L'INTERFACCIA ---

    @Override
    public void save(Organizer organizer) throws DAOException {
        throw new DAOException("Operazione non supportata in sola lettura");
    }

    @Override
    public void update(Organizer organizer) throws DAOException {
        throw new DAOException("Operazione non supportata in sola lettura");
    }

    @Override
    public void delete(int id) throws DAOException {
        throw new DAOException("Operazione non supportata in sola lettura");
    }

    // ----------------------------------------------------

    private Organizer mapToOrganizer(ResultSet rs) throws SQLException {
        return new Organizer(
                rs.getInt("id"),              // 1. int id
                rs.getString("name"),         // 2. String name
                rs.getString("surname"),      // 3. String surname
                rs.getString("email"),        // 4. String email
                null,                         // 5. String password (null)
                null,                         // 6. LocalDate dateOfBirth (null)
                null,                         // 7. String gender (null)
                null,                         // 8. String country (null)
                null,                         // 9. String city (null)
                List.of(rs.getString("company_name")) // 10. List<String> localNames
        );
    }
}