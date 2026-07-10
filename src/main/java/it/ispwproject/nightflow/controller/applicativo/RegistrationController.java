package it.ispwproject.nightflow.controller.applicativo;

import it.ispwproject.nightflow.bean.RegistrationBean;
import it.ispwproject.nightflow.dao.*;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.exception.LoginException;
import it.ispwproject.nightflow.model.*;
import it.ispwproject.nightflow.enumerator.Role;
import it.ispwproject.nightflow.exception.RegistrationException;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.util.PasswordUtils;

public class RegistrationController {

    public void register(RegistrationBean bean) throws RegistrationException {
        // 1. Validazione password (in chiaro)
        if (!bean.getPassword().equals(bean.getConfirmPassword())) {
            throw new RegistrationException("Le password non coincidono.");
        }

        RegistrationDAO dao = DAOFactory.getRegistrationDAO();
        try {
            // 2. Verifica se l'email esiste già
            if (dao.emailExists(bean.getEmail())) {
                throw new RegistrationException("Email già registrata.");
            }
        } catch (DAOException e) {
            throw new RegistrationException("Errore durante la verifica dell'email.");
        }

        // 3. Cifratura della password usando la tua utility condivisa
        String hashedPassword;
        try {
            hashedPassword = PasswordUtils.hash(bean.getPassword());
        } catch (LoginException e) {
            throw new RegistrationException("Errore interno durante la cifratura della password.");
        }

        // 4. Creazione e popolamento dell'utente
// 4. Creazione e popolamento dell'utente
        User user;
        if (bean.getRole() == Role.ORGANIZER) {
            Organizer organizer = new Organizer();
            organizer.setName(bean.getName());
            organizer.setSurname(bean.getSurname());
            organizer.setEmail(bean.getEmail());
            organizer.setPassword(hashedPassword);
            organizer.setRole(Role.ORGANIZER);

            // (Il DB richiede dob, gender, country, city per tutti)
            organizer.setDateOfBirth(bean.getDateOfBirth());
            organizer.setGender(bean.getGender());
            organizer.setCountry(bean.getCountry());
            organizer.setCity(bean.getCity());

            user = organizer;
        } else {
            Client client = new Client();
            client.setName(bean.getName());
            client.setSurname(bean.getSurname());
            client.setEmail(bean.getEmail());
            client.setPassword(hashedPassword);
            client.setRole(Role.CLIENT);

            client.setDateOfBirth(bean.getDateOfBirth());
            client.setGender(bean.getGender());
            client.setCountry(bean.getCountry());
            client.setCity(bean.getCity());

            user = client;
        }

// 5. Salvataggio
        try {
            dao.save(user, bean.getLocalNames());
            it.ispwproject.nightflow.demo.DemoDataStore.getInstance().getUsers().add(user);

            User savedUser;
            try {
                savedUser = DAOFactory.getUserDAO().findByEmail(bean.getEmail());
            } catch (Exception e) {
                savedUser = user;
            }

            SessionManager.getInstance().setLoggedUser(savedUser);
            SessionManager.getInstance().setSessionBean(
                    new it.ispwproject.nightflow.bean.SessionBean(savedUser.getEmail(), savedUser.getRole())
            );

            // Se siamo connessi al Database, cambiamo i permessi SQL all'istante
            if (!DAOFactory.MEMORY.equalsIgnoreCase(DAOFactory.getPersistence())) {
                try {
                    ConnectionFactory.changeRole(savedUser.getRole());
                } catch (java.sql.SQLException ex) {
                    throw new RegistrationException("Errore nel cambio permessi DB: " + ex.getMessage());
                }
            }

        } catch (DAOException e) {
            throw new RegistrationException("Errore durante il salvataggio: " + e.getMessage());
        }
    }
}
