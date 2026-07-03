package it.ispwproject.nightflow.view.cli;

import it.ispwproject.nightflow.bean.BookingResponseBean;

import java.util.List;

public class CancelBookingView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("NightFlow  –  Annulla una prenotazione");
    }

    public void mostraPrenotazioniAnnullabili(List<BookingResponseBean> cancellable) {
        CLIRenderer.sezione("Biglietti attivi");

        if (cancellable.isEmpty()) {
            CLIRenderer.messaggio("Non hai nessuna prenotazione attiva da poter annullare.");
            return;
        }

        // larghezza colonne calcolata sui dati reali dell'evento
        int eventW = cancellable.stream()
                .mapToInt(b -> b.getEvent().getName().length())
                .max().orElse(10);
        int localW = cancellable.stream()
                .mapToInt(b -> b.getEvent().getLocalName().length())
                .max().orElse(12);
        int numW = String.valueOf(cancellable.size()).length();

        // Formato: [1] Fluo Party @ Jolie Club 2026-10-31 [NF-TKT-123]
        String fmt = "  [%-" + numW + "d] %-" + eventW + "s  @ %-" + localW + "s  %s  [%s]%n";

        for (int i = 0; i < cancellable.size(); i++) {
            BookingResponseBean b = cancellable.get(i);
            System.out.printf(fmt,
                    i + 1,
                    b.getEvent().getName(),
                    b.getEvent().getLocalName(),
                    b.getEvent().getDateTime().toLocalDate().toString(),
                    b.getTicketCode());
        }
        CLIRenderer.voceMenuZero("Indietro");
    }

    public void mostraRiepilogo(BookingResponseBean selected) {
        CLIRenderer.sezione("Conferma annullamento");
        CLIRenderer.campo("Evento",   selected.getEvent().getName());
        CLIRenderer.campo("Locale",   selected.getEvent().getLocalName());
        CLIRenderer.campo("Data/Ora", selected.getEvent().getDateTime().toLocalDate().toString()
                + " " + selected.getEvent().getDateTime().toLocalTime().toString());
        CLIRenderer.campo("Codice",   selected.getTicketCode());
    }

    public void mostraSuccesso() {
        CLIRenderer.successo("Prenotazione annullata con successo. Il biglietto non è più valido.");
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }

    public int chiediScelta(String prompt, int min, int max) {
        return CLIRenderer.chiediScelta(prompt, min, max);
    }

    public boolean chiediConferma(String prompt) {
        return CLIRenderer.chiediConferma(prompt);
    }
}