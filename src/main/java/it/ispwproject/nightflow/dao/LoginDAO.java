package it.ispwproject.nightflow.dao;

import it.ispwproject.nightflow.exception.LoginException;
import it.ispwproject.nightflow.model.Credentials;

public interface LoginDAO {
    Credentials execute(String email, String password) throws LoginException;
}
