
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Model {
    //hashmap of word 1, then list of words that follow it (with frequencies corresponding to how often they followed it)
    Map<String, List<String>> map = new HashMap<>();

    // function for adding a key, value pair to the map, and creating a new key if necessary
    public void addWordPair(String firstWord, String secondWord) {
        if (!map.containsKey(firstWord)) {
            if (firstWord == "") {
                List<String> holder = new ArrayList<>();
                holder.add("Error!");
                map.put(firstWord, holder);
            } else {
                map.put(firstWord, new ArrayList<>());
            }
        }

        map.get(firstWord).add(secondWord);
    }

    // returns the next word according to Markov chain probabilities
    public List<String> nextWord(String firstWord) {
        if (!map.containsKey(firstWord)) {
            //throw new IllegalArgumentException("No pair for: " + firstWord);
            List<String> returnable = new ArrayList<>();
            returnable.add("Another_Error!");
            return(returnable);
        } else {
            return map.get(firstWord);
        }
    }
}
