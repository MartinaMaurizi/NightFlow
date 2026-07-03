package it.ispwproject.nightflow.controller.applicativo;

import it.ispwproject.nightflow.bean.SessionBean;
import it.ispwproject.nightflow.dao.ConnectionFactory;
import it.ispwproject.nightflow.dao.DAOFactory;
import it.ispwproject.nightflow.dao.UserDAO;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.exception.LoginException;
import it.ispwproject.nightflow.model.Credentials;
import it.ispwproject.nightflow.model.User;
import it.ispwproject.nightflow.util.PasswordUtils;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;

import java.sql.SQLException;

public class LoginController {

    // 1. Ho modificato i risultati in base agli attori del tuo diagramma
    public enum LoginResult {
        SUCCESSO_CLIENT,
        SUCCESSO_ORGANIZER
    }

    public LoginResult login(String email, String password) throws LoginException {

        String hashedPassword = PasswordUtils.hash(password);
        Credentials credentials = DAOFactory.getLoginDAO().execute(email, hashedPassword);

        if (!DAOFactory.MEMORY.equalsIgnoreCase(DAOFactory.getPersistence())) {
            try {
                ConnectionFactory.changeRole(credentials.getRole());
            } catch (SQLException e) {
                throw new LoginException("Errore durante il cambio ruolo: " + e.getMessage(), e);
            }
        }

        User user;
        try {
            UserDAO userDAO = DAOFactory.getUserDAO();
            user = userDAO.findByEmail(email);
        } catch (DAOException e) {
            throw new LoginException("Errore nel caricamento utente: " + e.getMessage(), e);
        }

        SessionManager.getInstance().setLoggedUser(user);
        SessionManager.getInstance().setSessionBean(
                new SessionBean(user.getEmail(), credentials.getRole())
        );

        // 2. Lo switch ora smista l'utente verso la dashboard corretta del tuo progetto
        return switch (credentials.getRole()) {
            case CLIENT       -> LoginResult.SUCCESSO_CLIENT;
            case ORGANIZER -> LoginResult.SUCCESSO_ORGANIZER;
            default -> throw new LoginException("Ruolo utente non riconosciuto dal sistema.");
        };
    }
}