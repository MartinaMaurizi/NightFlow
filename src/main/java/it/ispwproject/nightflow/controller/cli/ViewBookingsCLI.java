package it.ispwproject.nightflow.controller.cli;

import it.ispwproject.nightflow.pattern.state.AbstractCLIState;
import it.ispwproject.nightflow.pattern.state.CLIStateMachine;

import it.ispwproject.nightflow.bean.BookingResponseBean;
import it.ispwproject.nightflow.controller.applicativo.BookingController;
import it.ispwproject.nightflow.enumerator.BookingStatus;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.view.cli.ViewBookingsView;

import java.time.LocalDateTime;
import java.util.List;

public class ViewBookingsCLI extends AbstractCLIState {

    private final BookingController bookingController = new BookingController();
    private final ViewBookingsView view = new ViewBookingsView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        int clientId = SessionManager.getInstance().getLoggedUser().getId();
        try {
            // Usa i Bean e i metodi del tuo Controller
            List<BookingResponseBean> all  = bookingController.getAllClientBookings(clientId);
            List<BookingResponseBean> past = bookingController.getClientPastBookings(clientId);

            List<BookingResponseBean> confirmed = all.stream()
                    .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                    .filter(b -> b.getEvent().getDateTime().isAfter(LocalDateTime.now()))
                    .sorted((a, b) -> a.getEvent().getDateTime().compareTo(b.getEvent().getDateTime()))
                    .toList();

            List<BookingResponseBean> cancelled = all.stream()
                    .filter(b -> b.getStatus() == BookingStatus.CANCELLED)
                    .sorted((a, b) -> a.getEvent().getDateTime().compareTo(b.getEvent().getDateTime()))
                    .toList();

            boolean running = true;
            while (running) {
                view.mostraTab(confirmed.size(), cancelled.size(), past.size());
                int scelta = view.chiediScelta("Scelta", 0, 3);
                switch (scelta) {
                    case 1 -> view.mostraConfermate(confirmed);
                    case 2 -> view.mostraCancellate(cancelled);
                    case 3 -> view.mostraScadute(past);
                    case 0 -> { running = false; }
                }
            }
        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }
        goBack(context);
    }
}