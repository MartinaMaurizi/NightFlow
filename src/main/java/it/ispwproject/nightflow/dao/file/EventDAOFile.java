package it.ispwproject.nightflow.dao.file;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.ispwproject.nightflow.dao.EventDAO;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.Event;
import java.io.*;
import java.lang.reflect.Type;
import java.time.*;
import java.util.*;

public class EventDAOFile implements EventDAO {

    private static final String FILE_PATH = "events.json";
    private final List<Event> cache = new ArrayList<>();
    private final Gson gson;

    public EventDAOFile() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .setPrettyPrinting()
                .create();
        loadAll();
    }

    private void loadAll() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Event>>() {}.getType();
            List<Event> loaded = gson.fromJson(reader, listType);
            if (loaded != null) {
                cache.clear();
                cache.addAll(loaded);
            }
        } catch (IOException e) { /* Gestisci errore */ }
    }

    private void saveToFile() throws DAOException {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(cache, writer);
        } catch (IOException e) {
            throw new DAOException("Errore di scrittura file: " + e.getMessage());
        }
    }

    @Override
    public Event findById(int id) {
        return cache.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<Event> getAllUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        return cache.stream().filter(e -> e.getDateTime().isAfter(now)).toList();
    }

    @Override
    public void save(Event event) throws DAOException {
        event.setId(cache.stream().mapToInt(Event::getId).max().orElse(0) + 1);
        cache.add(event);
        saveToFile();
    }

    @Override
    public void update(Event event) throws DAOException {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId() == event.getId()) {
                cache.set(i, event);
                saveToFile();
                return;
            }
        }
    }

    @Override
    public List<Event> findByLocalName(String localName) {
        return cache.stream().filter(e -> e.getLocalName().equalsIgnoreCase(localName)).toList();
    }

    @Override
    public List<Event> findByOrganizerId(int organizerId) {
        return cache.stream().filter(e -> e.getOrganizerId() == organizerId).toList();
    }

    @Override
    public void delete(int eventId, int organizerId) throws DAOException {
        boolean removed = cache.removeIf(e -> e.getId() == eventId && e.getOrganizerId() == organizerId);
        if (removed) saveToFile();
    }
}