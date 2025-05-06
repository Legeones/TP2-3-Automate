import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Automate.java
 * Defines the Automate class representing a finite automaton.
 * It contains methods to read an automaton from a file, check if it is deterministic,
 * and check if a word belongs to the language of the automaton.
 */
public class Automate {
    private State initialState;
    private Set<State> states;
    private Set<State> finalStates;

    private Set<Transition> transitions;

    /**
     * Contructor that reads an automaton from a file.
     * @param nomDuFichier the name of the file containing the automaton description
     * @throws IOException if an I/O error occurs
     */
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

    /**
     * Default constructor that initializes the automaton with empty sets.
     */
    public Automate() {
        this.states = new HashSet<>();
        this.finalStates = new HashSet<>();
        this.transitions = new HashSet<>();
        this.initialState = null;
    }

    /**
     * Returns the state with the given name.
     * @param stateName the name of the state to find
     * @return the state with the given name, or null if not found
     */
    private State getStateByName(String stateName) {
        for (State state : states) {
            if (state.getName().equals(stateName)) {
                return state;
            }
        }
        return null; // State not found
    }

    /**
     * Checks if the automaton is deterministic.
     * @return true if the automaton is deterministic, false otherwise
     */
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

    /**
     * Checks if the given word belongs to the language of the automaton.
     * Tested with the automaton from the file "automaton_not_determinist.txt" and "automaton_determinist.txt".
     * Also with others. It works for ε transitions but maybe not for not deterministic as in multiple path for one symbol.
     * Prints the current state and the symbol being processed. And the error if there is one in the word.
     * @param word the word to check
     * @return true if the word belongs to the language, false otherwise
     */
    boolean appartient(String word) {
        if (initialState == null) {
            System.out.println("The automaton has no initial state");
            return false; // No initial state
        }

        // Init the current state to the initial state
        State currentState = initialState;
        // Get the epsilon closure of the initial state
        Set<State> epsilonClosure = getEpsilonClosure(currentState);

        for (char symbol : word.toCharArray()) {
            System.out.println("Current state: " + currentState);
            System.out.println("Processing symbol: " + symbol);

            State nextState = null;

            // Check for the next state using the epsilon closure for all transitions. ε and the symbol.
            for (State state : epsilonClosure) {
                Transition transition = getTransition(state, String.valueOf(symbol));
                if (transition != null) {
                    nextState = transition.getToState();
                    break; // On prend la première transition valide
                }
            }

            if (nextState == null) {
                System.out.println("No valid transition for symbol: " + symbol);
                return false; // Invalid transition
            }

            System.out.println("Next state: " + nextState +"\n---------------");

            // Update the current state to the next state
            currentState = nextState;
            epsilonClosure = getEpsilonClosure(currentState);
        }

        // Check if the current state is a final state
        if (finalStates.contains(currentState)) {
            return true; // Final state reached
        }

        System.out.println("No final state reached");
        return false; // No final state reached
    }

    /**
     * Computes the epsilon closure of a given state.
     * This method recursively finds all states reachable from the given state through epsilon transitions.
     * @param state the state to compute the epsilon closure for
     * @return a set of states in the epsilon closure
     */
    private Set<State> getEpsilonClosure(State state) {
        Set<State> closure = new HashSet<>();
        // Add the current state to the closure
        closure.add(state);
        // Iterate through all transitions to find epsilon transitions
        for (Transition transition : transitions) {
            // Check if the transition is an epsilon transition from the current state
            if (transition.getFromState().equals(state) && transition.getSymbol().equals("ε")) {
                // Add the target state of the epsilon transition to the closure
                closure.add(transition.getToState());
                // Recursively compute the epsilon closure of the target state
                closure.addAll(getEpsilonClosure(transition.getToState()));
            }
        }
        return closure;
    }

    /**
     * Returns the transition from a given state with a specific symbol.
     * @param fromState the state to find the transition from
     * @param symbol the symbol to find the transition for
     * @return the transition from the given state with the specified symbol, or null if not found
     */
    private Transition getTransition(State fromState, String symbol) {
        // Iterate through all transitions to find the one matching the given state and symbol
        for (Transition transition : transitions) {
            // Check if the transition matches the given state and symbol
            if (transition.getFromState().equals(fromState) && transition.getSymbol().equals(symbol)) {
                return transition;
            }
        }
        return null; // No valid transition found
    }

    /**
     * Returns a set of outgoing transitions from a given state.
     * @param state the state to find the outgoing transitions for
     * @return a set of outgoing transitions from the given state
     */
    private Set<Transition> getOutgoingTransitions(State state) {
        // Create a set to store outgoing transitions
        Set<Transition> outgoingTransitions = new HashSet<>();
        // Iterate through all transitions to find those originating from the given state
        for (Transition transition : transitions) {
            if (transition.getFromState().equals(state)) {
                outgoingTransitions.add(transition);
            }
        }
        return outgoingTransitions;
    }

    //region Getters and Setters
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
    //endregion
}
