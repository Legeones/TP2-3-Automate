import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class Automate {
    private State initialState;
    private Set<State> states;
    private Set<State> finalStates;

    private Set<Transition> transitions;

    public Automate(String nomDuFichier) throws IOException {
        this.states = new HashSet<>();
        this.finalStates = new HashSet<>();
        this.transitions = new HashSet<>();
        this.initialState = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(nomDuFichier))) {
            String line;
            boolean parsingStates = false;
            boolean parsingTransitions = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.equals("L")) {
                    parsingStates = false;
                    parsingTransitions = false;
                    continue; // Skip empty lines
                }
                if (line.equals("states")) {
                    parsingStates = true;
                    parsingTransitions = false;
                } else if (line.equals("transitions")) {
                    parsingStates = false;
                    parsingTransitions = true;
                } else if (parsingStates) {
                    String[] parts = line.split(":");
                    State state = new State(parts[0]);
                    states.add(state);
                    if (parts.length > 1) {
                        if (parts[1].equals("I")) {
                            if (initialState != null) {
                                throw new Exception("Multiple initial states found");
                            }
                            initialState = state;
                        } else if (parts[1].equals("F")) {
                            finalStates.add(state);
                        }
                    }
                } else if (parsingTransitions) {
                    String[] parts = line.split("->");
                    String fromStateName = parts[0];
                    String[] toParts = parts[1].split("\\[");
                    String toStateName = toParts[0];
                    String[] symbols = toParts[1].replace("label=", "").split("]")[0].split(",");

                    State fromState = getStateByName(fromStateName);
                    State toState = getStateByName(toStateName);

                    if (fromState != null && toState != null) {
                        for (String symbol : symbols) { // Create a transition for each label
                            transitions.add(new Transition(fromState, toState, symbol.trim()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Automate() {
        this.states = new HashSet<>();
        this.finalStates = new HashSet<>();
        this.transitions = new HashSet<>();
        this.initialState = null;
    }

    private State getStateByName(String stateName) {
        for (State state : states) {
            if (state.getName().equals(stateName)) {
                return state;
            }
        }
        return null; // State not found
    }

    boolean isDeterministe() {
        AtomicBoolean isDeterministic = new AtomicBoolean(true);
        for (State state : states) {
            Set<Transition> outgoingTransitions = getOutgoingTransitions(state);
            outgoingTransitions.forEach(transition -> {
                for (Transition otherTransition : outgoingTransitions) {
                    if (!transition.equals(otherTransition) && transition.getSymbol().equals(otherTransition.getSymbol())) {
                        System.out.println("Non-deterministic transition found: " + transition + " and " + otherTransition);
                        isDeterministic.set(false);
                        return; // Non-deterministic transition found
                    }
                }
            });
        }
        return isDeterministic.get();
    }

    boolean appartient(String word) {
        Set<State> currentStates = new HashSet<>();
        currentStates.add(initialState);
        currentStates.addAll(getEpsilonClosure(initialState));

        for (char symbol : word.toCharArray()) {
            Set<State> nextStates = new HashSet<>();
            for (State state : currentStates) {
                Transition transition = getTransition(state, String.valueOf(symbol));
                if (transition != null) {
                    nextStates.add(transition.getToState());
                    nextStates.addAll(getEpsilonClosure(transition.getToState()));
                }
            }
            if (nextStates.isEmpty()) {
                return false; // Aucun état valide
            }
            currentStates = nextStates;
        }

        for (State state : currentStates) {
            if (finalStates.contains(state)) {
                return true; // Un état final est atteint
            }
        }
        return false;
    }

    private Set<State> getEpsilonClosure(State state) {
        Set<State> closure = new HashSet<>();
        closure.add(state);
        for (Transition transition : transitions) {
            if (transition.getFromState().equals(state) && transition.getSymbol().isEmpty()) {
                closure.add(transition.getToState());
                closure.addAll(getEpsilonClosure(transition.getToState()));
            }
        }
        return closure;
    }

    private Transition getTransition(State fromState, String symbol) {
        for (Transition transition : transitions) {
            if (transition.getFromState().equals(fromState) && transition.getSymbol().equals(symbol)) {
                return transition;
            }
        }
        return null; // No valid transition found
    }

    private Set<Transition> getOutgoingTransitions(State state) {
        Set<Transition> outgoingTransitions = new HashSet<>();
        for (Transition transition : transitions) {
            if (transition.getFromState().equals(state)) {
                outgoingTransitions.add(transition);
            }
        }
        return outgoingTransitions;
    }

    public State getInitialState() {
        return initialState;
    }

    public Set<State> getStates() {
        return states;
    }

    public Set<State> getFinalStates() {
        return finalStates;
    }

    public Set<Transition> getTransitions() {
        return transitions;
    }
}
