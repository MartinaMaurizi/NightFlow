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
import it.ispwproject.nightflow.util.ValidationUtils; // 🌟 Aggiunto l'import della tua utility

public class RegistrationController {

    public void register(RegistrationBean bean) throws RegistrationException {
        // 1. Validazione completa e pulita delegata a metodi privati
        validateBean(bean);

        RegistrationDAO dao = DAOFactory.getRegistrationDAO();
        try {
            // 2. Verifica se l'email esiste già
            if (dao.emailExists(bean.getEmail())) {
                throw new RegistrationException("Email già registrata. Usa un'altra email.");
            }
        } catch (DAOException e) {
            throw new RegistrationException("Errore durante la verifica dell'email.");
        }

        // 3. Cifratura della password
        String hashedPassword;
        try {
            hashedPassword = PasswordUtils.hash(bean.getPassword());
        } catch (LoginException e) {
            throw new RegistrationException("Errore interno durante la cifratura della password.");
        }

        // 4. Creazione e popolamento dell'utente (usando i nuovi costruttori snelli!)
        User user;
        if (bean.getRole() == Role.ORGANIZER) {
            // Mettiamo id=0 o un valore di default visto che lo genererà il database
            Organizer organizer = new Organizer(0, bean.getName(), bean.getSurname(), bean.getEmail(), hashedPassword);
            organizer.setDateOfBirth(bean.getDateOfBirth());
            organizer.setGender(bean.getGender());
            organizer.setCountry(bean.getCountry());
            organizer.setCity(bean.getCity());

            if (bean.getLocalNames() != null) {
                organizer.setLocalNames(bean.getLocalNames());
            }
            user = organizer;
        } else {
            Client client = new Client(0, bean.getName(), bean.getSurname(), bean.getEmail(), hashedPassword);
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

            User savedUser = retrieveSavedUser(bean.getEmail(), user);

            SessionManager.getInstance().setLoggedUser(savedUser);
            SessionManager.getInstance().setSessionBean(
                    new it.ispwproject.nightflow.bean.SessionBean(savedUser.getEmail(), savedUser.getRole())
            );

            changeDatabaseRole(savedUser.getRole());

        } catch (DAOException e) {
            throw new RegistrationException("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    // ─── NUOVI METODI DI VALIDAZIONE (Ispirati alla logica della tua collega) ───

    private void validateBean(RegistrationBean bean) throws RegistrationException {
        if (bean == null) {
            throw new RegistrationException("Dati di registrazione mancanti.");
        }

        validateRequiredField(bean.getName(), "Il nome è obbligatorio.");
        validateRequiredField(bean.getSurname(), "Il cognome è obbligatorio.");
        validateRequiredField(bean.getEmail(), "L'email è obbligatoria.");

        // Uso della tua ValidationUtils
        if (!ValidationUtils.isValidEmail(bean.getEmail())) {
            throw new RegistrationException("Formato email non valido.");
        }

        validatePassword(bean);

        if (bean.getRole() == null) {
            throw new RegistrationException("Devi selezionare un ruolo (Cliente o Organizzatore).");
        }

    }

    private void validateRequiredField(String value, String message) throws RegistrationException {
        if (value == null || value.trim().isEmpty()) {
            throw new RegistrationException(message);
        }
    }

    private void validatePassword(RegistrationBean bean) throws RegistrationException {
        if (bean.getPassword() == null || bean.getPassword().length() < 8) {
            throw new RegistrationException("La password deve contenere almeno 8 caratteri.");
        }
        if (!bean.getPassword().equals(bean.getConfirmPassword())) {
            throw new RegistrationException("Le password non coincidono.");
        }
    }

    // ─── METODI PRIVATI ESISTENTI ───

    private User retrieveSavedUser(String email, User fallbackUser) {
        try {
            return DAOFactory.getUserDAO().findByEmail(email);
        } catch (Exception e) {
            return fallbackUser;
        }
    }

    private void changeDatabaseRole(Role role) throws RegistrationException {
        if (!DAOFactory.MEMORY.equalsIgnoreCase(DAOFactory.getPersistence())) {
            try {
                ConnectionFactory.changeRole(role);
            } catch (java.sql.SQLException ex) {
                throw new RegistrationException("Errore nel cambio permessi DB: " + ex.getMessage());
            }
        }
    }
}