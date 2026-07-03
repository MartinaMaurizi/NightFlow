package it.ispwproject.nightflow.view.cli;

public class EditProfileView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("NightFlow  –  Profilo");
    }

    public void mostraMenu() {
        CLIRenderer.vuota();
        CLIRenderer.voceMenu(1, "Modifica email");
        CLIRenderer.voceMenu(2, "Modifica città");
        CLIRenderer.voceMenuZero("Indietro");
    }

    // Aggiunti i campi anagrafici specifici di NightFlow
    public void mostraDatiAttuali(String nome, String cognome, String email,  String citta) {
        CLIRenderer.sezione("Profilo attuale");
        CLIRenderer.campo("Nome",    nome);
        CLIRenderer.campo("Cognome", cognome);
        CLIRenderer.campo("Email",   email);
        CLIRenderer.campo("Città",   citta);
    }

    public String chiediScelta() {
        return CLIRenderer.chiediSceltaStringa("Scelta");
    }

    public String chiediCampo(String label) {
        return CLIRenderer.chiediCampo(label);
    }

    public void mostraSuccesso(String messaggio) {
        CLIRenderer.successo(messaggio);
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }

    public boolean chiediConferma(String prompt) {
        return CLIRenderer.chiediConferma(prompt);
    }
}