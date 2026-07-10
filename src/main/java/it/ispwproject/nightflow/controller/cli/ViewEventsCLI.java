package it.ispwproject.nightflow.controller.cli;

import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.view.cli.ViewEventsView;
import it.ispwproject.nightflow.controller.applicativo.EventController;
import it.ispwproject.nightflow.pattern.state.AbstractCLIState;
import it.ispwproject.nightflow.pattern.state.CLIStateMachine;

import java.time.LocalDateTime;
import java.util.List;

public class ViewEventsCLI extends AbstractCLIState {

    private final ViewEventsView view = new ViewEventsView();
    private final EventController eventController = new EventController();

    @Override
    public void action(CLIStateMachine context) {
        view.mostraIntestazione();

        while (true) {
            try {
                // 1. Carica gli eventi a ogni iterazione per avere i dati sempre freschi
                List<EventBean> eventi = eventController.getOrganizerEvents();

                // 2. Se non ci sono eventi, avvisa ed esce
                if (eventi.isEmpty()) {
                    view.mostraListaEventi(eventi); // La view stampa "Nessun evento in programma"
                    view.attesaInvio();
                    goBack(context);
                    return;
                }

                // 3. Mostra la lista degli eventi
                view.mostraListaEventi(eventi);

                // 4. Chiede quale evento gestire
                int scelta = view.chiediSelezioneEvento(eventi.size());
                if (scelta == 0) {
                    goBack(context); // L'utente ha scelto 0 per tornare alla Dashboard
                    return;
                }

                // Ottieni l'evento selezionato (l'indice della lista è base-0, la scelta base-1)
                EventBean selectedEvent = eventi.get(scelta - 1);

                // 5. Mostra il menu per Modificare o Eliminare lo specifico evento
                int azione = view.mostraMenuAzione(selectedEvent);
                switch (azione) {
                    case 1 -> gestisciModificaData(selectedEvent);
                    case 2 -> gestisciEliminazione(selectedEvent);
                    case 0 -> {
                        // Se sceglie 0 qui, torna semplicemente alla lista degli eventi
                        // Il loop while(true) ripartirà dall'inizio aggiornando la lista
                    }
                }

            } catch (Exception e) {
                view.mostraErrore("Errore di sistema: " + e.getMessage());
                view.attesaInvio();
                goBack(context);
                return;
            }
        }
    }

    private void gestisciModificaData(EventBean event) throws Exception {
        LocalDateTime nuovaData = view.chiediNuovaDataOra();
        event.setDateTime(nuovaData);

        eventController.updateEventDate(event);

        view.mostraSuccesso("Data e ora dell'evento '" + event.getName() + "' aggiornate con successo!");
        view.attesaInvio();
    }

    private void gestisciEliminazione(EventBean event) throws Exception {
        boolean confermato = view.chiediConfermaEliminazione(event.getName());
        if (confermato) {
            eventController.deleteEvent(event.getId());
            view.mostraSuccesso("L'evento '" + event.getName() + "' è stato annullato e rimosso dal sistema.");
        } else {
            view.mostraMessaggio("Operazione annullata.");
        }
        view.attesaInvio();
    }
}