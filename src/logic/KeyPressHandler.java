package logic;

import javafx.scene.input.KeyCode;

import static logic.Main.screen;

/**
 * Created by hanashi on 19.07.2017.
 */
public class KeyPressHandler {
   private String searchString = "";

   public void handle(KeyCode code, String text, boolean isShiftDown) {
      switch (code) {
         case ESCAPE:
            resetSearchString();
            screen.updateUserTextString(searchString);
            break;

         case DELETE:
         case BACK_SPACE:
            if (!searchString.isEmpty()) {
               searchString = searchString.substring(0, searchString.length() - 1);
               screen.updateUserTextString(searchString);
            }
            break;

         case SHIFT:
         case CONTROL:
         case ALT:
         case LEFT:
         case RIGHT:
            break;

         default:
            if (!isShiftDown) {
               searchString += text;
            } else {
               if(Character.isLetter(text.charAt(0))) {
                  searchString += text.toUpperCase();
               }
            }

            screen.updateUserTextString(searchString);
            break;
      }
   }

   public void resetSearchString() {
      searchString = "";
   }
}
