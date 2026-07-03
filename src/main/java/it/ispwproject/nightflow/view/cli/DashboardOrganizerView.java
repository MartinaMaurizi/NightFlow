package it.ispwproject.nightflow.view.cli;

public class DashboardOrganizerView {

    public void mostraBenvenuto(String nome) {
        CLIRenderer.intestazioneBenvenuto(nome, "Organizer");
    }

    public void mostraMenu() {
        CLIRenderer.vuota();
        CLIRenderer.voceMenu(1, "Crea nuovo evento");
        CLIRenderer.voceMenu(2, "Modifica un evento");
        CLIRenderer.voceMenu(3, "Le mie serate");
        CLIRenderer.voceMenu(4, "Lista partecipanti");
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