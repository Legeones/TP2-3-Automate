/**
 * Transition.java
 * Defines the Transition class representing a transition in an automaton.
 * It contains the source state, destination state, and the symbol that triggers the transition.
 */
public class Transition {
    private final State fromState;
    private final State toState;
    private final String symbol;

    public Transition(State fromState, State toState, String symbol) {
        this.fromState = fromState;
        this.toState = toState;
        this.symbol = symbol;
    }

    public State getFromState() {
        return fromState;
    }

    public State getToState() {
        return toState;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return "Transition{" +
                "fromState=" + fromState +
                ", toState=" + toState +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}
