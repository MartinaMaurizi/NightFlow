package it.ispwproject.nightflow.view.cli;

import it.ispwproject.nightflow.enumerator.Role;

public class RegistrationView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("NightFlow  –  Registrazione Nuovo Utente");
    }

    public String chiediCampo(String label) {
        return CLIRenderer.chiediCampo(label);
    }

    public String chiediPassword(String label) {
        return CLIRenderer.chiediCampo(label); // In CLI il testo rimane visibile
    }

    public Role chiediRuolo() {
        while (true) {
            CLIRenderer.sezione("Tipo Account");
            CLIRenderer.voceMenu(1, "Cliente (Prenota serate)");
            CLIRenderer.voceMenu(2, "Organizzatore (Gestisci eventi)");
            String input = CLIRenderer.chiediSceltaStringa("Scelta [1-2]");
            if (input.equals("1")) return Role.CLIENT;
            if (input.equals("2")) return Role.ORGANIZER;
            CLIRenderer.errore("Scelta non valida.");
        }
    }

    // Metodo per raccogliere i dati anagrafici (usato dal Controller)
    public void mostraIstruzioniAnagrafiche() {
        CLIRenderer.sezione("Informazioni Personali");
        CLIRenderer.messaggio("Inserisci i dati richiesti per completare il profilo.");
    }

    public void mostraSuccesso() {
        CLIRenderer.vuota();
        CLIRenderer.successo("Registrazione completata! Benvenuto in NightFlow.");
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }
}