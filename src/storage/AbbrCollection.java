package storage;


import java.io.*;
import java.nio.file.*;
import java.util.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static logic.Main.COLLECTION_BUFFER_PATH;
import static logic.Main.COLLECTION_PATH;
import static logic.Main.screen;

/**
 * Created by hanashi on 20.07.2017.
 */
public class AbbrCollection {
   private TreeMap<String, List<String>> collection;
   private TreeMap<String, String> normalCase;
   private List<String> splitPatterns = Arrays.asList(" \u002D ", "\t\u002D ", " \u002D\t", "\t\u002D\t", " \u2013 ", "\t\u2013 ", " \u2013\t", "\t\u2013\t");

   public AbbrCollection() {
      fillCollectionFromFile();
   }

   private void fillCollectionFromFile() {
      collection = new TreeMap();
      normalCase = new TreeMap<>();
      if(!COLLECTION_PATH.isEmpty()) {

         File abbrCollectionFile = new File(COLLECTION_PATH);
         if(abbrCollectionFile.exists()) {
            try {
               BufferedReader reader = new BufferedReader(new FileReader(abbrCollectionFile));
               String line;

               while ((line = reader.readLine()) != null) {
                  Object[] splitResult = splitToAbbreviationAndDescription(line);
                  addOrUpdateAbbreviationToCollection( (boolean) splitResult[1], (List<String>) splitResult[0]);
               }
               reader.close();
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
      fillCollectionFromFile();
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

   public void remove(String abbreviationToRemove, String descriptionToRemove){

      File file = new File(COLLECTION_PATH);
      File fileBuffer = new File(COLLECTION_BUFFER_PATH);


      try {
         if (fileBuffer.exists())
            fileBuffer.delete();
         fileBuffer.createNewFile();
      } catch (IOException e) {
         e.printStackTrace();
      }

      try {
         FileReader fileReader = new FileReader(file);
         BufferedReader br = new BufferedReader(fileReader);

         String line;
         List<String> splitValues;
         List<String> result = new ArrayList<>();
         while ((line = br.readLine()) != null) {
            for (String pattern : splitPatterns) {
              splitValues = Arrays.asList(line.split(pattern));
               if (splitValues.size() == 2) {
                  result = splitValues;
               }
            }
            if(!(result.get(0).equals(abbreviationToRemove)&& result.get(1).equals(descriptionToRemove))){
               Files.write(Paths.get(COLLECTION_BUFFER_PATH), (line  + "\n").getBytes() , StandardOpenOption.APPEND);
            }
         }
         fileReader.close();
         br.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
      try {
         Files.copy(Paths.get(COLLECTION_BUFFER_PATH), Paths.get(COLLECTION_PATH), REPLACE_EXISTING);
      } catch (IOException e) {
         e.printStackTrace();
      }
      fileBuffer.delete();
      refresh();
   }
}
