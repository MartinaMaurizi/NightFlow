package it.ispwproject.nightflow.exception;

public class RegistrationException extends Exception {

    private static final long serialVersionUID = 1L;

    public RegistrationException() {
        super("Errore durante la registrazione.");
    }

    public RegistrationException(String message) {
        super(message);
    }

    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}