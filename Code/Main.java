
//NOTES:
// fix data-- why does period frequency decrease with number of models?


import java.util.HashMap;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static java.lang.Math.max;


public class Main {

    public static int modelDepth = 7;
    public static int lineLength = 5000;
    public static String fileName = "gatsby_text.txt";

    public static Model[] models = new Model[modelDepth];

    // this will store the previous words 1, 2, ... , modelDepth words away from the word being generated
    public static String[] previousWords = new String[modelDepth];


    public static void main(String[] args) {
        Main mothership = new Main();
        for (int i = 0; i < modelDepth; i++) {
            models[i] = new Model();
        }
        mothership.process();
        mothership.generate();

    }

    //process the raw data into an arraylist of (cleaned up) lines
    private void process() {
        List<String> unprocessedLines = readFile();

        for (String line : unprocessedLines) {
            processLine(line);
        }
    }

    // get the data from the txt file
    private List<String> readFile() {
        try {
            //split into lines based on linelength variable from above
            String content = Files.readString(Path.of(fileName));
            String[] words = content.split("\\s+");
            List<String> lines = new ArrayList<>();

            StringBuilder currentLine = new StringBuilder();

            // set the linelength with maximum i value
            int i = 0;
            for (String word : words) {
                currentLine.append(word).append(" ");
                if (i < lineLength) {
                    i++;
                } else {
                    lines.add(currentLine.toString());
                    currentLine.setLength(0);
                    i = 0;
                }
            }
            return(lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processLine(String line) {
        //replace line breaks with spaces
        line = line.replace("\n", " ");
        line = line.replace("\r", " ");
        line = line.replace("\r\n", " ");

        // remove double spaces
        line = line.replace("  ", " ");


        // turn the String line into an array of strings words
        String[] words = line.split("\\s");

        // add each of the words and the next word to the table
        int j = 1;
        while (j <= models.length) {
            for (int i = 0; i < words.length - j; i++) {
                models[j-1].addWordPair(words[i], words[i+j]);
            }
            j++;
        }



    }

    //generate the text
    private void generate() {
        // seed word
        String previousWord = "In";
        String newSong = previousWord + " ";

        
        previousWords[0] = previousWord;


        // number of "sentences" (periods) in the text
        int totalPeriods = 4;
        int periodCounter = 0;

        // generate text
        while (periodCounter < totalPeriods) {

            //generate arraylist where each component is the output of nextWord for each model
            List<List<String>> listOfLists = new ArrayList<>();
            for (int i = 0; i < modelDepth; i++) {
                listOfLists.add(models[i].nextWord(previousWords[i]));
            }

            // find the overlap between these lists, if any
            listOfListsIndexer = 1;
            List<String> trueOverlapList = generateOverlap(listOfLists.get(0), listOfLists.get(1), listOfLists);

            // randomly generate the next word
            String theFollowingWord = trueOverlapList.get((int) (Math.random() * trueOverlapList.size()));

            // shunt items in previousWords over by one index, replacing the first word with the new word
            for (int i = modelDepth - 1; i > 0; i--) {
                previousWords[i] = previousWords[i-1];
            }
            previousWords[0] = theFollowingWord;


            if (theFollowingWord.contains(".")) {
                periodCounter++;
            }
            newSong += theFollowingWord + " ";
            previousWord = theFollowingWord;
        }

        System.out.println("HERE'S YOUR SONG: \n" + newSong);
    }

    // a function that moves through the lists
    // and compares pairs of them, generating a list of
    // their intersection with each element's
    // multiplicity = f(mult (list 0), mult (list 1))
    // where f is defined below
    public static int listOfListsIndexer;
    private List<String> generateOverlap (List <String> list0, List <String> list1, List<List<String>> listOfLists) {
        // HashMap will count the multiplicity of each word from the lists
        Map<String, Integer> mapOfList0 = new HashMap<>();
        Map<String, Integer> mapOfList1 = new HashMap<>();

        //count occurrences in list0
        for (String word : list0) {
            mapOfList0.put(word, mapOfList0.getOrDefault(word, 0) +1 );
        }
        for (String word : list1) {
            mapOfList1.put(word, mapOfList1.getOrDefault(word, 0) +1 );
        }

        List<String> overlapCache = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : mapOfList0.entrySet()) {
            String word = entry.getKey();
            int multiplicity0 = entry.getValue();
            int multiplicity1 = mapOfList1.getOrDefault(word, 0);
            int i = 0;

            // if list1 contains the word too
            if (multiplicity1 != 0) {

                //note that whatever is happening in the "i < ..." constitutes f(mult(0), mult(1))
                //as described in the note for this function
                while (i < max(multiplicity0 , multiplicity1)) {
                    overlapCache.add(word);
                    i++;
                }
            }
            //System.out.println(overlapCache);
        }
        if (overlapCache.isEmpty() || models.length < listOfListsIndexer + 2 ) {
            //System.out.println("finished");
            return(list0);
        } else {
            //System.out.println("unfinished");
            listOfListsIndexer++;
            return(generateOverlap(overlapCache, listOfLists.get(listOfListsIndexer), listOfLists));
        }
    }
}

