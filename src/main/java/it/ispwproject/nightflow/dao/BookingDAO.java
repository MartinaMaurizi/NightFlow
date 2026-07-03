package it.ispwproject.nightflow.dao;

import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.Booking;

import java.util.List;

public interface BookingDAO {

    // Salva una nuova prenotazione (e aggiorna i biglietti disponibili)
    void save(Booking booking) throws DAOException;

    // Trova tutte le prenotazioni di un cliente
    List<Booking> findByClient(int clientId) throws DAOException;

    // Trova solo le prenotazioni future (eventi che devono ancora avvenire)
    List<Booking> findUpcomingByClient(int clientId) throws DAOException;

    // Trova solo le prenotazioni passate (storico degli eventi a cui il cliente ha partecipato)
    List<Booking> findPastByClient(int clientId) throws DAOException;

    // --- NUOVI METODI PER L'ORGANIZZATORE (Usati in ClientManagementController) ---

    // Trova gli eventi passati di un cliente specifico, ma solo per un determinato organizzatore
    List<Booking> findCompletedByClientAndOrganizer(int clientId, int organizerId) throws DAOException;

    // Trova gli eventi futuri di un cliente specifico, ma solo per un determinato organizzatore
    List<Booking> findUpcomingByClientAndOrganizer(int clientId, int organizerId) throws DAOException;

    // ------------------------------------------------------------------------------

    // Annulla una prenotazione (e rimette a disposizione il biglietto)
    void cancel(int bookingId, int clientId) throws DAOException;

    // Trova tutte le prenotazioni nel sistema (utile per l'amministratore o reportistica)
    List<Booking> findAll() throws DAOException;

    void updateStatus(int bookingId, String status) throws DAOException;
    }
