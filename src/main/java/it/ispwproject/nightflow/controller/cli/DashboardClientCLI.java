package it.ispwproject.nightflow.controller.cli;

import it.ispwproject.nightflow.pattern.state.AbstractCLIState;
import it.ispwproject.nightflow.pattern.state.CLIStateMachine;

import it.ispwproject.nightflow.dao.ConnectionFactory;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.view.cli.DashboardClientView;

public class DashboardClientCLI extends AbstractCLIState {

    private final DashboardClientView view = new DashboardClientView();

    @Override
    public void entry(CLIStateMachine context) {
        String nome = SessionManager.getInstance().getLoggedUser().getName();
        view.mostraBenvenuto(nome);
    }

    @Override
    public void action(CLIStateMachine context) {
        view.mostraMenu();
        switch (view.chiediScelta()) {
            case "1" -> goNext(context, new BookTicketCLI());      // Sostituisce BookLesson
            case "2" -> goNext(context, new ViewBookingsCLI());    // Storico e gestione biglietti
            case "3" -> goNext(context, new EditProfileCLI());     // Sostituisce la vecchia EditProfile
            case "0" -> {
                try {
                    ConnectionFactory.clearRole();
                    SessionManager.getInstance().clearSession();
                    view.mostraMessaggio("✓ Logout effettuato.");
                    goNext(context, new InitialCLI());
                } catch (java.sql.SQLException ex) {
                    view.mostraMessaggio("❌ Errore: impossibile effettuare il logout in sicurezza. Riprova.");
                    goNext(context, this);
                }
            }
            default -> {
                view.mostraMessaggio("❌ Scelta non valida.");
                goNext(context, this);
            }
        }
    }
}