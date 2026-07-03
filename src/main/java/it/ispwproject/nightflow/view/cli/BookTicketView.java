package it.ispwproject.nightflow.view.cli;

import it.ispwproject.nightflow.bean.*;

import java.util.List;

public class BookTicketView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("NightFlow  –  Prenota un Biglietto");
    }

    public void mostraEventi(List<EventBean> events) {
        CLIRenderer.sezione("Eventi disponibili");

        if (events.isEmpty()) {
            CLIRenderer.messaggio("Nessun evento disponibile al momento.");
        } else {
            for (int i = 0; i < events.size(); i++) {
                EventBean e = events.get(i);
                // Es: [1] Fluo Party           @ Jolie Club      (2026-10-31) – 15.00€
                System.out.printf("  [%d] %-20s @ %-15s (%s) – %.2f€%n",
                        i + 1,
                        e.getName(),
                        e.getLocalName(),
                        e.getDateTime().toLocalDate().toString(),
                        e.getPrice());
            }
        }
        CLIRenderer.voceMenuZero("Indietro");
    }

    public void mostraTipiBiglietto(List<String> ticketTypes) {
        CLIRenderer.sezione("Seleziona il tipo di biglietto");
        for (int i = 0; i < ticketTypes.size(); i++) {
            CLIRenderer.voceMenu(i + 1, ticketTypes.get(i));
        }
        CLIRenderer.voceMenuZero("Indietro");
    }

    public void mostraRiepilogo(EventBean event, String ticketType) {
        CLIRenderer.sezione("Riepilogo prenotazione");
        CLIRenderer.campo("Evento",    event.getName());
        CLIRenderer.campo("Locale",    event.getLocalName());
        CLIRenderer.campo("Data",      event.getDateTime().toLocalDate().toString());
        CLIRenderer.campo("Orario",    event.getDateTime().toLocalTime().toString());
        CLIRenderer.campo("Biglietto", ticketType);
    }

    public void mostraConferma(BookingResponseBean response) {
        CLIRenderer.sezione("Prenotazione confermata!");
        CLIRenderer.campo("Stato",   response.getStatus() != null ? response.getStatus().toString() : "—");
        CLIRenderer.campo("Codice",  response.getTicketCode());
        CLIRenderer.campo("Evento",  response.getEvent().getName());
        CLIRenderer.separatore();
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }

    public int chiediScelta(String prompt, int min, int max) {
        return CLIRenderer.chiediScelta(prompt, min, max);
    }

    public boolean chiediConferma(String prompt) {
        return CLIRenderer.chiediConferma(prompt);
    }
    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }

    public void attesaInvio() {
        CLIRenderer.chiediCampo("[ INVIO per continuare ]");
    }

    public it.ispwproject.nightflow.enumerator.PaymentMethod chiediMetodoPagamento() {
        CLIRenderer.sezione("Metodo di Pagamento");
        CLIRenderer.voceMenu(1, "Carta di Credito");
        CLIRenderer.voceMenu(2, "PayPal");

        int scelta = CLIRenderer.chiediScelta("Scegli come pagare: ", 1, 2);

        if (scelta == 1) {
            return it.ispwproject.nightflow.enumerator.PaymentMethod.CREDIT_CARD;
        } else {
            return it.ispwproject.nightflow.enumerator.PaymentMethod.PAYPAL;
        }
    }
}