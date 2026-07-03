package it.ispwproject.nightflow.dao.memory;

import it.ispwproject.nightflow.dao.RegistrationDAO;
import it.ispwproject.nightflow.demo.DemoDataStore;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.User;

import java.util.List;

public class RegistrationDAOMemory implements RegistrationDAO {

    @Override
    public boolean emailExists(String email) throws DAOException {
        // Ora controlliamo direttamente nel "Cervello Centrale" (DemoDataStore)
        return DemoDataStore.getInstance().getUsers().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public void save(User user, List<String> localNames) throws DAOException {
        DemoDataStore store = DemoDataStore.getInstance();

        if (emailExists(user.getEmail())) {
            throw new DAOException("L'email " + user.getEmail() + " è già presente in memoria.");
        }

        // Usiamo il generatore di ID del DemoDataStore così è tutto sincronizzato
        user.setId(store.nextUserId());

        // Aggiungiamo l'utente alla lista centrale! Ora il Login lo troverà.
        store.getUsers().add(user);

        // N.B: I nomi dei locali per l'organizzatore andrebbero settati direttamente
        // sull'oggetto user (es. setLocalNames se lo hai creato nel modello)
    }
}