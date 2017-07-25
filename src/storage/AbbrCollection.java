package storage;

import logic.Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by hanashi on 20.07.2017.
 */
public class AbbrCollection
{
   private Map<String, List<String>> collection;
   private List<String> splitPatterns = Arrays.asList(" \u002D ", "\t\u002D ", " \u002D\t", "\t\u002D\t", " \u2013 ", "\t\u2013 ", " \u2013\t", "\t\u2013\t");

   public AbbrCollection(String filePath)
   {
      collection = new HashMap<>();
      fillCollection(filePath);
   }

   private void fillCollection(String filePath)
   {
      if(!filePath.isEmpty())
      {
         File abbrCollectionFile = new File(filePath);
         if(abbrCollectionFile.exists())
         {
            try
            {
               BufferedReader reader = new BufferedReader(new FileReader(abbrCollectionFile));
               String line;
               while ((line = reader.readLine()) != null)
               {
                  List<String> values = new ArrayList<>();

                  for (String pattern : splitPatterns)
                  {
                     values = Arrays.asList(line.split(pattern));
                     if (values.size() == 2)
                     {
                        break;
                     }
                  }

                  if(values.size() == 2)
                  {
                     if (collection.keySet().contains(values.get(0)))
                     {
                        collection.get(values.get(0)).add(values.get(1));
                     }
                     else
                     {
                        List<String> descriptions = new ArrayList<String>();
                        descriptions.add(values.get(1));
                        collection.put(values.get(0), descriptions);
                     }
                  }
               }
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
         }
      }
   }

   public Map<String, List<String>> get(String abbrName)
   {
      Map<String, List<String>> result = new HashMap<>();
      for (String abbr : collection.keySet())
      {
         if (abbr.toUpperCase().startsWith(abbrName.toUpperCase()))
         {
            result.put(abbr, collection.get(abbr));
         }
      }
      return result;
   }

   public Map<String, List<String>> getAll()
   {
      Map<String, List<String>> result = new HashMap<>();
      for (String abbr : collection.keySet())
      {
         result.put(abbr, collection.get(abbr));
      }
      return result;
   }

   public void refresh()
   {
      collection = new HashMap<>();
      fillCollection(Main.COLLECTION_PATH);
      Main.screen.updateAbbrList(collection);
      Main.screen.updateUserTextString("");
      Main.screen.resetAbbrListCursor();
      Main.screen.resetSearchString();
   }
}
