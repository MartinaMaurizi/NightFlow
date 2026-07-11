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

    private static final String UPDATE_PASSWORD = "UPDATE user SET password = ? WHERE id = ?";

    // 🌟 1. HO AGGIUNTO 'dob', 'gender' E 'country' ALLE QUERY 🌟
    private static final String GET_ALL =
            "SELECT id, name, surname, dob, gender, country, city, email, role FROM user";
    private static final String FIND_BY_EMAIL =
            "SELECT id, name, surname, dob, gender, country, city, email, password, role FROM user WHERE email = ?";

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

        // 🌟 2. ESTRAGGO I NUOVI CAMPI DAL RESULTSET 🌟
        String country = rs.getString("country");
        String gender = rs.getString("gender");

        // La data necessita di una conversione da sql.Date a LocalDate
        java.time.LocalDate dob = null;
        java.sql.Date sqlDate = rs.getDate("dob");
        if (sqlDate != null) {
            dob = sqlDate.toLocalDate();
        }

        // Estraiamo la password dal DB.
        // Nota: GET_ALL non chiede la password, quindi dobbiamo controllare se la colonna esiste per evitare errori.
        String password = null;
        try {
            password = rs.getString("password");
        } catch (SQLException e) {
            // Se la colonna password non è presente nella query (come in GET_ALL), la ignoriamo.
        }

        // 🌟 3. POPOLIAMO GLI OGGETTI CON I DATI RECUPERATI 🌟
        if (role == Role.CLIENT) {
            Client c = new Client(id, name, surname, email, password);
            c.setCity(city);
            c.setCountry(country);
            c.setGender(gender);
            c.setDateOfBirth(dob); // Ecco la data!
            return c;
        } else {
            // Usiamo il nuovo costruttore snello da 5 parametri
            Organizer o = new Organizer(id, name, surname, email, password);

            // Impostiamo i dati anagrafici usando i setter, proprio come per il Client
            o.setCity(city);
            o.setCountry(country);
            o.setGender(gender);
            o.setDateOfBirth(dob);

            return o;
        }
    }

    @Override
    public void updatePassword(int id, String newPassword) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_PASSWORD)) {

            ps.setString(1, newPassword);
            ps.setInt(2, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Errore durante l'aggiornamento della password: " + e.getMessage(), e);
        }
    }
}