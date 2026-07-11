package it.ispwproject.nightflow.dao.db;

import it.ispwproject.nightflow.dao.ConnectionFactory;
import it.ispwproject.nightflow.dao.RegistrationDAO;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.User;
import it.ispwproject.nightflow.util.logger.AppLogger;

import java.sql.*;
import java.util.List;

public class RegistrationDAODB implements RegistrationDAO {

    // Tabella 'user' e 9 parametri (corrispondono esattamente al DB)
    private static final String INSERT_USER =
            "INSERT INTO user (name, surname, dob, gender, country, city, email, password, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String CHECK_EMAIL =
            "SELECT COUNT(*) FROM user WHERE email = ?";

    @Override
    public boolean emailExists(String email) throws DAOException {
        try { ConnectionFactory.clearRole(); }
        catch (SQLException e) { AppLogger.logWarning("clearRole fallito: " + e.getMessage()); }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(CHECK_EMAIL)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DAOException("Errore verifica email: " + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public void save(User user, List<String> localNames) throws DAOException {
        try { ConnectionFactory.clearRole(); }
        catch (SQLException e) { AppLogger.logWarning("clearRole fallito: " + e.getMessage()); }

        try (Connection conn = ConnectionFactory.getConnection()) {
            executeSaveTransaction(conn, user);
        } catch (SQLException e) {
            throw new DAOException("Errore di connessione durante la registrazione: " + e.getMessage(), e);
        }
    }

    private void executeSaveTransaction(Connection conn, User user) throws SQLException, DAOException {
        conn.setAutoCommit(false);
        try {
            // Inserisce l'utente
            int userId = insertUser(conn, user);
            user.setId(userId);

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw new DAOException("Errore transazione: " + e.getMessage(), e);
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private int insertUser(Connection conn, User user) throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getSurname());

            // Gestione sicura data
            if (user.getDateOfBirth() != null) {
                ps.setDate(3, java.sql.Date.valueOf(user.getDateOfBirth()));
            } else {
                ps.setNull(3, Types.DATE); // Se è null, mandiamo NULL al DB invece di lanciare eccezione
            }

            ps.setString(4, user.getGender());
            ps.setString(5, user.getCountry());
            ps.setString(6, user.getCity());
            ps.setString(7, user.getEmail());
            ps.setString(8, user.getPassword());

            if (user.getRole() != null) {
                ps.setString(9, user.getRole().name());
            } else {
                ps.setString(9, "CLIENT"); // Default se nullo
            }

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Nessuna riga inserita.");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("ID utente non generato.");
    }
}