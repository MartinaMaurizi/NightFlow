package it.ispwproject.nightflow.util;

@SuppressWarnings("java:S106")
public final class Printer {

    // Codici ANSI per i colori
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED   = "\u001B[31m";
    private static final String ANSI_CYAN  = "\u001B[36m"; // Usiamo Cyan per i titoli NightFlow
    private static final String ANSI_GREEN = "\u001B[32m";

    private Printer() {}

    public static void print(String message) {
        System.out.print(message);
    }

    public static void println(String message) {
        System.out.println(message);
    }

    // Stile "NightFlow": Cyan per le intestazioni
    public static void printTitle(String message) {
        System.out.print(ANSI_CYAN + message + ANSI_RESET);
    }

    public static void printlnTitle(String message) {
        System.out.println(ANSI_CYAN + message + ANSI_RESET);
    }

    // Messaggi di errore (Rosso)
    public static void printError(String message) {
        System.out.println(ANSI_RED + "❌ " + message + ANSI_RESET);
    }

    // Messaggi di successo (Verde)
    public static void printSuccess(String message) {
        System.out.println(ANSI_GREEN + "✅ " + message + ANSI_RESET);
    }
}