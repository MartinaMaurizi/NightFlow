package it.ispwproject.nightflow.dao.memory;

import it.ispwproject.nightflow.dao.UserDAO;
import it.ispwproject.nightflow.demo.DemoDataStore;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.User;

import java.util.List;

public class UserDAOMemory implements UserDAO {

    private final DemoDataStore dataStore = DemoDataStore.getInstance();

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

    // 🌟 AGGIUNTO IL METODO MANCANTE PER IL CAMBIO PASSWORD
    @Override
    public void updatePassword(int id, String newPassword) throws DAOException {
        // Cerchiamo l'utente nella lista fittizia tramite il suo ID
        User userToUpdate = dataStore.getUsers().stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElseThrow(() -> new DAOException("Utente non trovato nel datastore in memoria."));

        // Aggiorniamo la password dell'oggetto trovato
        userToUpdate.setPassword(newPassword);
    }
}