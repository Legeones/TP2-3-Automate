import java.util.List;
import java.util.Set;

public class Generation {
    public Generation() {

    }

    public void genererNetLogo(String nomFichier, Automate automate) {
        StringBuilder sb = new StringBuilder();
        sb.append("to setup\n");
        sb.append("\tclear-all\n");
        sb.append("\tset-default-shape turtles \"circle\"\n");
        sb.append("\tset default-shape turtles \"circle\"\n");
        sb.append("\tset default-shape patches \"square\"\n");
        sb.append("\tset default-shape links \"line\"\n");
        sb.append("end\n");

        sb.append("to go\n");
        sb.append("\task turtles [");
        sb.append(" set color red ");
        sb.append("]\n");
        sb.append("end\n");
        Set<Transition> transitions = automate.getTransitions();
        for (State etat : automate.getStates()){
            List<Transition> sorties = transitions.stream()
                    .filter(t -> etat.equals(t.getFromState())).toList();
            sb.append("\n");
            sb.append("to ").append(etat.getName()).append("\n");
            sb.append("\tprint \"").append(etat.getName()).append("\"\n");
            for (int i = 0; i < sorties.size(); i++) {
                Transition t = sorties.get(i);
                String tabulation = "\t".repeat(i+1);
                String condition = "(" + t.getSymbol() + " = true)";
                String action = tabulation+ "\t\tset new-activity [ ->" + t.getToState() + "]\n";
                if (i < sorties.size() - 1) {
                    sb.append(tabulation).append("ifelse ").append(condition).append(" [\n")
                            .append(action)
                            .append(tabulation).append("]\n")
                            .append(tabulation).append("[\n");
                } else {
                    sb.append(tabulation).append("if ").append(condition).append(" [\n")
                            .append(action)
                            .append(tabulation).append("]\n");
                    for (int j = 0; j < sorties.size() - 1; j++) {
                        sb.append(tabulation).append("]\n");
                    }
                }
            }
            sb.append("end\n");
        }

        // Write the generated code to a file
        try (java.io.FileWriter writer = new java.io.FileWriter(nomFichier)) {
            writer.write(sb.toString());
            System.out.println("NetLogo code generated successfully.");
        } catch (java.io.IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
