package it.ispwproject.nightflow.view.cli;

import it.ispwproject.nightflow.bean.EventBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ViewEventsView {

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void mostraIntestazione() {
        CLIRenderer.intestazione("I Miei Eventi");
    }

    public void mostraListaEventi(List<EventBean> eventi) {
        if (eventi.isEmpty()) {
            CLIRenderer.vuota();
            CLIRenderer.messaggio("Nessun evento in programma.");
            return;
        }

        CLIRenderer.sezione("Eventi in programma");

        for (int i = 0; i < eventi.size(); i++) {
            EventBean e = eventi.get(i);
            String dataFormattata = e.getDateTime() != null ? e.getDateTime().format(DT_FMT) : "Data n.d.";

            // Usiamo il System.out.printf per un incolonnamento pulito nella CLI
            System.out.printf("  [%d] %-24s | %s | %s%n",
                    i + 1, e.getName(), dataFormattata, e.getLocalName());
            System.out.printf("      Prezzo: %.2f€  |  Biglietti Rimasti: %d%n",
                    e.getPrice(), e.getAvailableTickets());
            CLIRenderer.separatore();
        }
    }

    public int chiediSelezioneEvento(int max) {
        CLIRenderer.vuota();
        return CLIRenderer.chiediScelta("Inserisci il numero dell'evento da gestire (0 per tornare indietro)", 0, max);
    }

    public int mostraMenuAzione(EventBean evento) {
        CLIRenderer.vuota();
        CLIRenderer.sezione("Gestione Evento: " + evento.getName());
        CLIRenderer.voceMenu(1, "Modifica Data e Ora");
        CLIRenderer.voceMenu(2, "Annulla (Elimina) Evento");
        CLIRenderer.voceMenuZero("Torna alla lista");
        return CLIRenderer.chiediScelta("Scelta", 0, 2);
    }

    public LocalDateTime chiediNuovaDataOra() {
        LocalDateTime dataOra = null;
        while (dataOra == null) {
            String dataStr = CLIRenderer.chiediCampo("Nuova Data e Ora (formato GG/MM/AAAA HH:MM): ");
            try {
                dataOra = LocalDateTime.parse(dataStr, DT_FMT);
                if (dataOra.isBefore(LocalDateTime.now())) {
                    CLIRenderer.errore("La data non può essere nel passato.");
                    dataOra = null;
                }
            } catch (DateTimeParseException e) {
                CLIRenderer.errore("Formato data non valido! (es. 22/11/2026 22:30).");
            }
        }
        return dataOra;
    }

    public boolean chiediConfermaEliminazione(String nomeEvento) {
        return CLIRenderer.chiediConferma("Sei sicuro di voler annullare definitivamente l'evento '" + nomeEvento + "'?");
    }

    public void mostraSuccesso(String messaggio) {
        CLIRenderer.successo(messaggio);
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }

    public void attesaInvio() {
        CLIRenderer.vuota();
        CLIRenderer.chiediSceltaStringa("Premi INVIO per continuare");
    }
}