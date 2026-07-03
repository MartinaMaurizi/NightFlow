package it.ispwproject.nightflow.exception;

public class AvailabilityException extends Exception {

    public AvailabilityException(String message) {
        super(message);
    }

    public AvailabilityException(String message, Throwable cause) {
        super(message, cause);
    }
}