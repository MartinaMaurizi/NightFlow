package it.ispwproject.nightflow.pattern.state;

import it.ispwproject.nightflow.controller.cli.InitialCLI;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Context del pattern GoF State.
 * Mantiene lo stato corrente e la history degli stati precedenti.
 */
public class CLIStateMachineImpl implements CLIStateMachine {

    private AbstractCLIState currentState;
    private final Deque<AbstractCLIState> stateHistory = new ArrayDeque<>();

    public CLIStateMachineImpl() {
        this.currentState = new InitialCLI();
    }

    @Override
    public void start() {
        currentState = new InitialCLI();
        currentState.entry(this);
        goNext();
    }

    @Override
    public void goNext() {
        if (currentState != null) {
            currentState.action(this);
        }
    }

    @Override
    public void goBack() {
        if (!stateHistory.isEmpty()) {
            currentState.exit(this);
            currentState = stateHistory.pop();
            currentState.entry(this);
            goNext();
        }
    }

    @Override
    public void transition(AbstractCLIState nextState) {
        currentState.exit(this);
        if (currentState != null) {
            stateHistory.push(currentState);
        }
        currentState = nextState;
        currentState.entry(this);
        goNext();
    }

    @Override
    public AbstractCLIState getState() {
        return currentState;
    }

    @Override
    public void setState(AbstractCLIState state) {
        this.currentState = state;
    }

}