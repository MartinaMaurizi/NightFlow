package it.ispwproject.nightflow.controller.cli;

import it.ispwproject.nightflow.bean.BookingResponseBean;
import it.ispwproject.nightflow.controller.applicativo.BookingController;
import it.ispwproject.nightflow.enumerator.BookingStatus;
import it.ispwproject.nightflow.enumerator.PaymentMethod;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.pattern.state.AbstractCLIState;
import it.ispwproject.nightflow.pattern.state.CLIStateMachine;
import it.ispwproject.nightflow.view.cli.CancelBookingView;

import java.time.LocalDateTime;
import java.time.Clock;
import java.util.List;

public class CancelBookingCLI extends AbstractCLIState {

    private final BookingController bookingController = new BookingController();
    private final CancelBookingView view = new CancelBookingView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        int clientId = SessionManager.getInstance().getLoggedUser().getId();

        try {
            // 🌟 FILTRO RIGIDO: Mostriamo SOLO le prenotazioni da pagare all'ingresso
            List<BookingResponseBean> manageables = bookingController
                    .getAllClientBookings(clientId)
                    .stream()
                    .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                    .filter(b -> b.getEvent().getDateTime().isAfter(LocalDateTime.now(Clock.systemDefaultZone())))
                    .filter(b -> b.getPaymentMethod() == PaymentMethod.PAY_ON_SITE || b.getPaymentMethod() == null)
                    .toList();

            if (manageables.isEmpty()) {
                view.mostraMessaggio("Nessuna prenotazione modificabile trovata.");
                view.mostraMessaggio("(Le prenotazioni già pagate online con Carta o PayPal non possono essere annullate).");
                view.attesaInvio();
                goBack(context);
                return;
            }

            view.mostraPrenotazioniAnnullabili(manageables);
            int choice = view.chiediScelta("Seleziona la prenotazione da gestire (0 per tornare)", 0, manageables.size());
            if (choice == 0) {
                goBack(context);
                return;
            }

            BookingResponseBean selected = manageables.get(choice - 1);
            view.mostraRiepilogo(selected);

            // 🌟 BIVIO LOGICO: L'utente può Annullare o Pagare
            int azione = view.chiediAzioneGestione();

            if (azione == 0) {
                goBack(context);
                return;
            } else if (azione == 1) {
                // ANNULLAMENTO
                if (!view.chiediConferma("Sei sicuro di voler annullare questa prenotazione?")) {
                    view.mostraMessaggio("Operazione annullata.");
                    goBack(context);
                    return;
                }
                bookingController.cancelBooking(selected.getId(), clientId);
                view.mostraSuccesso("Prenotazione annullata con successo.");

            } else if (azione == 2) {
                // MODIFICA PAGAMENTO (Da Ingresso a Online)
                PaymentMethod pm = view.chiediMetodoPagamentoOnline();
                if (pm != null) {
                    bookingController.updatePaymentMethod(selected.getEvent().getId(), pm);
                    view.mostraSuccesso("Pagamento online completato! La prenotazione è ora bloccata e non modificabile.");
                }
            }

        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }

        view.attesaInvio();
        goBack(context);
    }
}