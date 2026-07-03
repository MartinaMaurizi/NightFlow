package it.ispwproject.nightflow.dao;

import it.ispwproject.nightflow.dao.db.*;
import it.ispwproject.nightflow.dao.file.BookingDAOFile;
import it.ispwproject.nightflow.dao.memory.*;

public class DAOFactory {

    public static final String DATABASE = "database";
    public static final String FILE     = "file";
    public static final String MEMORY   = "memory";

    private static String persistence = DATABASE;

    private DAOFactory() {}

    public static void setPersistence(String mode) {
        if (mode != null && !mode.isBlank()) {
            persistence = mode;
        }
    }

    public static String getPersistence() {
        return persistence;
    }

    public static LoginDAO getLoginDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new LoginDAOMemory();
        return new LoginDAODB();
    }

    public static BookingDAO getBookingDAO() {
        return switch (persistence.toLowerCase()) {
            case FILE   -> new BookingDAOFile();
            case MEMORY -> new BookingDAOMemory();
            default     -> new BookingDAODB();
        };
    }

    public static EventDAO getEventDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new EventDAOMemory();
        return new EventDAODB();
    }

    public static OrganizerDAO getOrganizerDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new OrganizerDAOMemory();
        return new OrganizerDAODB();
    }

    public static ClientDAO getClientDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new ClientDAOMemory();
        return new ClientDAODB();
    }

    public static RegistrationDAO getRegistrationDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) {
            return new RegistrationDAOMemory();
        }
        return new RegistrationDAODB();
    }

    public static UserDAO getUserDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new UserDAOMemory();
        return new UserDAODB();
    }
}