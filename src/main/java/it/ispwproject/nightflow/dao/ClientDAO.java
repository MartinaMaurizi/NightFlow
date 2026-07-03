package it.ispwproject.nightflow.dao;

import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.Client;

import java.util.List;

public interface ClientDAO {

    // Trova un cliente specifico tramite il suo ID
    Client findById(int id) throws DAOException;

    // Ottiene la lista di tutti i clienti che hanno prenotato almeno un evento gestito da un certo organizzatore
    List<Client> getByOrganizer(int organizerId) throws DAOException;
}