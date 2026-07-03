package it.ispwproject.nightflow.dao;

import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.User;

import java.util.List;

public interface UserDAO {

    // Aggiorna l'indirizzo email di un utente nel sistema
    void updateEmail(int id, String newEmail) throws DAOException;

    // Cerca un utente tramite la sua email (fondamentale in fase di Login o recupero password)
    User findByEmail(String email) throws DAOException;

    // Restituisce la lista di tutti gli utenti registrati (utile per eventuali pannelli di amministrazione)
    List<User> getAll() throws DAOException;

    void updateCity(int id, String newCity) throws DAOException;

}