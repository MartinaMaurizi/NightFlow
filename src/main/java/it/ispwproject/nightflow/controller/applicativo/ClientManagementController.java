package it.ispwproject.nightflow.controller.applicativo;

import it.ispwproject.nightflow.bean.*;
import it.ispwproject.nightflow.dao.*;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.model.*;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ClientManagementController {

    private final ClientDAO    clientDAO;
    private final BookingDAO   bookingDAO;

    public ClientManagementController() {
        this.clientDAO    = DAOFactory.getClientDAO();
        this.bookingDAO   = DAOFactory.getBookingDAO();
    }

    public List<ClientBean> getClients() throws DAOException {
        Organizer organizer = (Organizer) SessionManager.getInstance().getLoggedUser();
        List<ClientBean> result = new ArrayList<>();

        for (Client client : clientDAO.getByOrganizer(organizer.getId())) {
            result.add(new ClientBean(client.getId(), client.getName(),
                    client.getSurname(), client.getEmail()));
        }
        return result;
    }

    public List<BookingResponseBean> getClientBookings(int clientId) throws DAOException {
        Organizer organizer = (Organizer) SessionManager.getInstance().getLoggedUser();

        // Recupero entrambe le liste dall'interfaccia BookingDAO
        List<Booking> allBookings = new ArrayList<>();
        allBookings.addAll(bookingDAO.findCompletedByClientAndOrganizer(clientId, organizer.getId()));
        allBookings.addAll(bookingDAO.findUpcomingByClientAndOrganizer(clientId, organizer.getId()));

        return buildBookingResponseList(allBookings);
    }

    public void performCheckIn(int bookingId) throws DAOException {
        // NOTA: Assicurati di aver aggiunto updateStatus all'interfaccia BookingDAO
        bookingDAO.updateStatus(bookingId, "CHECKED_IN");
    }

    private List<BookingResponseBean> buildBookingResponseList(List<Booking> bookings) {
        List<BookingResponseBean> result = new ArrayList<>();
        for (Booking booking : bookings) {
            Event event = booking.getEvent();
            if (event == null) continue;

            EventBean eventBean = new EventBean(
                    event.getId(), event.getName(), event.getDescription(),
                    event.getDateTime(), event.getLocation(), event.getLocalName(),
                    event.getAvailableTickets(), event.getPrice()
            );

            BookingResponseBean bean = new BookingResponseBean();
            bean.setId(booking.getId());
            bean.setStatus(booking.getStatus());
            bean.setTicketCode(booking.getTicketCode());
            bean.setEvent(eventBean);
            bean.setTicketType(booking.getTicketType());

            result.add(bean);
        }
        return result;
    }
}