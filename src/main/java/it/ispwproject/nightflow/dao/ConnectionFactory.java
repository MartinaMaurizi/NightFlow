package it.ispwproject.nightflow.dao;

import it.ispwproject.nightflow.enumerator.Role;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {

    private static Connection connection;
    private static Role currentRole = null;

    private static final String PROPERTIES_FILE = "src/main/resources/db.properties";
    private static final Properties properties = new Properties();

    private ConnectionFactory() {}

    // Blocco statico per caricare il file di configurazione all'avvio dell'applicazione
    static {
        try (InputStream input = new FileInputStream(PROPERTIES_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Impossibile caricare db.properties. Assicurati che il file esista in src/main/resources/");
        }
    }

    private static void initConnection() throws SQLException {
        String url  = properties.getProperty("CONNECTION_URL");
        String user;
        String pass;

        // Se un utente è loggato, usa le credenziali del suo ruolo specifico
        if (currentRole != null) {
            user = properties.getProperty(currentRole.name() + "_USER");
            pass = properties.getProperty(currentRole.name() + "_PASS");
        } else {
            // Nessun ruolo ancora (es. fase di login/registrazione) → utente login con permessi minimi
            user = properties.getProperty("LOGIN_USER");
            pass = properties.getProperty("LOGIN_PASS");
        }

        if (user == null || pass == null) {
            throw new SQLException("Credenziali mancanti nel file properties per il ruolo: " + currentRole);
        }

        connection = DriverManager.getConnection(url, user, pass);
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            initConnection();
        }
        return connection;
    }

    // Cambia il ruolo e resetta la connessione (chiamato dal LoginController dopo un accesso riuscito)
    public static void changeRole(Role role) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        currentRole = role;
        initConnection();
    }

    // Pulisce il ruolo (chiamato al Logout o durante la registrazione)
    public static void clearRole() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        currentRole = null;
    }
}