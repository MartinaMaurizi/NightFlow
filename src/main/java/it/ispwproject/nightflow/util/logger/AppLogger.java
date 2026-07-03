package it.ispwproject.nightflow.util.logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class AppLogger {

    // Aggiornato il nome del logger per il nuovo dominio
    private static final Logger LOGGER = Logger.getLogger("NightFlowLogger");

    static {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(handler);
        LOGGER.setUseParentHandlers(false);
        LOGGER.setLevel(Level.ALL);
    }

    // Costruttore privato per impedire l'istanziazione (Utility Class Pattern)
    private AppLogger() {}

    public static Logger getLogger() {
        return LOGGER;
    }

    public static void logInfo(String msg) {
        LOGGER.log(Level.INFO, msg);
    }

    public static void logWarning(String msg) {
        LOGGER.log(Level.WARNING, msg);
    }

    public static void logError(String msg) {
        LOGGER.log(Level.SEVERE, msg);
    }
}