package it.ispwproject.nightflow.dao.memory;

import it.ispwproject.nightflow.demo.DemoDataStore;
import it.ispwproject.nightflow.dao.LoginDAO;
import it.ispwproject.nightflow.exception.LoginException;
import it.ispwproject.nightflow.model.Credentials;
import it.ispwproject.nightflow.model.User;
import it.ispwproject.nightflow.util.logger.AppLogger;

public class LoginDAOMemory implements LoginDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public Credentials execute(String email, String plainPassword) throws LoginException {
        AppLogger.logInfo("Tentativo di login per email: " + email);

        // 1. Cerca l'utente per email
        User user = store.getUsers().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> {
                    AppLogger.logWarning("Login fallito: email non trovata - " + email);
                    return new LoginException("Credenziali non valide. Riprova.");
                });

        // 2. Controllo password (IL VERO CONTROLLO! 🔒)
        // Se la password è vuota OPPURE non corrisponde a quella salvata per questo utente...
        if (plainPassword == null || plainPassword.isBlank() || !plainPassword.equals(user.getPassword())) {
            AppLogger.logWarning("Login fallito: password errata per " + email);
            // Lanciamo l'allarme!
            throw new LoginException("Email o password errati.");
        }

        AppLogger.logInfo("Login riuscito per utente: " + email);
        return new Credentials(email, plainPassword, user.getRole());
    }
}