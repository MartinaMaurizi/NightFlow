package it.ispwproject.nightflow.view.cli;

import it.ispwproject.nightflow.bean.BookingResponseBean;
import it.ispwproject.nightflow.bean.ClientBean;
import it.ispwproject.nightflow.util.logger.AppLogger;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ManageParticipantsView {

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'alle' HH:mm");

    public void mostraIntestazione() {
        CLIRenderer.intestazione("NightFlow  –  Gestisci Partecipanti");
    }

    public void mostraPartecipanti(List<ClientBean> clients) {
        if (clients.isEmpty()) {
            CLIRenderer.messaggio("Nessun cliente ha ancora acquistato biglietti per i tuoi eventi.");
            return;
        }
        CLIRenderer.sezione("I tuoi clienti");
        for (int i = 0; i < clients.size(); i++) {
            ClientBean c = clients.get(i);
            // 🌟 Usiamo AppLogger
            AppLogger.logInfo(String.format("  [%d] %-24s  %s",
                    i + 1, c.getName() + " " + c.getSurname(), c.getEmail()));
        }
        CLIRenderer.voceMenuZero("Indietro");
    }

    public void mostraSchedaPartecipante(ClientBean client, List<BookingResponseBean> upcomingEvents) {
        CLIRenderer.vuota();
        AppLogger.logInfo(CLIRenderer.LINE_DECO);
        AppLogger.logInfo(String.format("  Cliente: %s %s", client.getName(), client.getSurname()));

        // Prossimo evento in evidenza
        if (!upcomingEvents.isEmpty()) {
            BookingResponseBean next = upcomingEvents.get(0);
            AppLogger.logInfo(String.format("  %s Prossima serata: %s  –  %s @ %s",
                    CLIRenderer.TICKET,
                    next.getEvent().getDateTime().toLocalDate().toString(),
                    next.getEvent().getName(),
                    next.getEvent().getLocalName()));
        }
        AppLogger.logInfo(CLIRenderer.LINE_DECO);
    }

    public void mostraMenuPartecipante() {
        CLIRenderer.sezione("Azioni");
        CLIRenderer.voceMenu(1, "Visualizza storico serate");
        CLIRenderer.voceMenuZero("Torna alla lista");
    }

    public void mostraStoricoEventi(List<BookingResponseBean> completed) {
        CLIRenderer.sezione("Storico serate partecipate");
        if (completed.isEmpty()) {
            CLIRenderer.messaggio("Nessuna serata ancora effettuata.");
            return;
        }
        for (BookingResponseBean b : completed) {
            // 🌟 Usiamo AppLogger
            AppLogger.logInfo(String.format("  %s  %s  %s @ %s",
                    CLIRenderer.PARTY,
                    b.getEvent().getDateTime().toLocalDate().toString(),
                    b.getEvent().getName(),
                    b.getEvent().getLocalName()));
        }
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

    public int chiediScelta(String prompt, int min, int max) {
        return CLIRenderer.chiediScelta(prompt, min, max);
    }

    public String chiediTesto(String prompt) {
        return CLIRenderer.chiediCampo(prompt);
    }

    public boolean chiediConferma(String prompt) {
        return CLIRenderer.chiediConferma(prompt);
    }

    public void attesaInvio() {
        CLIRenderer.chiediCampo("[ INVIO per tornare ]");
    }
}