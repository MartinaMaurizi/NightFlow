package it.ispwproject.nightflow.dao.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import it.ispwproject.nightflow.dao.AbstractBookingDAO;
import it.ispwproject.nightflow.enumerator.BookingStatus;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.Booking;
import it.ispwproject.nightflow.model.Event;
import it.ispwproject.nightflow.util.logger.AppLogger;

import java.io.*;
import java.lang.reflect.Type;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAOFile extends AbstractBookingDAO {

    private static final String FILE_PATH        = "bookings.json";
    private static final String EVENTS_FILE_PATH = "events.json";
    private final Gson gson;

    public BookingDAOFile() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .addSerializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) { return f.getName().equals("observers"); }
                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) { return false; }
                })
                .addDeserializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) { return f.getName().equals("observers"); }
                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) { return false; }
                })
                .setPrettyPrinting()
                .create();

        loadAllFromFile().forEach(this::addToCache);
    }

    @Override
    public void save(Booking booking) throws DAOException {
        booking.setId(generateId());
        booking.setStatus(BookingStatus.CONFIRMED);
        addToCache(booking);
        saveToFile();
        updateEventTickets(booking.getEvent().getId(), false);
    }

    // SALVATAGGIO SU FILE
    @Override
    public void update(Booking booking) throws DAOException {
        boolean found = false;
        // Cerchiamo la prenotazione nella cache
        for (int i = 0; i < identityMap.size(); i++) {
            if (identityMap.get(i).getId() == booking.getId()) {
                // Sostituiamo il vecchio oggetto con quello aggiornato
                identityMap.set(i, booking);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new DAOException("Prenotazione " + booking.getId() + " non trovata nel file JSON.");
        }

        // Salviamo la lista aggiornata nel file
        saveToFile();
    }

    @Override
    public void updateStatus(int bookingId, String status) throws DAOException {
        Booking booking = findInCache(bookingId);
        if (booking == null) throw new DAOException("Prenotazione non trovata: " + bookingId);
        booking.setStatus(BookingStatus.valueOf(status));
        saveToFile();
    }

    @Override
    public List<Booking> findByClient(int clientId) throws DAOException {
        return findInCacheByClient(clientId);
    }

    @Override
    public List<Booking> findAll() throws DAOException {
        return new ArrayList<>(identityMap);
    }

    @Override
    public List<Booking> findUpcomingByClient(int clientId) throws DAOException {
        LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());
        return identityMap.stream()
                .filter(b -> b.getClient() != null && b.getClient().getId() == clientId
                        && b.getStatus() == BookingStatus.CONFIRMED
                        && b.getEvent().getDateTime().isAfter(now))
                .toList();
    }

    @Override
    public List<Booking> findPastByClient(int clientId) throws DAOException {
        LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());
        return identityMap.stream()
                .filter(b -> b.getClient() != null && b.getClient().getId() == clientId
                        && b.getStatus() == BookingStatus.CONFIRMED
                        && b.getEvent().getDateTime().isBefore(now))
                .toList();
    }

    @Override
    public List<Booking> findCompletedByClientAndOrganizer(int clientId, int organizerId) throws DAOException {
        LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());
        return identityMap.stream()
                .filter(b -> b.getClient() != null && b.getClient().getId() == clientId
                        && b.getEvent() != null && b.getEvent().getOrganizerId() == organizerId
                        && b.getStatus() == BookingStatus.CONFIRMED
                        && b.getEvent().getDateTime().isBefore(now))
                .toList();
    }

    @Override
    public List<Booking> findUpcomingByClientAndOrganizer(int clientId, int organizerId) throws DAOException {
        LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());
        return identityMap.stream()
                .filter(b -> b.getClient() != null && b.getClient().getId() == clientId
                        && b.getEvent() != null && b.getEvent().getOrganizerId() == organizerId
                        && b.getStatus() == BookingStatus.CONFIRMED
                        && b.getEvent().getDateTime().isAfter(now))
                .toList();
    }

    @Override
    public void cancel(int bookingId, int clientId) throws DAOException {
        Booking booking = findInCache(bookingId);
        if (booking == null || booking.getClient().getId() != clientId) {
            throw new DAOException("Prenotazione non trovata o non autorizzata.");
        }
        booking.cancel();
        updateInCache(bookingId);
        saveToFile();
        updateEventTickets(booking.getEvent().getId(), true);
    }

    private int generateId() {
        return identityMap.stream().mapToInt(Booking::getId).max().orElse(0) + 1;
    }

    private List<Booking> loadAllFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Booking>>() {}.getType();
            List<Booking> loaded = gson.fromJson(reader, listType);
            return loaded != null ? loaded : new ArrayList<>();
        } catch (IOException e) {
            AppLogger.logError("Errore lettura file JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveToFile() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(identityMap, writer);
        } catch (IOException e) {
            AppLogger.logError("Errore scrittura file JSON: " + e.getMessage());
        }
    }

    private void updateEventTickets(int eventId, boolean increment) {
        File file = new File(EVENTS_FILE_PATH);
        if (!file.exists()) return;
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Event>>() {}.getType();
            List<Event> events = gson.fromJson(reader, listType);
            if (events == null) return;
            events.stream().filter(e -> e.getId() == eventId).findFirst()
                    .ifPresent(e -> e.setAvailableTickets(e.getAvailableTickets() + (increment ? 1 : -1)));
            try (Writer writer = new FileWriter(file)) { gson.toJson(events, writer); }
        } catch (IOException e) {
            AppLogger.logError("Errore aggiornamento eventi JSON: " + e.getMessage());
        }
    }
    @Override
    public List<Booking> getBookingsByEventId(int eventId) throws DAOException {
        // Filtriamo la cache (identityMap) che è sempre sincronizzata con il file
        return identityMap.stream()
                .filter(b -> b.getEvent() != null
                        && b.getEvent().getId() == eventId
                        && b.getStatus() == BookingStatus.CONFIRMED)
                .toList();
    }
    @Override
    public List<Booking> findCancelledByClient(int clientId) throws DAOException {
        // Filtriamo dalla nostra 'identityMap' (la cache in memoria che legge/scrive sul file JSON)
        return identityMap.stream()
                .filter(b -> b.getClient() != null && b.getClient().getId() == clientId
                        && b.getStatus() == BookingStatus.CANCELLED)
                .toList();
    }
}