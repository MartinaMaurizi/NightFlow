package it.ispwproject.nightflow.dao.memory;

import it.ispwproject.nightflow.dao.UserDAO;
import it.ispwproject.nightflow.demo.DemoDataStore;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.User;

import java.util.List;

public class UserDAOMemory implements UserDAO {

    private final DemoDataStore dataStore = DemoDataStore.getInstance();

    @Override
    public void updateEmail(int id, String newEmail) throws DAOException {
        User user = dataStore.getUsers().stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElseThrow(() -> new DAOException("Utente con ID " + id + " non trovato."));

        user.setEmail(newEmail);
    }

    @Override
    public void updateCity(int id, String newCity) throws DAOException {
        User user = dataStore.getUsers().stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElseThrow(() -> new DAOException("Utente con ID " + id + " non trovato."));

        user.setCity(newCity);
    }

    @Override
    public User findByEmail(String email) throws DAOException {
        return dataStore.getUsers().stream()
                .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> getAll() throws DAOException {
        return dataStore.getUsers();
    }
}