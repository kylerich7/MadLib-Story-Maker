import edu.duke.FileResource;
import edu.duke.URLResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MadLib {
  private static final String dataSourceDirectory = "./resources/replacement_words";
  private static final String storySourceDirectory = "./resources/stories/madtemplate2.txt";
  private final HashMap<String, ArrayList<String>> myMap;
  private final Random myRandom;
  private ArrayList<String> usedWordsList;
  private ArrayList<String> usedCategoryList;
  private int replaceCount;

  public MadLib() {
    myMap = new HashMap<String, ArrayList<String>>();
    myRandom = new Random();
    initializeFromSource(dataSourceDirectory);
  }

  public MadLib(String source) {
    myMap = new HashMap<String, ArrayList<String>>();
    myRandom = new Random();
    initializeFromSource(source);
  }

  private void initializeFromSource(String source) {
    usedWordsList = new ArrayList<String>();
    usedCategoryList = new ArrayList<String>();
    replaceCount = 0;

    String[] categories = {
      "adjective", "noun", "color", "country", "name", "animal", "timeframe", "verb", "fruit"
    };

    for (String category : categories) {
      ArrayList<String> words = new ArrayList<String>();
      words = readIt(source + "/" + category + ".txt");

      myMap.put(category, words);
    }
  }

  private String randomFrom(ArrayList<String> source) {
    int index = myRandom.nextInt(source.size());
    return source.get(index);
  }

  private String getSubstitute(String label) {
    if (myMap.containsKey(label)) {
      usedCategoryList.add(label);
      return randomFrom(myMap.get(label));
    }
    if (label.equals("number")) {
      return "" + myRandom.nextInt(50) + 5;
    }
    return "**UNKNOWN**";
  }

  private String processWord(String w) {
    int first = w.indexOf("<");
    int last = w.indexOf(">", first);
    if (first == -1 || last == -1) {
      return w;
    }
    String prefix = w.substring(0, first);
    String suffix = w.substring(last + 1);
    String sub = getSubstitute(w.substring(first + 1, last));
    String sub1 = sub;

    for (String str : usedWordsList) {
      if (sub == str) {
        sub = getSubstitute(w.substring(first + 1, last));
      }
    }
    usedWordsList.add(sub1);
    replaceCount += 1;

    return prefix + sub + suffix;
  }

  private void printOut(String s, int lineWidth) {
    int charsWritten = 0;
    for (String w : s.split("\\s+")) {
      if (charsWritten + w.length() > lineWidth) {
        System.out.println();
        charsWritten = 0;
      }
      System.out.print(w + " ");
      charsWritten += w.length() + 1;
    }
  }

  private String fromTemplate(String source) {
    String story = "";
    if (source.startsWith("http")) {
      URLResource resource = new URLResource(source);
      for (String word : resource.words()) {
        story = story + processWord(word) + " ";
      }
    } else {
      FileResource resource = new FileResource(source);
      for (String word : resource.words()) {
        story = story + processWord(word) + " ";
      }
    }
    return story;
  }

  private ArrayList<String> readIt(String source) {
    ArrayList<String> list = new ArrayList<String>();
    if (source.startsWith("http")) {
      URLResource resource = new URLResource(source);
      for (String line : resource.lines()) {
        list.add(line);
      }
    } else {
      FileResource resource = new FileResource(source);
      for (String line : resource.lines()) {
        list.add(line);
      }
    }
    return list;
  }

  private int totalWordsInMap() {
    int countSoFar = 0;

    for (String category : myMap.keySet()) {
      ArrayList<String> currentWordList = myMap.get(category);

      for (int i = 0; i < currentWordList.size(); i++) {
        countSoFar++;
      }
    }

    return countSoFar;
  }

  private int totalWordsConsidered() {
    int countSoFar = 0;

    for (String category : myMap.keySet()) {
      if (usedCategoryList.contains(category)) {
        ArrayList<String> currentWordList = myMap.get(category);

        for (int i = 0; i < currentWordList.size(); i++) {
          countSoFar++;
        }
      }
    }

    return countSoFar;
  }

  public void makeStory() {
    System.out.println("\n");
    usedWordsList.clear();
    replaceCount = 0;
    String story = fromTemplate(storySourceDirectory);
    printOut(story, 60);
    System.out.println(" ");
    System.out.println(" ");
    System.out.println("Replaced " + replaceCount + " Words");
    System.out.println(" ");
    System.out.println("Words Available For Used Categories: " + totalWordsConsidered());
    System.out.println(" ");
    System.out.println("Words Available For All Categories: " + totalWordsInMap());
  }
}
