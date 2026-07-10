package it.ispwproject.nightflow.controller.cli;

import it.ispwproject.nightflow.bean.BookingResponseBean;
import it.ispwproject.nightflow.controller.applicativo.BookingController;
import it.ispwproject.nightflow.enumerator.BookingStatus;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.pattern.state.AbstractCLIState;
import it.ispwproject.nightflow.pattern.state.CLIStateMachine;
import it.ispwproject.nightflow.view.cli.CancelBookingView;

import java.time.LocalDateTime;
import java.time.Clock;
import java.util.List;

public class CancelBookingCLI extends AbstractCLIState {

    private final BookingController bookingController = new BookingController();
    private final CancelBookingView view = new CancelBookingView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        int clientId = SessionManager.getInstance().getLoggedUser().getId();

        try {
            // Filtriamo le prenotazioni attive
            List<BookingResponseBean> cancellable = bookingController
                    .getAllClientBookings(clientId)
                    .stream()
                    .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                    .filter(b -> b.getEvent().getDateTime().isAfter(LocalDateTime.now(Clock.systemDefaultZone())))
                    .toList();

            if (cancellable.isEmpty()) {
                view.mostraMessaggio("Nessuna prenotazione attiva da annullare.");
                goBack(context);
                return;
            }

            view.mostraPrenotazioniAnnullabili(cancellable);
            int choice = view.chiediScelta("Seleziona la prenotazione da annullare (0 per tornare)", 0, cancellable.size());
            if (choice == 0) {
                goBack(context);
                return;
            }

            BookingResponseBean selected = cancellable.get(choice - 1);
            view.mostraRiepilogo(selected);

            if (!view.chiediConferma("Sei sicuro di voler annullare?")) {
                view.mostraMessaggio("Operazione annullata.");
                goBack(context);
                return;
            }

            bookingController.cancelBooking(selected.getId(), clientId);
            view.mostraSuccesso(); // Assicurati che la View stampi il successo tramite CLIRenderer

        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }

        goBack(context);
    }
}