package it.ispwproject.nightflow.dao;

import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.Event;

import java.util.List;

public interface EventDAO {

    // Recupera un evento specifico tramite il suo ID
    Event findById(int id) throws DAOException;

    // Recupera tutti gli eventi futuri disponibili nel sistema (utile per la home del Cliente)
    List<Event> getAllUpcomingEvents() throws DAOException;

    // Salva un nuovo evento nel database (quando un Organizzatore crea una nuova serata)
    void save(Event event) throws DAOException;

    // Aggiorna un evento esistente (es. cambia l'orario, il prezzo o i biglietti disponibili)
    void update(Event event) throws DAOException;

    // Recupera gli eventi dato il nome del locale (utile per controllare sovrapposizioni)
    List<Event> findByLocalName(String localName) throws DAOException;

    List<Event> findByOrganizerId(int organizerId) throws DAOException;
    void delete(int eventId, int organizerId) throws DAOException;
}