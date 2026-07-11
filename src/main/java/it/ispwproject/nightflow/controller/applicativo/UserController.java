package it.ispwproject.nightflow.controller.applicativo;

import it.ispwproject.nightflow.bean.ClientBean;
import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.bean.OrganizerBean;
import it.ispwproject.nightflow.dao.DAOFactory;
import it.ispwproject.nightflow.dao.UserDAO;
import it.ispwproject.nightflow.enumerator.Role;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.exception.LoginException;
import it.ispwproject.nightflow.model.Client;
import it.ispwproject.nightflow.model.Organizer;
import it.ispwproject.nightflow.model.User;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.util.PasswordUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller applicativo per la gestione del profilo utente e delle impostazioni.
 */
public class UserController {

    private final UserDAO userDAO;

    public UserController() {
        this.userDAO = DAOFactory.getUserDAO();
    }

    // 🌟 NUOVO METODO: Trasforma l'Entity della sessione in un Bean per la View
    public Object getUserProfile() {
        // Il controller legge l'Entity dalla Sessione
        User loggedUser = SessionManager.getInstance().getLoggedUser();

        if (loggedUser.getRole() == Role.ORGANIZER) {
            Organizer org = (Organizer) loggedUser;

            // Logica applicativa: recupero dei locali gestiti tramite EventController
            List<String> nomiLocali = new ArrayList<>();
            try {
                EventController ec = new EventController();
                List<EventBean> eventi = ec.getOrganizerEvents();
                nomiLocali = eventi.stream()
                        .map(EventBean::getLocalName)
                        .distinct()
                        .toList();
            } catch (Exception e) {
                nomiLocali.add("Errore caricamento");
            }

            // Impacchetta i dati nel Bean e lo restituisce alla View
            return new OrganizerBean(
                    org.getId(), org.getName(), org.getSurname(), org.getEmail(), nomiLocali);

        } else {
            Client cli = (Client) loggedUser;
            // Restituisce un ClientBean alla View
            return new ClientBean(
                    cli.getId(), cli.getName(), cli.getSurname(), cli.getEmail());
        }
    }

    // ── METODO ORIGINALE (Invariato) ──
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