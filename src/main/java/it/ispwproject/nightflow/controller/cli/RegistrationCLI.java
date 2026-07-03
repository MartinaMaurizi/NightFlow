package it.ispwproject.nightflow.controller.cli;

import it.ispwproject.nightflow.pattern.state.AbstractCLIState;
import it.ispwproject.nightflow.pattern.state.CLIStateMachine;
import it.ispwproject.nightflow.bean.RegistrationBean;
import it.ispwproject.nightflow.controller.applicativo.RegistrationController;
import it.ispwproject.nightflow.enumerator.Role;
import it.ispwproject.nightflow.exception.DAOException;
import it.ispwproject.nightflow.exception.RegistrationException;
import it.ispwproject.nightflow.view.cli.RegistrationView;

import java.time.LocalDate;

public class RegistrationCLI extends AbstractCLIState {

    private final RegistrationController registrationController = new RegistrationController();
    private final RegistrationView view = new RegistrationView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        try {
            RegistrationBean bean = new RegistrationBean();

            // Dati anagrafici base
            bean.setName(view.chiediCampo("Nome"));
            bean.setSurname(view.chiediCampo("Cognome"));
            bean.setEmail(view.chiediCampo("Email"));
            bean.setPassword(view.chiediPassword("Password"));
            bean.setConfirmPassword(view.chiediPassword("Conferma password"));

            // Dati specifici NightFlow
            bean.setGender(view.chiediCampo("Genere"));
            bean.setCountry(view.chiediCampo("Nazione"));
            bean.setCity(view.chiediCampo("Città"));

            // Parsing data di nascita (es. formato YYYY-MM-DD)
            String dataStr = view.chiediCampo("Data di nascita (AAAA-MM-DD)");
            bean.setDateOfBirth(LocalDate.parse(dataStr));

            Role role = view.chiediRuolo();
            bean.setRole(role);

            // Logica specifica per l'Organizzatore
            if (role == Role.ORGANIZER) {
                // In NightFlow, l'organizzatore gestisce i locali
                String localName = view.chiediCampo("Nome del locale gestito");
                bean.setLocalNames(java.util.List.of(localName));
            }

            registrationController.register(bean);
            view.mostraSuccesso();
            goNext(context, new LoginCLI());

        } catch (RegistrationException e) {
            view.mostraErrore(e.getMessage());
            goNext(context, this);
        } catch (Exception e) { // Catch generico per errori di parsing o DB
            view.mostraErrore("Errore: " + e.getMessage());
            goNext(context, new InitialCLI());
        }
    }
}