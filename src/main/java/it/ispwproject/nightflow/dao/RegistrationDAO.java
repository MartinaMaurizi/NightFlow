package it.ispwproject.nightflow.dao;

import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.User;
import java.util.List;

public interface RegistrationDAO {

    boolean emailExists(String email) throws DAOException;

    void save(User user, List<String> localNames) throws DAOException;
}