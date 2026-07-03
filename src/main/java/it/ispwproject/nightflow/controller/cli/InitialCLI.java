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
        boolean inputValido = false;

        while (!inputValido) {
            view.mostraMenu();
            String scelta = view.chiediScelta();

            switch (scelta) {
                case "1" -> {
                    // Transizione verso Login
                    goNext(context, new LoginCLI());
                    inputValido = true;
                }
                case "2" -> {
                    // Transizione verso Registrazione
                    goNext(context, new RegistrationCLI());
                    inputValido = true;
                }
                case "0" -> {
                    // Uscita dall'applicazione
                    context.setState(null);
                    inputValido = true;
                }
            }
        }
    }
}