package it.ispwproject.nightflow.controller.cli;

import it.ispwproject.nightflow.pattern.state.AbstractCLIState;
import it.ispwproject.nightflow.pattern.state.CLIStateMachine;
import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.controller.applicativo.EventController;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.view.cli.SearchEventView;
import it.ispwproject.nightflow.util.logger.AppLogger;

import java.util.List;

public class SearchEventCLI extends AbstractCLIState {

    private final EventController eventController = new EventController();
    private final SearchEventView view = new SearchEventView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        String keyword = view.chiediTermineRicerca();

        if (keyword.equals("0")) {
            goBack(context);
            return;
        }

        try {
            List<EventBean> allUpcoming = eventController.getAllUpcomingEvents();

            String lowerKw = keyword.toLowerCase();
            List<EventBean> risultati = allUpcoming.stream()
                    .filter(e -> e.getName().toLowerCase().contains(lowerKw) ||
                            e.getLocalName().toLowerCase().contains(lowerKw) ||
                            e.getLocation().toLowerCase().contains(lowerKw))
                    .toList();

            if (risultati.isEmpty()) {
                view.mostraNessunRisultato();
                view.attesaInvio();
                goBack(context);
            } else {
                view.mostraRisultati(risultati);
                // chiediamo di selezionare un evento invece di uscire subito
                int scelta = view.chiediSelezioneEvento(risultati.size());

                if (scelta == 0) {
                    goBack(context);
                } else {
                    EventBean selectedEvent = risultati.get(scelta - 1);
                    // Passiamo l'evento selezionato direttamente al BookTicketCLI!
                    goNext(context, new BookTicketCLI(selectedEvent));
                }
            }

        } catch (DAOException e) {
            AppLogger.logError("Errore durante la ricerca: " + e.getMessage());
            view.mostraErrore("Si è verificato un errore. Riprova più tardi.");
            view.attesaInvio();
            goBack(context);
        }
    }
}