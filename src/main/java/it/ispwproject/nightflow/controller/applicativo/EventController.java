package it.ispwproject.nightflow.controller.applicativo;

import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.dao.DAOFactory;
import it.ispwproject.nightflow.dao.EventDAO;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.exception.AvailabilityException;
import it.ispwproject.nightflow.model.Event;
import it.ispwproject.nightflow.model.Organizer;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class EventController {

    private final EventDAO eventDAO;

    public EventController() {
        this.eventDAO = DAOFactory.getEventDAO();
    }

    // Creazione evento con controllo sovrapposizioni
    public void createEvent(EventBean eventBean) throws DAOException, AvailabilityException {
        Organizer organizer = (Organizer) SessionManager.getInstance().getLoggedUser();

        // Validazione: controllo se il locale è già occupato nello stesso orario
        List<Event> existingEvents = eventDAO.findByLocalName(eventBean.getLocalName());

        for (Event e : existingEvents) {
            // Controllo sovrapposizione data/ora esatta
            if (e.getDateTime().equals(eventBean.getDateTime())) {
                throw new AvailabilityException("Il locale '" + eventBean.getLocalName() +
                        "' ha già un evento in questo orario.");
            }
        }

        // Creazione e salvataggio
        Event newEvent = new Event(0, eventBean.getName(), eventBean.getDescription(),
                eventBean.getDateTime(), eventBean.getLocation(),
                eventBean.getLocalName(), eventBean.getAvailableTickets(),
                eventBean.getPrice(), organizer.getId());

        eventDAO.save(newEvent);
        eventBean.setId(newEvent.getId());
    }

    public List<EventBean> getOrganizerEvents() throws DAOException {
        Organizer organizer = (Organizer) SessionManager.getInstance().getLoggedUser();
        List<Event> events = eventDAO.findByOrganizerId(organizer.getId());

        List<EventBean> eventBeans = new ArrayList<>();
        for (Event e : events) {
            // 🌟 Sostituito il costruttore gigante con i metodi Set
            EventBean bean = new EventBean();
            bean.setId(e.getId());
            bean.setName(e.getName());
            bean.setDescription(e.getDescription());
            bean.setDateTime(e.getDateTime());
            bean.setLocation(e.getLocation());
            bean.setLocalName(e.getLocalName());
            bean.setAvailableTickets(e.getAvailableTickets());
            bean.setPrice(e.getPrice());

            eventBeans.add(bean);
        }
        return eventBeans;
    }

    public void deleteEvent(int eventId) throws DAOException {
        Organizer organizer = (Organizer) SessionManager.getInstance().getLoggedUser();
        eventDAO.delete(eventId, organizer.getId());
    }
}