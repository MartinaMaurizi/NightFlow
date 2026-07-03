package it.ispwproject.nightflow.dao.db;

import it.ispwproject.nightflow.dao.ConnectionFactory;
import it.ispwproject.nightflow.dao.UserDAO;
import it.ispwproject.nightflow.enumerator.Role;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.Client;
import it.ispwproject.nightflow.model.Organizer;
import it.ispwproject.nightflow.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAODB implements UserDAO {

    private static final String FIND_BY_EMAIL =
            "SELECT u.id, u.name, u.surname, u.email, u.role, u.city, od.local_name " +
                    "FROM users u LEFT JOIN organizer_details od ON u.id = od.user_id WHERE u.email = ?";

    private static final String UPDATE_EMAIL = "UPDATE users SET email = ? WHERE id = ?";
    private static final String UPDATE_CITY = "UPDATE users SET city = ? WHERE id = ?";

    private static final String GET_ALL =
            "SELECT u.id, u.name, u.surname, u.email, u.role, u.city, od.local_name " +
                    "FROM users u LEFT JOIN organizer_details od ON u.id = od.user_id";

    @Override
    public void updateEmail(int id, String newEmail) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_EMAIL)) {
            ps.setString(1, newEmail); ps.setInt(2, id);
            if (ps.executeUpdate() == 0) throw new DAOException("Utente non trovato: " + id);
        } catch (SQLException e) { throw new DAOException("Errore DB: " + e.getMessage(), e); }
    }

    @Override
    public void updateCity(int id, String newCity) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_CITY)) {
            ps.setString(1, newCity); ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new DAOException("Errore DB: " + e.getMessage(), e); }
    }

    @Override
    public User findByEmail(String email) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_EMAIL)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new DAOException("Utente non trovato: " + email);
                return mapUser(rs);
            }
        } catch (SQLException e) { throw new DAOException("Errore DB: " + e.getMessage(), e); }
    }

    @Override
    public List<User> getAll() throws DAOException {
        List<User> list = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapUser(rs));
        } catch (SQLException e) { throw new DAOException("Errore caricamento: " + e.getMessage(), e); }
        return list;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String surname = rs.getString("surname");
        String email = rs.getString("email");
        String city = rs.getString("city");
        Role role = Role.fromString(rs.getString("role"));
        String localName = rs.getString("local_name");

        if (role == Role.CLIENT) {
            Client c = new Client(id, name, surname, email, null);
            c.setCity(city);
            return c;
        } else {
            Organizer o = new Organizer(
                    id,
                    name,
                    surname,
                    email,
                    null,       // password
                    null,       // dateOfBirth (dovresti recuperarla dal DB se ti serve)
                    null,       // gender
                    null,       // country
                    city,       // city (recuperata dalla query)
                    localName != null ? new ArrayList<>(List.of(localName)) : new ArrayList<>()
            );
            o.setCity(city);
            return o;
        }
    }
}