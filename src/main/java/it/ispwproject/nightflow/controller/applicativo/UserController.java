package it.ispwproject.nightflow.controller.applicativo;

import it.ispwproject.nightflow.bean.ClientBean;
import it.ispwproject.nightflow.bean.OrganizerBean;
import it.ispwproject.nightflow.dao.DAOFactory;
import it.ispwproject.nightflow.dao.UserDAO;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.Organizer;
import it.ispwproject.nightflow.model.User;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.util.ValidationUtils;

import java.util.List;

/**
 * Controller applicativo per la gestione del profilo utente e delle impostazioni.
 */
public class UserController {

    private final UserDAO userDAO;

    public UserController() {
        this.userDAO = DAOFactory.getUserDAO();
    }

    public void updateEmail(String newEmail) throws DAOException {
        if (newEmail == null || newEmail.isBlank() || !ValidationUtils.isValidEmail(newEmail)) {
            throw new DAOException("Email non valida.");
        }

        User user = SessionManager.getInstance().getLoggedUser();
        if (user == null) throw new DAOException("Sessione scaduta.");

        userDAO.updateEmail(user.getId(), newEmail);
        user.setEmail(newEmail);
    }

    public void updateCity(String newCity) throws DAOException {
        if (newCity == null || newCity.isBlank()) {
            throw new DAOException("La città non può essere vuota.");
        }

        User user = SessionManager.getInstance().getLoggedUser();
        if (user == null) throw new DAOException("Sessione scaduta.");

        userDAO.updateCity(user.getId(), newCity);
        user.setCity(newCity);
    }

    public Object getProfileData() {
        User user = SessionManager.getInstance().getLoggedUser();
        if (user == null) return null;

        if (user instanceof Organizer org) {
            List<String> localNames = org.getLocalNames();
            String locale = (localNames != null && !localNames.isEmpty())
                    ? localNames.get(0)
                    : "Nessun locale associato";

            return new OrganizerBean(user.getId(), user.getName(), user.getSurname(),
                    user.getEmail(), localNames);
        }

        return new ClientBean(user.getId(), user.getName(), user.getSurname(), user.getEmail());
    }
}