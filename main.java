import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class main {
    public static void main(String[] args) {
        try {
            printAutomate("automaton.txt");
            printAutomate("auto_test_1.txt");
            printAutomate("auto_test_2.txt");
            printAutomate("auto_test_3.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printAutomate(String fileName) throws IOException {
        Automate automate = new Automate(fileName);
        System.out.println("Initial State: " + automate.getInitialState());
        System.out.println("States: " + automate.getStates());
        System.out.println("Final States: " + automate.getFinalStates());
        System.out.println("Transitions: " + automate.getTransitions());
        System.out.println("Is deterministic: " + automate.isDeterministe());
        String testWordsFileName = fileName.split("\\.")[0] + "_words.txt";
        for (String word : testWords(testWordsFileName)) {
            System.out.println("Does Word: " + word + " belong to the Automate -> " + automate.appartient(word));
        }
    }

    private static Set<String> testWords(String fileName) throws FileNotFoundException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            return new HashSet<String>(br.lines().collect(Collectors.toSet()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
