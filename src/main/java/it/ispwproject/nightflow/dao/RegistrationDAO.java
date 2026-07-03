package it.ispwproject.nightflow.dao;

import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.User;
import java.util.List;

public interface RegistrationDAO {

    boolean emailExists(String email) throws DAOException;

    // Metodo aggiornato con la lista dei locali come secondo parametro
    void save(User user, List<String> localNames) throws DAOException;
}