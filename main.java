import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class main {
    public static void main(String[] args) {
        try {
            printAutomate("automaton_not_determinist.txt");
            Generation generation = new Generation();
            generation.genererNetLogo("test_netlogo.nls", new Automate("automaton_not_determinist.txt"));
            //printAutomate("automaton_determinist.txt");
            //printAutomate("auto_test_1.txt");
            //printAutomate("auto_test_2.txt");
            //printAutomate("auto_test_3.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * This method reads an automaton from a file, prints its details, and tests from the file corresponding to the automaton.
     */
    private static void printAutomate(String fileName) throws IOException {
        Automate automate = new Automate(fileName);
        System.out.println("Initial State: " + automate.getInitialState());
        System.out.println("States: " + automate.getStates());
        System.out.println("Final States: " + automate.getFinalStates());
        System.out.println("Transitions: " + automate.getTransitions());
        System.out.println("Is deterministic: " + automate.isDeterministe());
        String testWordsFileName = fileName.split("\\.")[0] + "_words.txt";
        String testWordResultExpected = "";
        System.out.println("\nTest Words: ");
        System.out.println("=====================================");
        for (String word : testWords(testWordsFileName)) {
            if (word.contains("::")) {
                testWordResultExpected = word;
            } else {
                System.out.println("Does Word: " + word + " belong to the Automate -> " + automate.appartient(word));
                System.out.println(testWordResultExpected);
                System.out.println("-------------------------------------");
            }
        }
    }

    /*
     * This method reads a file containing test words and returns them as a list of strings.
     */
    private static List<String> testWords(String fileName) throws FileNotFoundException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            return br.lines().collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
