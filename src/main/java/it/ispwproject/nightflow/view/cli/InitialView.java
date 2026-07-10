package it.ispwproject.nightflow.view.cli;

import it.ispwproject.nightflow.util.logger.AppLogger;

public class InitialView {
    public void mostraBenvenuto() {
        CLIRenderer.vuota();
        System.out.println(CLIRenderer.LINE_DECO);
        System.out.println(CLIRenderer.centra("N I G H T F L O W"));
        System.out.println(CLIRenderer.LINE_DECO);
    }

    public void mostraMenu() {
        CLIRenderer.vuota();
        CLIRenderer.voceMenu(1, "Accedi");
        CLIRenderer.voceMenu(2, "Registrati");
        CLIRenderer.voceMenuZero("Esci");
    }

    public String chiediScelta() {
        return CLIRenderer.chiediSceltaStringa("Scelta");
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }

    public void mostraArrivederci() {
        CLIRenderer.vuota();
        AppLogger.logInfo(CLIRenderer.LINE_DECO);
        AppLogger.logInfo(CLIRenderer.centra("Arrivederci!  –  NightFlow"));
        AppLogger.logInfo(CLIRenderer.LINE_DECO);
        CLIRenderer.vuota();
    }
}