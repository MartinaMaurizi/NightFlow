package it.ispwproject.nightflow.controller.cli;

import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.controller.applicativo.EventController;
import it.ispwproject.nightflow.exception.AvailabilityException;
import it.ispwproject.nightflow.pattern.state.AbstractCLIState;
import it.ispwproject.nightflow.pattern.state.CLIStateMachine;
import it.ispwproject.nightflow.view.cli.CreateEventView;

import java.time.LocalDateTime;

public class CreateEventCLI extends AbstractCLIState {

    private final CreateEventView view = new CreateEventView();
    private final EventController eventController = new EventController();

    @Override
    public void action(CLIStateMachine context) {
        view.mostraIntestazione();

        try {
            // 1. Inizializziamo il Bean
            EventBean eventBean = new EventBean();

            // 2. Raccogliamo i dati passo passo tramite la View
            String name = view.chiediStringa("Inserisci il nome dell'evento (0 per annullare): ");
            if (name.equals("0")) {
                goBack(context);
                return;
            }
            eventBean.setName(name);

            eventBean.setDescription(view.chiediStringa("Inserisci una descrizione: "));
            eventBean.setLocalName(view.chiediStringa("Nome del locale (es. Jolie Club): "));
            eventBean.setLocation(view.chiediStringa("Indirizzo del locale: "));

            // Supponiamo che la tua View abbia un metodo per gestire l'input di date
            LocalDateTime dateTime = view.chiediDataOra("Data e ora dell'evento (Formato consigliato: yyyy-MM-dd HH:mm): ");
            eventBean.setDateTime(dateTime);

            eventBean.setTotalCapacity(view.chiediIntero("Capacità totale del locale (numero massimo biglietti): "));
            // All'inizio, i biglietti disponibili sono uguali alla capacità totale
            eventBean.setAvailableTickets(eventBean.getTotalCapacity());

            eventBean.setPrice(view.chiediDecimale("Prezzo base del biglietto (usa la virgola per i decimali): "));

            // 3. Mostriamo il riepilogo e chiediamo conferma
            view.mostraRiepilogo(eventBean);
            boolean conferma = view.chiediConferma("Vuoi confermare la creazione di questo evento?");

            if (conferma) {
                // 4. Invochiamo il Controller Applicativo
                eventController.createEvent(eventBean);

                // Se non lancia eccezioni, l'evento è salvato!
                view.mostraSuccesso("Evento creato con successo! Il nuovo evento è ora disponibile a sistema.");
            } else {
                view.mostraMessaggio("Creazione evento annullata dall'utente.");
            }

            view.attesaInvio();
            goBack(context);

        } catch (AvailabilityException e) {
            // Intercettiamo l'eccezione specifica per la sovrapposizione del locale
            view.mostraErrore("Locale non disponibile: " + e.getMessage());
            view.attesaInvio();
            goBack(context);

        } catch (Exception e) {
            // Gestione di altri errori (es. DAOException o formati di input errati)
            view.mostraErrore("Errore di sistema: " + e.getMessage());
            view.attesaInvio();
            goBack(context);
        }
    }
}