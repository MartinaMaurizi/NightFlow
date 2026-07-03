package it.ispwproject.nightflow.controller.cli;

import it.ispwproject.nightflow.pattern.state.AbstractCLIState;
import it.ispwproject.nightflow.pattern.state.CLIStateMachine;

import it.ispwproject.nightflow.controller.applicativo.LoginController;
import it.ispwproject.nightflow.controller.applicativo.LoginController.LoginResult;
import it.ispwproject.nightflow.exception.LoginException;
import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.view.cli.LoginView;

public class LoginCLI extends AbstractCLIState {

    private final LoginController loginController = new LoginController();
    private final LoginView view = new LoginView();

    @Override
    public void action(CLIStateMachine context) {
        String[] credenziali = view.chiediCredenziali();
        String email    = credenziali[0];
        String password = credenziali[1];

        if (email.isEmpty() || password.isEmpty()) {
            view.mostraErroreInput();
            // Rimane nello stesso stato in caso di errore di input
            goNext(context, this);
            return;
        }

        try {
            LoginResult result = loginController.login(email, password);

            // Assicurati che la tua classe User abbia il metodo getNome() o getName()
            String nome = SessionManager.getInstance().getLoggedUser().getName();
            view.mostraSuccesso(nome);

            // Transizione verso le rispettive Dashboard del tuo progetto NightFlow
            switch (result) {
                case SUCCESSO_CLIENT      -> goNext(context, new DashboardClientCLI());
                case SUCCESSO_ORGANIZER -> goNext(context, new DashboardOrganizerCLI());
            }
        } catch (LoginException e) {
            view.mostraErrore(e.getMessage());
            // Rimane nello stato di Login se le credenziali sono errate
            goNext(context, this);
        }
    }
}