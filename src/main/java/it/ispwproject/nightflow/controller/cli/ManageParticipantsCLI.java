package it.ispwproject.nightflow.controller.cli;

import it.ispwproject.nightflow.bean.ClientBean;
import it.ispwproject.nightflow.bean.BookingResponseBean;
import it.ispwproject.nightflow.view.cli.ManageParticipantsView;
import it.ispwproject.nightflow.controller.applicativo.BookingController;
import it.ispwproject.nightflow.pattern.state.AbstractCLIState;
import it.ispwproject.nightflow.pattern.state.CLIStateMachine;
import java.util.List;

// 1. ESTENDIAMO LA CLASSE ASTRATTA
public class ManageParticipantsCLI extends AbstractCLIState {

    private final ManageParticipantsView view = new ManageParticipantsView();
    private final BookingController bookingController = new BookingController();

    // 2. SOSTITUIAMO start() CON action() E METTIAMO @Override
    @Override
    public void action(CLIStateMachine context) {
        view.mostraIntestazione();

        try {
            List<ClientBean> partecipanti = bookingController.getAllParticipants();

            view.mostraPartecipanti(partecipanti);

            if (partecipanti.isEmpty()) {
                view.attesaInvio();
                goBack(context); // 3. Usiamo la State Machine per tornare indietro
                return;
            }

            int scelta = view.chiediScelta("Seleziona un cliente (0 per tornare):", 0, partecipanti.size());

            if (scelta == 0) {
                goBack(context); // 3. Usiamo la State Machine per tornare indietro
                return;
            }

            gestisciDettaglioCliente(partecipanti.get(scelta - 1));

            // Dopo aver visto il dettaglio, torniamo alla schermata precedente
            goBack(context);

        } catch (Exception e) {
            view.mostraErrore("Errore nel recupero dati: " + e.getMessage());
            goBack(context);
        }
    }

    private void gestisciDettaglioCliente(ClientBean cliente) {
        try {
            List<BookingResponseBean> prenotazioni = bookingController.getClientBookings(cliente.getId());

            view.mostraSchedaPartecipante(cliente, prenotazioni);
            view.mostraMenuPartecipante();

            int scelta = view.chiediScelta("Scelta:", 0, 1);
            if (scelta == 1) {
                view.mostraStoricoEventi(prenotazioni);
                view.attesaInvio();
            }
        } catch (Exception e) {
            view.mostraErrore("Errore nel dettaglio cliente: " + e.getMessage());
        }
    }
}