package it.ispwproject.nightflow.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import it.ispwproject.nightflow.exception.LoginException;

public final class PasswordUtils {

    private PasswordUtils() {}

    /**
     * Esegue l'hashing di una password utilizzando SHA-256.
     * Trasforma la password in una stringa esadecimale non reversibile.
     */
    public static String hash(String password) throws LoginException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new LoginException("Errore interno durante la cifratura della password.", e);
        }
    }
}