package it.ispwproject.nightflow.view.cli;

public class DashboardOrganizerView {

    public void mostraBenvenuto(String nome) {
        CLIRenderer.intestazioneBenvenuto(nome, "Organizer");
    }

    public void mostraMenu() {
        CLIRenderer.vuota();
        CLIRenderer.voceMenu(1, "Crea nuovo evento");
        CLIRenderer.voceMenu(2, "I miei eventi e modifica"); // Mappa su ViewEventsCLI (che include la modifica)
        CLIRenderer.voceMenu(3, "Lista partecipanti");
        CLIRenderer.voceMenu(4, "Profilo");
        CLIRenderer.voceMenuZero("Logout");
    }

    public String chiediScelta() {
        return CLIRenderer.chiediSceltaStringa("Scelta");
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }
}