package it.ispwproject.nightflow.controller.applicativo;

import it.ispwproject.nightflow.dao.DAOFactory;
import it.ispwproject.nightflow.dao.UserDAO;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.User;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.util.PasswordUtils;
import it.ispwproject.nightflow.exception.LoginException;

/**
 * Controller applicativo per la gestione del profilo utente e delle impostazioni.
 */
public class UserController {

    private final UserDAO userDAO;

    public UserController() {
        this.userDAO = DAOFactory.getUserDAO();
    }

    public void updatePassword(String oldPassword, String newPassword) throws DAOException {
        User user = SessionManager.getInstance().getLoggedUser();
        if (user == null) throw new DAOException("Sessione scaduta. Fai di nuovo il login.");

        try {
            // 1. Proviamo a calcolare l'hash
            String hashedOldPassword = PasswordUtils.hash(oldPassword);

            // 2. Controllo logico
            if (user.getPassword() == null || !user.getPassword().equals(hashedOldPassword)) {
                throw new DAOException("La vecchia password non è corretta.");
            }

            // 3. Controllo nuova password
            if (newPassword == null || newPassword.isBlank()) {
                throw new DAOException("La nuova password non può essere vuota.");
            }

            // 4. Salvataggio hash
            String hashedNewPassword = PasswordUtils.hash(newPassword);
            userDAO.updatePassword(user.getId(), hashedNewPassword);
            user.setPassword(hashedNewPassword);

        } catch (LoginException e) {
            // Se PasswordUtils fallisce, trasformiamo l'eccezione in una DAOException
            throw new DAOException("Errore interno di sicurezza: " + e.getMessage(), e);
        }
    }

}