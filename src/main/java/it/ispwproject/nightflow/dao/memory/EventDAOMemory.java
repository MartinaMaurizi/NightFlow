package it.ispwproject.nightflow.dao.memory;

import it.ispwproject.nightflow.dao.EventDAO;
import it.ispwproject.nightflow.demo.DemoDataStore;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.Event;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

public class EventDAOMemory implements EventDAO {

    private final DemoDataStore dataStore = DemoDataStore.getInstance();

    @Override
    public Event findById(int id) {
        return dataStore.getEvents().stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Event> getAllUpcomingEvents() {
        // 🌟 Risolto: Timezone esplicita con Clock
        LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());
        return dataStore.getEvents().stream()
                .filter(e -> e.getDateTime().isAfter(now))
                .toList();
    }

    @Override
    public List<Event> findByOrganizer(int organizerId) {
        return dataStore.getEvents().stream()
                .filter(e -> e.getOrganizerId() == organizerId)
                .toList();
    }

    @Override
    public List<Event> findByOrganizerId(int organizerId) {
        return findByOrganizer(organizerId);
    }

    @Override
    public List<Event> findByLocalName(String localName) {
        return dataStore.getEvents().stream()
                .filter(e -> e.getLocalName() != null && e.getLocalName().equalsIgnoreCase(localName))
                .toList();
    }

    @Override
    public void save(Event event) {
        if (event.getId() == 0) {
            event.setId(dataStore.nextEventId());
        }
        dataStore.getEvents().add(event);
    }

    @Override
    public void update(Event event) throws DAOException {
        List<Event> events = dataStore.getEvents();
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getId() == event.getId()) {
                events.set(i, event);
                return;
            }
        }
        throw new DAOException("Evento con ID " + event.getId() + " non trovato per l'aggiornamento.");
    }

    @Override
    public void delete(int eventId) {
        dataStore.getEvents().removeIf(e -> e.getId() == eventId);
    }

    @Override
    public void delete(int eventId, int organizerId) throws DAOException {
        boolean removed = dataStore.getEvents().removeIf(e ->
                e.getId() == eventId && e.getOrganizerId() == organizerId
        );

        if (!removed) {
            throw new DAOException("Evento non trovato o permessi insufficienti.");
        }
    }
}