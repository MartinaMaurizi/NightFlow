package it.ispwproject.nightflow.controller.cli;

import it.ispwproject.nightflow.pattern.state.CLIStateMachine;
import it.ispwproject.nightflow.pattern.state.CLIStateMachineImpl;

public class MainCLI {

    // Costruttore privato per nascondere quello pubblico implicito
    private MainCLI() {
        throw new IllegalStateException("Classe di utilità: non può essere istanziata");
    }

    /**
     * Punto di ingresso per l'interfaccia a riga di comando.
     * Inizializza la State Machine che gestirà la navigazione tra le schermate.
     */
    public static void start() {
        CLIStateMachine machine = new CLIStateMachineImpl();
        machine.start();
    }
}