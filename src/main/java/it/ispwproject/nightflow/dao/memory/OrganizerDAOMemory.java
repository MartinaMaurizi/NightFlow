package it.ispwproject.nightflow.dao.memory;

import it.ispwproject.nightflow.dao.OrganizerDAO;
import it.ispwproject.nightflow.demo.DemoDataStore;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.Organizer;
import it.ispwproject.nightflow.model.User;

import java.util.List;

public class OrganizerDAOMemory implements OrganizerDAO {

    private final DemoDataStore dataStore = DemoDataStore.getInstance();

    @Override
    public Organizer findById(int id) {
        // 🌟 Usiamo il method reference come richiesto da SonarCloud
        return dataStore.getUsers().stream()
                .filter(Organizer.class::isInstance)
                .map(Organizer.class::cast)
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Organizer> findAll() {
        // 🌟 Usiamo .toList() (Java 16+) invece di Collectors.toList()
        return dataStore.getUsers().stream()
                .filter(Organizer.class::isInstance)
                .map(Organizer.class::cast)
                .toList();
    }

    @Override
    public void save(Organizer organizer) {
        if (organizer.getId() == 0) {
            organizer.setId(dataStore.nextUserId());
        }
        dataStore.getUsers().add(organizer);
    }

    @Override
    public void update(Organizer organizer) throws DAOException {
        List<User> users = dataStore.getUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == organizer.getId() && users.get(i) instanceof Organizer) {
                users.set(i, organizer);
                return;
            }
        }
        throw new DAOException("Organizzatore con ID " + organizer.getId() + " non trovato.");
    }

    @Override
    public void delete(int id) throws DAOException {
        boolean removed = dataStore.getUsers().removeIf(u -> u instanceof Organizer && u.getId() == id);
        if (!removed) {
            throw new DAOException("Organizzatore non trovato.");
        }
    }
}