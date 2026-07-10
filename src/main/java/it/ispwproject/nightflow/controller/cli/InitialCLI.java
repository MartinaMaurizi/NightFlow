package it.ispwproject.nightflow.controller.cli;

import it.ispwproject.nightflow.pattern.state.AbstractCLIState;
import it.ispwproject.nightflow.pattern.state.CLIStateMachine;
import it.ispwproject.nightflow.view.cli.InitialView;

/**
 * Stato iniziale dell'applicazione NightFlow.
 */
public class InitialCLI extends AbstractCLIState {

    private final InitialView view = new InitialView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraBenvenuto();
    }

    @Override
    public void action(CLIStateMachine context) {
        view.mostraMenu();
        switch (view.chiediScelta()) {
            case "1" -> goNext(context, new LoginCLI());
            case "2" -> goNext(context, new RegistrationCLI());
            case "0" -> context.setState(null);
            default  -> {
                view.mostraErrore("Scelta non valida.");
                goNext(context, this);
            }
        }
    }
}