package it.ispwproject.nightflow.view.cli;

public class LoginView {

    public String[] chiediCredenziali() {
        CLIRenderer.intestazione("NightFlow  –  Accedi");
        CLIRenderer.vuota();
        String email    = CLIRenderer.chiediCampo("Email");
        String password = CLIRenderer.chiediCampo("Password");
        return new String[]{email, password};
    }

    public void mostraErroreInput() {
        CLIRenderer.errore("Inserisci sia email che password.");
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }

    public void mostraSuccesso(String nome) {
        CLIRenderer.messaggio(CLIRenderer.OK + " Benvenuto, " + nome + "!");
    }
}