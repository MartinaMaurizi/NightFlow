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

            // 2. Le 3 opzioni come nell'interfaccia grafica
            List<String> tipiBiglietto = Arrays.asList("Senza drink (prezzo base)", "Con drink", "Tavolo VIP");
            view.mostraTipiBiglietto(tipiBiglietto);

            int sceltaTipo = view.chiediScelta("Scegli il tipo di biglietto (0 per annullare): ", 0, tipiBiglietto.size());
            if (sceltaTipo == 0) {
                goBack(context);
                return;
            }

            // Mappiamo la scelta e calcoliamo il prezzo
            String tipoSelezionato = switch (sceltaTipo) {
                case 1 -> "Senza drink";
                case 2 -> "Con drink";
                case 3 -> "Tavolo VIP";
                default -> throw new IllegalStateException("Scelta non valida");
            };

            double prezzoFinale = calcolaPrezzo(eventoSelezionato.getPrice(), tipoSelezionato);

            // 3. Chiediamo il pagamento e prepariamo la richiesta
            PaymentMethod metodoPagamento = view.chiediMetodoPagamento();
            BookingRequestBean request = new BookingRequestBean();
            request.setEvent(eventoSelezionato);
            request.setTicketType(tipoSelezionato);

            // 4. Usa il tuo metodo mostraRiepilogo (aggiunto il prezzo finale)
            view.mostraRiepilogo(eventoSelezionato, tipoSelezionato, prezzoFinale);

            boolean conferma = view.chiediConferma("Vuoi procedere con l'acquisto?");
            if (conferma) {
                BookingResponseBean risultato = bookingController.createBooking(request, metodoPagamento);
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

    private double calcolaPrezzo(double base, String tipo) {
        return switch (tipo) {
            case "Con drink" -> base + 5.0;
            case "Tavolo VIP" -> base + 85.0;
            default -> base;
        };
    }
}