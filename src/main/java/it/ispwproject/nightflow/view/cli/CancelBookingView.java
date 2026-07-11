package it.ispwproject.nightflow.view.cli;

import it.ispwproject.nightflow.bean.BookingResponseBean;
import it.ispwproject.nightflow.enumerator.PaymentMethod;

import java.util.List;

public class CancelBookingView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("Gestisci Prenotazioni in Sospeso");
    }

    public void mostraPrenotazioniAnnullabili(List<BookingResponseBean> cancellable) {
        CLIRenderer.sezione("Prenotazioni con pagamento all'ingresso");

        int eventW = cancellable.stream().mapToInt(b -> b.getEvent().getName().length()).max().orElse(10);
        int localW = cancellable.stream().mapToInt(b -> b.getEvent().getLocalName().length()).max().orElse(12);
        int numW = String.valueOf(cancellable.size()).length();

        String fmt = "  [%-" + numW + "d] %-" + eventW + "s  @ %-" + localW + "s  %s  [%s]";

        for (int i = 0; i < cancellable.size(); i++) {
            BookingResponseBean b = cancellable.get(i);
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
        CLIRenderer.sezione("Dettaglio Prenotazione");
        CLIRenderer.campo("Evento",   selected.getEvent().getName());
        CLIRenderer.campo("Locale",   selected.getEvent().getLocalName());
        CLIRenderer.campo("Data/Ora", selected.getEvent().getDateTime().toLocalDate().toString()
                + " " + selected.getEvent().getDateTime().toLocalTime().toString());
        CLIRenderer.campo("Codice",   selected.getTicketCode());
    }

    // 🌟 I NUOVI MENU PER LA MODIFICA
    public int chiediAzioneGestione() {
        CLIRenderer.sezione("Cosa vuoi fare con questa prenotazione?");
        CLIRenderer.voceMenu(1, "Annulla prenotazione");
        CLIRenderer.voceMenu(2, "Paga ora online (Carta / PayPal)");
        CLIRenderer.voceMenuZero("Indietro");
        return CLIRenderer.chiediScelta("Scelta", 0, 2);
    }

    public PaymentMethod chiediMetodoPagamentoOnline() {
        CLIRenderer.sezione("Seleziona Metodo di Pagamento");
        CLIRenderer.voceMenu(1, "Carta di Credito o Debito");
        CLIRenderer.voceMenu(2, "PayPal");
        CLIRenderer.voceMenuZero("Annulla pagamento");

        int scelta = CLIRenderer.chiediScelta("Scegli come pagare", 0, 2);
        if (scelta == 1) return PaymentMethod.CREDIT_CARD;
        if (scelta == 2) return PaymentMethod.PAYPAL;
        return null;
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

    public void mostraSuccesso(String messaggio) {
        CLIRenderer.successo(messaggio);
    }

    public void mostraErrore(String errore) {
        CLIRenderer.errore(errore);
    }

    public void attesaInvio() {
        CLIRenderer.chiediCampo("[ INVIO per continuare ]");
    }
}