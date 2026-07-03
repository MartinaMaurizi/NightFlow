package it.ispwproject.nightflow.controller.cli;

import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.view.cli.ManageEventsView;
import it.ispwproject.nightflow.controller.applicativo.EventController; // Assumendo tu abbia un EventController
import it.ispwproject.nightflow.pattern.state.AbstractCLIState;
import it.ispwproject.nightflow.pattern.state.CLIStateMachine;

import java.util.List;

public class ManageEventsCLI extends AbstractCLIState {

    private final ManageEventsView view = new ManageEventsView();
    // Usa il controller dedicato alla gestione degli eventi
    private final EventController eventController = new EventController();

    @Override
    public void action(CLIStateMachine context) {
        view.mostraIntestazione();

        boolean stayInMenu = true;
        while (stayInMenu) {
            int scelta = view.mostraMenuEventi(); // Es: 1. Visualizza, 2. Crea, 0. Indietro

            switch (scelta) {
                case 1:
                    gestisciVisualizzazioneEventi();
                    break;
                case 2:
                    gestisciCreazioneEvento();
                    break;
                case 0:
                    stayInMenu = false;
                    goBack(context); // Torna alla Dashboard dell'Organizzatore
                    break;
                default:
                    view.mostraErrore("Scelta non valida. Riprova.");
            }
        }
    }

    private void gestisciVisualizzazioneEventi() {
        try {
            // Sostituisci con il metodo reale del tuo controller per prendere gli eventi dell'organizzatore
            List<EventBean> eventi = eventController.getOrganizerEvents();

            if (eventi.isEmpty()) {
                view.mostraMessaggio("Non hai ancora creato nessun evento.");
                view.attesaInvio();
                return;
            }

            view.mostraListaEventi(eventi);
            view.attesaInvio();

        } catch (Exception e) {
            view.mostraErrore("Errore nel caricamento degli eventi: " + e.getMessage());
        }
    }

    private void gestisciCreazioneEvento() {
        try {
            // Esempio: la View chiede i dati base e restituisce un Bean o i singoli campi
            EventBean nuovoEvento = view.chiediDatiNuovoEvento();

            // Il Controller salva l'evento nel DB
            eventController.createEvent(nuovoEvento);

            view.mostraSuccesso("Evento creato con successo!");
            view.attesaInvio();

        } catch (Exception e) {
            view.mostraErrore("Errore durante la creazione dell'evento: " + e.getMessage());
        }
    }
}