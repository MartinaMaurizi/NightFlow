package it.ispwproject.nightflow.view.cli;

import it.ispwproject.nightflow.bean.BookingResponseBean;
import java.util.List;

public class CancelBookingView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("Annulla una prenotazione");
    }

    public void mostraPrenotazioniAnnullabili(List<BookingResponseBean> cancellable) {
        CLIRenderer.sezione("Biglietti attivi");

        if (cancellable.isEmpty()) {
            CLIRenderer.messaggio("Non hai nessuna prenotazione attiva da poter annullare.");
            return;
        }

        int eventW = cancellable.stream().mapToInt(b -> b.getEvent().getName().length()).max().orElse(10);
        int localW = cancellable.stream().mapToInt(b -> b.getEvent().getLocalName().length()).max().orElse(12);
        int numW = String.valueOf(cancellable.size()).length();

        String fmt = "  [%-" + numW + "d] %-" + eventW + "s  @ %-" + localW + "s  %s  [%s]";

        for (int i = 0; i < cancellable.size(); i++) {
            BookingResponseBean b = cancellable.get(i);

            // 🌟 Addio AppLogger (niente più scritte rosse!), stampiamo normalmente
            System.out.println(String.format(fmt,
                    i + 1,
                    b.getEvent().getName(),
                    b.getEvent().getLocalName(),
                    b.getEvent().getDateTime().toLocalDate().toString(),
                    b.getTicketCode()));
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

    public int chiediScelta(String prompt, int min, int max) {
        return CLIRenderer.chiediScelta(prompt, min, max);
    }

    public boolean chiediConferma(String prompt) {
        return CLIRenderer.chiediConferma(prompt);
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }

    public void mostraSuccesso() {
        CLIRenderer.successo("Prenotazione annullata con successo.");
    }

    public void mostraErrore(String errore) {
        CLIRenderer.errore(errore);
    }
}