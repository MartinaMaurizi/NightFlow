package it.ispwproject.nightflow.controller.cli;

import it.ispwproject.nightflow.bean.BookingRequestBean;
import it.ispwproject.nightflow.bean.BookingResponseBean;
import it.ispwproject.nightflow.bean.EventBean;
import it.ispwproject.nightflow.controller.applicativo.BookingController;
import it.ispwproject.nightflow.enumerator.PaymentMethod;
import it.ispwproject.nightflow.pattern.state.AbstractCLIState;
import it.ispwproject.nightflow.pattern.state.CLIStateMachine;
import it.ispwproject.nightflow.view.cli.BookTicketView;

import java.util.List;
import java.util.Arrays;

public class BookTicketCLI extends AbstractCLIState {

    private final BookTicketView view = new BookTicketView();
    private final BookingController bookingController = new BookingController();

    @Override
    public void action(CLIStateMachine context) {
        view.mostraIntestazione();

        try {
            // 1. Usa il tuo metodo mostraEventi
            List<EventBean> eventiDisponibili = bookingController.getAvailableEvents();
            view.mostraEventi(eventiDisponibili);

            if (eventiDisponibili.isEmpty()) {
                view.attesaInvio();
                goBack(context);
                return;
            }

            int sceltaEvento = view.chiediScelta("Seleziona l'evento da prenotare (0 per annullare): ", 0, eventiDisponibili.size());
            if (sceltaEvento == 0) {
                goBack(context);
                return;
            }

            EventBean eventoSelezionato = eventiDisponibili.get(sceltaEvento - 1);

            // 2. Usa il tuo metodo mostraTipiBiglietto
            List<String> tipiBiglietto = Arrays.asList("Standard", "VIP (Salta Fila + Consumazione)");
            view.mostraTipiBiglietto(tipiBiglietto);

            int sceltaTipo = view.chiediScelta("Scegli il tipo di biglietto (0 per annullare): ", 0, tipiBiglietto.size());
            if (sceltaTipo == 0) {
                goBack(context);
                return;
            }
            String tipoSelezionato = (sceltaTipo == 1) ? "Standard" : "VIP";

            // 3. Chiediamo il pagamento e prepariamo la richiesta
            PaymentMethod metodoPagamento = view.chiediMetodoPagamento();
            BookingRequestBean request = new BookingRequestBean();
            request.setEvent(eventoSelezionato);
            request.setTicketType(tipoSelezionato);
            // Non impostiamo il ClientBean perché ci penserà il Controller!

            // 4. Usa il tuo metodo mostraRiepilogo
            view.mostraRiepilogo(eventoSelezionato, tipoSelezionato);

            boolean conferma = view.chiediConferma("Vuoi procedere con l'acquisto?");
            if (conferma) {
                BookingResponseBean risultato = bookingController.createBooking(request, metodoPagamento);

                // 5. Usa il tuo metodo mostraConferma
                view.mostraConferma(risultato);
            } else {
                view.mostraMessaggio("Prenotazione annullata.");
            }

            view.attesaInvio();
            goBack(context);

        } catch (Exception e) {
            view.mostraErrore("Errore: " + e.getMessage());
            view.attesaInvio();
            goBack(context);
        }
    }
}