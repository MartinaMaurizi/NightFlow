package it.ispwproject.nightflow.view.cli;

public class DashboardClientView {

    public void mostraBenvenuto(String nome) {
        CLIRenderer.intestazioneBenvenuto(nome, "Client");
    }

    public void mostraMenu() {
        CLIRenderer.vuota();
        CLIRenderer.voceMenu(1, "Prenota un biglietto");
        CLIRenderer.voceMenu(2, "Le mie prenotazioni");
        CLIRenderer.voceMenu(3, "Annulla una prenotazione");
        CLIRenderer.voceMenu(4, "Cerca evento");
        CLIRenderer.voceMenu(5, "Profilo");
        CLIRenderer.voceMenuZero("Logout");
    }

    public String chiediScelta() {
        return CLIRenderer.chiediSceltaStringa("Scelta");
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }
}