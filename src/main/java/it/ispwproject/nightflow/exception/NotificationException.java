package it.ispwproject.nightflow.exception;

public class NotificationException extends Exception {

    public NotificationException() {
        super("Errore durante l'invio della notifica.");
    }

    public NotificationException(String message) {
        super(message);
    }

    public NotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}