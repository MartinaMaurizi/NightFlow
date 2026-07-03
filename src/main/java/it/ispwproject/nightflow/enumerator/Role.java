package it.ispwproject.nightflow.enumerator;

public enum Role {
    CLIENT,
    ORGANIZER;

    public static Role fromString(String role) {
        return switch (role.toUpperCase()) {
            case "CLIENT"       -> CLIENT;
            case "ORGANIZER" -> ORGANIZER;
            default -> throw new IllegalArgumentException(
                    "Ruolo non valido: " + role);
        };
    }
}