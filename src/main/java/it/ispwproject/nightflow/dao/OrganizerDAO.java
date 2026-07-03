package it.ispwproject.nightflow.dao;

import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.Organizer;

import java.util.List;

public interface OrganizerDAO {
    List<Organizer> findAll() throws DAOException;
    Organizer findById(int id) throws DAOException;

    // Aggiungi questi:
    void save(Organizer organizer) throws DAOException;
    void update(Organizer organizer) throws DAOException;
    void delete(int id) throws DAOException;
}