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
                // 🌟 Ora la scelta 1 punta al cambio password
                case "1" -> editPassword();
                case "0" -> { /* esce dal while e torna indietro */ }
                default  -> view.mostraErrore("Scelta non valida.");
            }
        }

        goBack(context);
    }

    // 🌟 Nuovo metodo integrato con UserController
    private void editPassword() {
        String oldPwd = view.chiediCampo("Vecchia password");
        String newPwd = view.chiediCampo("Nuova password");
        String confirmPwd = view.chiediCampo("Conferma nuova password");

        // Controllo lato View (Boundary)
        if (!newPwd.equals(confirmPwd)) {
            view.mostraErrore("Le nuove password non coincidono. Riprova.");
            return;
        }

        if (!view.chiediConferma("Sei sicuro di voler cambiare la tua password?")) {
            view.mostraMessaggio("Operazione annullata.");
            return;
        }

        // Chiamata al Controller Applicativo
        try {
            userController.updatePassword(oldPwd, newPwd);
            view.mostraSuccesso("Password aggiornata con successo.");
        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }
    }
}