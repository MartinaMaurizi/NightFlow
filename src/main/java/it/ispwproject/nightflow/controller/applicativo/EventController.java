package it.ispwproject.nightflow.controller.applicativo;

import it.ispwproject.nightflow.bean.BookingResponseBean;
import it.ispwproject.nightflow.bean.ClientBean;
import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.dao.BookingDAO;
import it.ispwproject.nightflow.dao.DAOFactory;
import it.ispwproject.nightflow.dao.EventDAO;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.exception.AvailabilityException;
import it.ispwproject.nightflow.model.Booking;
import it.ispwproject.nightflow.model.Event;
import it.ispwproject.nightflow.model.Organizer;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.service.NotificationService;
import it.ispwproject.nightflow.util.logger.AppLogger;

import java.util.ArrayList;
import java.util.List;

public class EventController {

    private final EventDAO eventDAO;
    private final BookingDAO bookingDAO;

    public EventController() {
        this.eventDAO = DAOFactory.getEventDAO();
        this.bookingDAO = DAOFactory.getBookingDAO();
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

    // Recupero eventi dell'organizzatore loggato
    public List<EventBean> getOrganizerEvents() throws DAOException {
        Organizer organizer = (Organizer) SessionManager.getInstance().getLoggedUser();
        List<Event> events = eventDAO.findByOrganizerId(organizer.getId());

        List<EventBean> eventBeans = new ArrayList<>();
        for (Event e : events) {
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

    // Recupero di tutti gli eventi futuri per il Cliente
    public List<EventBean> getAllUpcomingEvents() throws DAOException {
        List<Event> events = eventDAO.getAllUpcomingEvents();
        List<EventBean> eventBeans = new ArrayList<>();

        for (Event e : events) {
            EventBean bean = new EventBean();
            bean.setId(e.getId());
            bean.setName(e.getName());
            bean.setDescription(e.getDescription());
            bean.setDateTime(e.getDateTime());
            bean.setLocation(e.getLocation());
            bean.setLocalName(e.getLocalName());
            bean.setTotalCapacity(e.getTotalCapacity());
            bean.setAvailableTickets(e.getAvailableTickets());
            bean.setPrice(e.getPrice());

            eventBeans.add(bean);
        }
        return eventBeans;
    }

    // Cancellazione evento (senza invio email)
    public void deleteEvent(int eventId) throws DAOException {
        Organizer organizer = (Organizer) SessionManager.getInstance().getLoggedUser();

        List<Booking> bookings = bookingDAO.getBookingsByEventId(eventId);

        for (Booking b : bookings) {
            bookingDAO.cancel(b.getId(), b.getClient().getId());
        }

        eventDAO.delete(eventId, organizer.getId());
    }

    // Modifica data evento con invio email diretto
    public void updateEventDate(EventBean eventBean) throws DAOException {
        Event event = eventDAO.findById(eventBean.getId());
        if (event != null) {
            // 1. Salva la vecchia data per la notifica
            String oldDate = event.getDateTime().toString();

            // 2. Aggiorna l'evento sul DB
            event.setDateTime(eventBean.getDateTime());
            eventDAO.update(event);

            // 3. Notifica tutti i clienti prenotati direttamente dal Controller
            List<Booking> bookings = bookingDAO.getBookingsByEventId(event.getId());
            String reason = "La data dell'evento " + event.getName() +
                    " è stata spostata da " + oldDate + " a " + eventBean.getDateTime();

            for (Booking b : bookings) {
                try {
                    // Costruiamo a mano il ClientBean
                    ClientBean clientBean = new ClientBean(
                            b.getClient().getId(),
                            b.getClient().getName(),
                            b.getClient().getSurname(),
                            b.getClient().getEmail()
                    );

                    // Costruiamo a mano l'EventBean
                    EventBean eventBeanTemp = new EventBean();
                    eventBeanTemp.setId(b.getEvent().getId());
                    eventBeanTemp.setName(b.getEvent().getName());
                    eventBeanTemp.setDescription(b.getEvent().getDescription());
                    eventBeanTemp.setDateTime(b.getEvent().getDateTime());
                    eventBeanTemp.setLocation(b.getEvent().getLocation());
                    eventBeanTemp.setLocalName(b.getEvent().getLocalName());
                    eventBeanTemp.setAvailableTickets(b.getEvent().getAvailableTickets());
                    eventBeanTemp.setPrice(b.getEvent().getPrice());

                    // Creiamo il BookingResponseBean di NightFlow finale
                    BookingResponseBean response = new BookingResponseBean(
                            b.getId(),
                            b.getStatus(),
                            b.getTicketCode(),
                            clientBean,
                            eventBeanTemp,
                            b.getTicketType(),
                            b.getPaymentMethod()
                    );

                    // Invia l'email tramite il servizio dedicato
                    NotificationService.sendEventModification(
                            b.getClient().getEmail(),
                            response,
                            reason
                    );
                } catch (Exception e) {
                    AppLogger.logWarning("Impossibile inviare notifica modifica a " +
                            b.getClient().getEmail() + ": " + e.getMessage());
                }
            }
        }
    }
}