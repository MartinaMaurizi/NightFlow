package it.ispwproject.nightflow.dao.db;

import it.ispwproject.nightflow.dao.ConnectionFactory;
import it.ispwproject.nightflow.dao.LoginDAO;
import it.ispwproject.nightflow.enumerator.Role;
import it.ispwproject.nightflow.exception.LoginException;
import it.ispwproject.nightflow.model.Credentials;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class LoginDAODB implements LoginDAO {

    @Override
    public Credentials execute(String email, String plainPassword) throws LoginException {
        // Se deciderai di implementare l'hashing (es. SHA-256), dovrai convertire la password qui.
        String hashedPassword = plainPassword;

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call login(?, ?, ?, ?, ?, ?)}")) {

            // Parametri di IN (dati che passiamo al DB)
            cs.setString(1, email);
            cs.setString(2, hashedPassword);

            // Parametri di OUT (dati che la Stored Procedure ci restituisce: id, nome, cognome, ruolo)
            cs.registerOutParameter(3, Types.INTEGER);
            cs.registerOutParameter(4, Types.VARCHAR);
            cs.registerOutParameter(5, Types.VARCHAR);
            cs.registerOutParameter(6, Types.VARCHAR);

            cs.execute();

            String roleStr = cs.getString(6);

            // Verifica se la Stored Procedure ha trovato una corrispondenza valida
            if (roleStr == null || roleStr.equals("NOT_FOUND")) {
                throw new LoginException("Credenziali non valide o utente inesistente. Riprova.");
            }

            // Converte in modo sicuro la stringa restituita dal DB ("CLIENT" o "ORGANIZER") nell'Enum
            Role role = Role.fromString(roleStr);
            return new Credentials(email, hashedPassword, role);

        } catch (SQLException e) {
            throw new LoginException("Errore di comunicazione col database durante il login: " + e.getMessage(), e);
        }
    }
}