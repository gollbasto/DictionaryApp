package storage;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static logic.Main.COLLECTION_PATH;
import static logic.Main.screen;

/**
 * Created by hanashi on 20.07.2017.
 */
public class AbbrCollection {
   private TreeMap<String, List<String>> collection;
   private TreeMap<String, String> normalCase;
   private List<String> splitPatterns = Arrays.asList(" \u002D ", "\t\u002D ", " \u002D\t", "\t\u002D\t", " \u2013 ", "\t\u2013 ", " \u2013\t", "\t\u2013\t");

   public AbbrCollection(String filePath) {
      fillCollectionFromFile(filePath);
   }

   private void fillCollectionFromFile(String filePath) {
      collection = new TreeMap();
      normalCase = new TreeMap<>();
      if(!filePath.isEmpty()) {

         File abbrCollectionFile = new File(filePath);
         if(abbrCollectionFile.exists()) {
            try {
               BufferedReader reader = new BufferedReader(new FileReader(abbrCollectionFile));
               String line;

               while ((line = reader.readLine()) != null) {
                  Object[] splitResult = splitToAbbreviationAndDescription(line);
                  addOrUpdateAbbreviationToCollection( (boolean) splitResult[1], (List<String>) splitResult[0]);
               }
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
   }

   private void addOrUpdateAbbreviationToCollection(boolean splitSuccess, List<String> values) {
      if(splitSuccess) {
         String abbreviation = values.get(0);
         String description = values.get(1);
         if (collection.keySet().contains(abbreviation.toUpperCase())) {
            collection.get(abbreviation.toUpperCase()).add(description);
         } else {
            List<String> descriptions = new ArrayList<>();
            descriptions.add(description);
            collection.put(abbreviation.toUpperCase(), descriptions);
            normalCase.put(abbreviation.toUpperCase(), abbreviation);
         }
      }
   }

   private Object[] splitToAbbreviationAndDescription(String line) {
      Object[] result = new Object[2];
      List<String> values = new ArrayList<>();
      boolean splitSuccess = false;

      for (String pattern : splitPatterns) {
         values = Arrays.asList(line.split(pattern));
         if (values.size() == 2) {
            splitSuccess = true;
            break;
         }
      }

      result[0] = values;
      result[1] = splitSuccess;
      return result;
   }

   public Map<String, List<String>> get(String abbrName) {
      Map<String, List<String>> result = new TreeMap<>();
      SortedMap<String, List<String>> tail = collection.tailMap(abbrName.toUpperCase());
      for (String abbr : tail.keySet()) {
         if (!abbr.toUpperCase().startsWith(abbrName.toUpperCase())) {
            return result;
         }
         result.put(abbr, collection.get(abbr));
      }
      return result;
   }

   public Map<String, List<String>> getAll() {
      Map<String, List<String>> result = new HashMap<>();
      for (String abbr : collection.keySet()) {
         result.put(abbr, collection.get(abbr));
      }
      return result;
   }

   public void refresh() {
      fillCollectionFromFile(COLLECTION_PATH);
      refreshScreen();
   }

   private void refreshScreen() {
      screen.updateAbbrList(collection);
      screen.updateUserTextString("");
      screen.resetAbbrListCursor();
      screen.resetSearchString();
   }

   public String getNormalCase(String abbrInUpperCase){
      return normalCase.get(abbrInUpperCase);
   }
}
