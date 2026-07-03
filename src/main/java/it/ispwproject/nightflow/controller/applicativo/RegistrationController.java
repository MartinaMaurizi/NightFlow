package it.ispwproject.nightflow.controller.applicativo;

import it.ispwproject.nightflow.bean.RegistrationBean;
import it.ispwproject.nightflow.dao.*;
import it.ispwproject.nightflow.model.*;
import it.ispwproject.nightflow.enumerator.Role;

public class RegistrationController {
    public void register(RegistrationBean bean) throws Exception {
        if (!bean.getPassword().equals(bean.getConfirmPassword())) {
            throw new Exception("Le password non coincidono.");
        }

        RegistrationDAO dao = DAOFactory.getRegistrationDAO();
        if (dao.emailExists(bean.getEmail())) {
            throw new Exception("Email già registrata.");
        }

        User user = (bean.getRole() == Role.ORGANIZER) ? new Organizer() : new Client();
        user.setName(bean.getName());
        user.setSurname(bean.getSurname());
        user.setEmail(bean.getEmail());
        user.setPassword(bean.getPassword());
        user.setRole(bean.getRole());
        user.setDateOfBirth(bean.getDateOfBirth());
        user.setGender(bean.getGender());
        user.setCountry(bean.getCountry());
        user.setCity(bean.getCity());

        // Salvataggio nel DB (usando il metodo che abbiamo aggiornato per la lista locali)
        dao.save(user, bean.getLocalNames());
    }
}