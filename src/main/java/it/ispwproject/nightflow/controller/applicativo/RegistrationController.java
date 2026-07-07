package it.ispwproject.nightflow.controller.applicativo;

import it.ispwproject.nightflow.bean.RegistrationBean;
import it.ispwproject.nightflow.dao.*;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.*;
import it.ispwproject.nightflow.enumerator.Role;
import it.ispwproject.nightflow.exception.RegistrationException;
import it.ispwproject.nightflow.pattern.singleton.SessionManager; // 🌟 IMPORTANTE

public class RegistrationController {

    public void register(RegistrationBean bean) throws RegistrationException {
        // 1. Validazione password
        if (!bean.getPassword().equals(bean.getConfirmPassword())) {
            throw new RegistrationException("Le password non coincidono.");
        }

        RegistrationDAO dao = DAOFactory.getRegistrationDAO();
        try {
            // 2. Verifica email esistente
            if (dao.emailExists(bean.getEmail())) {
                throw new RegistrationException("Email già registrata.");
            }
        } catch (DAOException e) {
            throw new RegistrationException("Errore durante la verifica dell'email.");
        }

        // 3. Creazione e popolamento dell'utente
        User user;
        if (bean.getRole() == Role.ORGANIZER) {
            user = new Organizer();
            // Qui andrebbero gli altri set per l'organizzatore
        } else {
            Client client = new Client();

            client.setName(bean.getName());
            client.setSurname(bean.getSurname());
            client.setEmail(bean.getEmail());
            client.setPassword(bean.getPassword());
            client.setDateOfBirth(bean.getDateOfBirth());
            user = client;
        }

        // 4. Salvataggio nel Database
        try {
            dao.save(user, bean.getLocalNames());

            it.ispwproject.nightflow.demo.DemoDataStore.getInstance().getUsers().add(user);
            SessionManager.getInstance().setLoggedUser(user);

        } catch (DAOException e) {
            throw new RegistrationException("Errore durante il salvataggio nel database: " + e.getMessage());
        }
    }
}