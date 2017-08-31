package logic;

import gui.UserScreen;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import storage.AbbrCollection;

public class Main extends Application {
   public static final String COLLECTION_PATH = "./res/abbr.txt";
   public static final String APP_ICON_PATH =  "file:res/app.png";
   private static final String APP_TITLE = "AbbrDictionaryApp";
   public static UserScreen screen = new UserScreen();
   public static AbbrCollection abbrCollection = new AbbrCollection(COLLECTION_PATH);

   @Override
   public void start(Stage primaryStage) throws Exception {
      screen.updateAbbrList(abbrCollection.getAll());
      primaryStage.setTitle(APP_TITLE);
      primaryStage.setScene(screen.getScene());
      primaryStage.setResizable(false);
      primaryStage.getIcons().add(new Image(APP_ICON_PATH));
      primaryStage.show();
   }

   public static void main(String[] args) {
      launch(args);
   }
}
