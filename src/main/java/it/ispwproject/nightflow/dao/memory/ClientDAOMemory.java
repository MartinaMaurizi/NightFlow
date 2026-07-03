package it.ispwproject.nightflow.dao.memory;

import it.ispwproject.nightflow.demo.DemoDataStore;
import it.ispwproject.nightflow.dao.ClientDAO;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.Client;

import java.util.List;

public class ClientDAOMemory implements ClientDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public Client findById(int id) throws DAOException {
        return store.getUsers().stream()
                .filter(u -> u instanceof Client && u.getId() == id)
                .map(u -> (Client) u)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Client> getByOrganizer(int organizerId) throws DAOException {
        // Trova tutti i clienti che hanno comprato un biglietto per le serate di questo organizzatore
        List<Integer> clientIds = store.getBookings().stream()
                .filter(b -> b.getEvent() != null && b.getEvent().getOrganizerId() == organizerId)
                .map(b -> b.getClient().getId())
                .distinct() // Rimuove i duplicati se un cliente ha comprato più biglietti
                .toList();

        // Recupera gli oggetti Client corrispondenti agli ID trovati
        return store.getUsers().stream()
                .filter(u -> u instanceof Client && clientIds.contains(u.getId()))
                .map(u -> (Client) u)
                .toList();
    }
}