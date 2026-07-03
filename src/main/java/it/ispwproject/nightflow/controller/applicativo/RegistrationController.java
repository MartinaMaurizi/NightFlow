package it.ispwproject.nightflow.controller.applicativo;

import it.ispwproject.nightflow.bean.RegistrationBean;
import it.ispwproject.nightflow.dao.*;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.*;
import it.ispwproject.nightflow.enumerator.Role;
import it.ispwproject.nightflow.exception.RegistrationException;

public class RegistrationController {
    // Ora il metodo dichiara esattamente cosa può andare storto
    public void register(RegistrationBean bean) throws RegistrationException {
        if (!bean.getPassword().equals(bean.getConfirmPassword())) {
            throw new RegistrationException("Le password non coincidono.");
        }

        RegistrationDAO dao = DAOFactory.getRegistrationDAO();
        try {
            if (dao.emailExists(bean.getEmail())) {
                throw new RegistrationException("Email già registrata.");
            }
        } catch (DAOException e) {
            // Qui convertiamo un errore tecnico (DAO) in un errore logico (Registration)
            throw new RegistrationException("Errore durante la verifica dell'email.");
        }

        User user = (bean.getRole() == Role.ORGANIZER) ? new Organizer() : new Client();
        // ... (resto del codice rimane uguale)

        try {
            dao.save(user, bean.getLocalNames());
        } catch (DAOException e) {
            throw new RegistrationException("Errore durante il salvataggio nel database.");
        }
    }
}