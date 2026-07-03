package it.ispwproject.nightflow.pattern.state;

/**
 * AbstractState del pattern GoF State.
 * Ogni schermata CLI estende questa classe e implementa action().
 * Le transizioni avvengono chiamando goNext() o goBack() dall'interno di action().
 */
public abstract class AbstractCLIState {

    protected AbstractCLIState() {}

    /** Comportamento dello stato corrente — implementato da ogni ConcreteState. */
    public abstract void action(CLIStateMachine context);

    /** Azione eseguita all'ingresso nello stato. */
    public void entry(CLIStateMachine context) {}

    /** Azione eseguita all'uscita dallo stato. */
    public void exit(CLIStateMachine context) {}

    /** Transisce allo stato successivo. */
    public void goNext(CLIStateMachine context, AbstractCLIState nextState) {
        context.transition(nextState);
    }

    /** Torna allo stato precedente. */
    public void goBack(CLIStateMachine context) {
        context.goBack();
    }
}
