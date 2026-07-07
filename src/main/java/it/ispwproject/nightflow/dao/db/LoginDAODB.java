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
        // 🌟 IL DAO RICEVE LA PASSWORD GIÀ CRIPTATA DAL CONTROLLER
        // Quindi si limita a passarla al database senza fare altre modifiche
        String hashedPassword = plainPassword;

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call login(?, ?, ?, ?, ?, ?)}")) {

            cs.setString(1, email);
            cs.setString(2, hashedPassword);

            cs.registerOutParameter(3, Types.INTEGER);
            cs.registerOutParameter(4, Types.VARCHAR);
            cs.registerOutParameter(5, Types.VARCHAR);
            cs.registerOutParameter(6, Types.VARCHAR);

            cs.execute();

            String roleStr = cs.getString(6);

            if (roleStr == null || roleStr.equals("NOT_FOUND")) {
                throw new LoginException("Credenziali non valide o utente inesistente. Riprova.");
            }

            Role role = Role.fromString(roleStr);
            return new Credentials(email, hashedPassword, role);

        } catch (SQLException e) {
            throw new LoginException("Errore DB durante il login: " + e.getMessage(), e);
        }
    }
}