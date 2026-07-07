package it.ispwproject.nightflow.dao.memory;

import it.ispwproject.nightflow.dao.RegistrationDAO;
import it.ispwproject.nightflow.demo.DemoDataStore;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.User;

import java.util.List;

public class RegistrationDAOMemory implements RegistrationDAO {

    @Override
    public boolean emailExists(String email) throws DAOException {

        return DemoDataStore.getInstance().getUsers().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public void save(User user, List<String> localNames) throws DAOException {
        DemoDataStore store = DemoDataStore.getInstance();

        if (emailExists(user.getEmail())) {
            throw new DAOException("L'email " + user.getEmail() + " è già presente in memoria.");
        }

        // Assegniamo l'ID
        user.setId(store.nextUserId());

        // 🌟 AGGIUNGIAMO QUESTO CONTROLLO PER FORZARE L'IDENTITÀ
        // Se è un Client, dobbiamo assicurarci che nella lista finisca come Client
        // (Il tuo codice attuale aggiunge l'oggetto così com'è, quindi se gli passi
        // un 'Client' mascherato da 'User', finirà dentro come 'Client'!)

        store.getUsers().add(user);

        System.out.println("DEBUG: Utente salvato nel DAO come: " + user.getClass().getName());
    }
}