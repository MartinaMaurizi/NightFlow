package it.ispwproject.nightflow.controller.cli;

import it.ispwproject.nightflow.pattern.singleton.SessionManager;
import it.ispwproject.nightflow.pattern.state.AbstractCLIState;
import it.ispwproject.nightflow.pattern.state.CLIStateMachine;

import it.ispwproject.nightflow.controller.applicativo.UserController;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.view.cli.EditProfileView;

public class EditProfileCLI extends AbstractCLIState {

    private final UserController userController = new UserController();
    private final EditProfileView view = new EditProfileView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
        var user = SessionManager.getInstance().getLoggedUser();
        view.mostraDatiAttuali(user.getName(), user.getSurname(), user.getEmail(), user.getCity());
    }

    @Override
    public void action(CLIStateMachine context) {
        String scelta = "";

        while (!scelta.equals("0")) {
            view.mostraMenu();
            scelta = view.chiediScelta();

            switch (scelta) {
                case "1" -> editEmail();
                case "2" -> editCity(); // Nuova funzionalità aggiunta!
                case "0" -> { /* esce dal while e torna indietro */ }
                default  -> view.mostraErrore("Scelta non valida.");
            }
        }

        goBack(context);
    }

    private void editEmail() {
        String newEmail = view.chiediCampo("Nuova email");
        if (!view.chiediConferma("Confermare il cambio email a " + newEmail + "?")) {
            view.mostraMessaggio("Operazione annullata.");
            return;
        }
        try {
            userController.updateEmail(newEmail);
            view.mostraSuccesso("Email aggiornata con successo.");
        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }
    }

    private void editCity() {
        String newCity = view.chiediCampo("Nuova città");
        if (!view.chiediConferma("Confermare il cambio città in " + newCity + "?")) {
            view.mostraMessaggio("Operazione annullata.");
            return;
        }
        try {
            userController.updateCity(newCity);
            view.mostraSuccesso("Città aggiornata con successo.");
        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }
    }
}