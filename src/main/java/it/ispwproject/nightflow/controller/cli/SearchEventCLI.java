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
            // 1. Usi ESATTAMENTE il metodo che hai già nel Controller!
            List<EventBean> allUpcoming = eventController.getAllUpcomingEvents();

            // 2. Filtri la lista direttamente qui nella CLI
            String lowerKw = keyword.toLowerCase();
            List<EventBean> risultati = allUpcoming.stream()
                    .filter(e -> e.getName().toLowerCase().contains(lowerKw) ||
                            e.getLocalName().toLowerCase().contains(lowerKw) ||
                            e.getLocation().toLowerCase().contains(lowerKw))
                    .toList();

            // 3. Mostri i risultati
            if (risultati.isEmpty()) {
                view.mostraNessunRisultato();
            } else {
                view.mostraRisultati(risultati);
            }

        } catch (DAOException e) {
            AppLogger.logError("Errore durante la ricerca: " + e.getMessage());
            view.mostraErrore("Si è verificato un errore. Riprova più tardi.");
        }

        view.attesaInvio();
        goBack(context);
    }
}