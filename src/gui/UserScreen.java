package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import logic.KeyPressHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static logic.Main.APP_ICON_PATH;
import static logic.Main.COLLECTION_PATH;
import static logic.Main.abbrCollection;

/**
 * Created by hanashi on 19.07.2017.
 */
public class UserScreen {
   private Scene scene;
   private GridPane root;
   private Label userText;
   private ListView<String> abbrListView;
   private ListView<TextArea> descrListView;
   private Stage addNewItemWindow;
   private TextArea text;
   private String selectedDescriptionText;
   private static final String BG_COLOR = "#40c080";
   private static final String USER_TEXT_FIELD_BORDER_COLOR = "#ffffff";
   private static final double ABBR_LIST_WIDTH = 400;
   private static final double DESCR_LIST_WIDTH = 610;
   private static final double ABBR_LIST_MAX_WIDTH = 200;
   private static final double DESCR_TEXT_WIDTH = DESCR_LIST_WIDTH - 20;
   private static final double DESCR_TEXT_AREA_SINGLE_ROW_WITH = 88;
   private static final int SCREEN_WIDTH = 1000;
   private static final int SCREEN_HEIGHT = 400;
   private static final String ADD_BUTTON_TOOLTIP = "Добавить запись";
   private static final String REFRESH_BUTTON_TOOLTIP = "Обновить из файла";
   private static final String REMOVE_BUTTON_TOOLTIP = "Удалить запись";
   private static final String REFRESH_BUTTON_ICON_PATH = "file:res/refresh2.png";
   private static final String ADD_BUTTON_ICON_PATH = "file:res/add.png";
   private static final String REMOVE_BUTTON_ICON_PATH = "file:res/remove.png";
   private static final String EMPTY_STR = "";
   private static final String DROP_SHADOW_STYLE = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);";

   private KeyPressHandler keyPressHandler = new KeyPressHandler();

   public UserScreen() {
      createRoot();
      createScene();
      createHeader();
      createBody();
      createAddNewItemWindow();
   }

   private boolean IsModifier(KeyCode code) {
      return code.equals(KeyCode.SHIFT) || code.equals(KeyCode.CONTROL) || code.equals(KeyCode.ALT);
   }

   private void createScene() {
      scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
      scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
         @Override
         public void handle(KeyEvent event) {
            KeyCode code = event.getCode();
            if (!IsModifier(code)) {
               keyPressHandler.handle(code, event.getText(), event.isShiftDown());
               resetSelectedDescriptionText();
            }
         }
      });
   }

   private void createHeader() {
      //кнопка "Обновить из файла"
      createButtons();
      //поле с вводимым пользователем текстом
      createUserTextField();
   }

   private HBox createUserTextStringContainer() {
      HBox headerBox = new HBox();
      headerBox.setBorder(new Border(
              new BorderStroke(Paint.valueOf(USER_TEXT_FIELD_BORDER_COLOR),
                      new BorderStrokeStyle(StrokeType.OUTSIDE, StrokeLineJoin.BEVEL, StrokeLineCap.ROUND,  0, 0, new ArrayList<Double>()),
                      new CornerRadii(1),
                      new BorderWidths(1))));
      headerBox.setMinHeight(33);
      return headerBox;
   }

   private void createButtons()
   {
      HBox buttons = new HBox();
      buttons.getChildren().add(createRefreshButton());
      buttons.getChildren().add(createAddButton());
      buttons.getChildren().add(createRemoveButton());
      buttons.setSpacing(15);

      root.add(buttons, 1, 0);
   }

   private ImageView createRefreshButton(){

       Image imgRefresh = new Image(REFRESH_BUTTON_ICON_PATH);
       final ImageView viewRefresh = new ImageView(imgRefresh);
       viewRefresh.setStyle(DROP_SHADOW_STYLE);
       Tooltip.install(viewRefresh, new Tooltip(REFRESH_BUTTON_TOOLTIP));
       viewRefresh.setOnMouseClicked(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
               abbrCollection.refresh();
           }
       });
       viewRefresh.setOnMousePressed(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
               viewRefresh.setStyle("");
           }
       });
       viewRefresh.setOnMouseReleased(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
               viewRefresh.setStyle(DROP_SHADOW_STYLE);
           }
       });
       return viewRefresh;
   }

   private ImageView createAddButton(){
       Image imgAdd = new Image(ADD_BUTTON_ICON_PATH);
       final ImageView viewAdd = new ImageView(imgAdd);
       viewAdd.setStyle(DROP_SHADOW_STYLE);
       Tooltip.install(viewAdd, new Tooltip(ADD_BUTTON_TOOLTIP));
       viewAdd.setOnMouseClicked(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
               openAddWindow();
           }
       });
       viewAdd.setOnMousePressed(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
               viewAdd.setStyle("");
           }
       });
       viewAdd.setOnMouseReleased(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
               viewAdd.setStyle(DROP_SHADOW_STYLE);
           }
       });

       return viewAdd;
   }

   private ImageView createRemoveButton(){

       Image imgRemove = new Image(REMOVE_BUTTON_ICON_PATH);
       final ImageView viewRemove = new ImageView(imgRemove);
       viewRemove.setStyle(DROP_SHADOW_STYLE);
       Tooltip.install(viewRemove, new Tooltip(REMOVE_BUTTON_TOOLTIP));
       viewRemove.setOnMouseClicked(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
               removeItem();
           }
       });
       viewRemove.setOnMousePressed(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
               viewRemove.setStyle("");
           }
       });
       viewRemove.setOnMouseReleased(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
               viewRemove.setStyle(DROP_SHADOW_STYLE);
           }
       });
       return viewRemove;
   }

   private void createUserTextField() {
      //коробка - обертка
      HBox headerBox = createUserTextStringContainer();

      userText = new Label(EMPTY_STR);
      userText.setId("userText");
      userText.setFont(new Font(20));

      headerBox.getChildren().add(userText);
      root.add(headerBox, 0, 0);
   }

   private void createBody() {

      createAbbreviationList();
      createDescriptionList();
   }

   private void createDescriptionList() {
      HBox descrBox = new HBox();
      descrListView = new ListView<>();
      descrListView.setId("descriptionsList");
      descrListView.setMinWidth(SCREEN_WIDTH - ABBR_LIST_MAX_WIDTH + 10);
      descrBox.getChildren().add(descrListView);
      //descrListView.setMouseTransparent(true);
      root.add(descrBox, 1,1);
   }

   private void createAbbreviationList() {
      //обертка для списка аббревиатур
      HBox abbrBox = new HBox();

      //данные для списка аббревиатур
      ObservableList<String> abbrList = FXCollections.observableArrayList();

      //графический элемент "список аббревиатур"
      abbrListView = new ListView<>();
      abbrListView.setItems(abbrList);
      abbrListView.setId("abbrList");
      abbrListView.setMaxWidth(ABBR_LIST_MAX_WIDTH);

      abbrListView.setOnMouseClicked(abbrListMouseEventHandler());
      abbrListView.setOnKeyPressed(abbrListKeyPressedHandler());
      abbrListView.setOnKeyReleased(abbrListKeyReleasedHandler());
      abbrBox.getChildren().add(abbrListView);
      root.add(abbrBox, 0, 1);
   }

   private EventHandler<MouseEvent> abbrListMouseEventHandler() {
      return  new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent event) {
            String abbrName = abbrListView.getSelectionModel().getSelectedItem();
            if (abbrName != null) {
               descrListView.setItems(createDescrList2(abbrCollection.get(abbrName), abbrName));
               resetSelectedDescriptionText();
            }
         }
      };
   }

   private EventHandler<KeyEvent> abbrListKeyPressedHandler() {
      return new EventHandler<KeyEvent>() {
         @Override
         public void handle(KeyEvent event) {
            if (event.getCode().equals(KeyCode.ENTER)) {
               if (abbrListView.getSelectionModel().getSelectedItem() != null) {
                  descrListView.setItems(createDescrList2(abbrCollection.get(abbrListView.getSelectionModel().getSelectedItem()), abbrListView.getSelectionModel().getSelectedItem()));
                  resetSelectedDescriptionText();
               }
            }
         }
      };
   }

   private EventHandler<KeyEvent> abbrListKeyReleasedHandler() {
      return new EventHandler<KeyEvent>() {
         @Override
         public void handle(KeyEvent event) {
            if (event.getCode().equals(KeyCode.UP) || event.getCode().equals(KeyCode.DOWN)) {
               if (abbrListView.getSelectionModel().getSelectedItem() != null) {
                  descrListView.setItems(createDescrList2(abbrCollection.get(abbrListView.getSelectionModel().getSelectedItem()), abbrListView.getSelectionModel().getSelectedItem()));
                  resetSelectedDescriptionText();
               }
            }
         }
      };
   }

   private void createRoot() {
      root = new GridPane();
      root.setStyle("-fx-background-color: " + BG_COLOR);
   }

   public Scene getScene() {
      return scene;
   }

   public GridPane getRoot() {
      return root;
   }

   public void updateUserTextString(String text) {
      userText.setText(text);

      ObservableList<String> list = createAbbrList(abbrCollection.get(text));
      abbrListView.setItems(list);

      if (!list.isEmpty()) {
         updateDescrList(createDescrList2(abbrCollection.get(text), list.get(0)));
      } else {
         updateDescrList(createDescrList2(abbrCollection.get(EMPTY_STR),EMPTY_STR));
      }
   }

   public ObservableList<String> createAbbrList(Map<String, List<String>> collection) {
      ObservableList<String> list = FXCollections.observableArrayList();

      for (String abbr : collection.keySet()) {
         list.add(abbrCollection.getNormalCase(abbr));
      }
      Collections.sort(list);
      return list;
   }

   public ObservableList<String> createDescrList(Map<String, List<String>> abbrCollection, String abbrName) {
      ObservableList<String> list = FXCollections.observableArrayList();

      if (!abbrCollection.isEmpty() && abbrCollection.get(abbrName) != null) {
         for (String descr : abbrCollection.get(abbrName)) {
            list.add(descr);
         }
         Collections.sort(list);
      }
      return list;
   }

   public ObservableList<TextArea> createDescrList2(Map<String, List<String>> abbrCollection, String abbrName) {
      ObservableList<TextArea> list = FXCollections.observableArrayList();

      if (!abbrCollection.isEmpty() && abbrCollection.get(abbrName.toUpperCase()) != null) {
         for (String descr : abbrCollection.get(abbrName.toUpperCase())) {
            TextArea area = new TextArea(descr);
            area.setWrapText(true);
            area.setPrefWidth(DESCR_TEXT_WIDTH);
            area.setPadding(new Insets(10,0,0,0));

            area.setStyle("-fx-focus-color: transparent; -fx-text-box-border: transparent; -fx-background-color: transparent; -fx-background-insets: -0.4, 1, 2; -fx-background-radius: 3.4, 2, 2;");
            if(area.getText().length() > DESCR_TEXT_AREA_SINGLE_ROW_WITH) {
               area.setPrefHeight(70);
            } else {
               area.setPrefHeight(10);
            }
            //area.setEditable(false);
            //area.setMouseTransparent(true);
            area.setPrefColumnCount(1);
            area.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    TextArea src = (TextArea) event.getSource();
                    selectedDescriptionText = src.getText();
                }
            });
            list.add(area);
         }
      }
      return list;
   }

   public void updateAbbrList(Map<String, List<String>> abbrCollection) {
      abbrListView.setItems(createAbbrList(abbrCollection));
   }

   public void updateDescrList(ObservableList<TextArea> descrList) {
      descrListView.setItems(descrList);
   }

   public void resetAbbrListCursor() {
      abbrListView.getSelectionModel().select(0);
   }

   public void resetSearchString() {
      keyPressHandler.resetSearchString();
   }

   private void openAddWindow() {
      addNewItemWindow.show();
   }

   private void createAddNewItemWindow(){
      GridPane root = new GridPane();

      text = new TextArea();
      text.setWrapText(true);

      Button okButton = new Button("OK");
      okButton.setFocusTraversable(false);
      okButton.setMinWidth(310);
      okButton.setOnAction(new EventHandler<ActionEvent>() {
         @Override
         public void handle(ActionEvent event) {
            saveItem(text.getText());
            addNewItemWindow.close();
            text.clear();
            abbrCollection.refresh();
         }
      });

      root.add(text, 0,0);
      root.add(okButton,0, 1);

      Scene scene = new Scene(root, 300, 190);
      addNewItemWindow = new Stage();
      addNewItemWindow.setScene(scene);
      addNewItemWindow.setResizable(false);
      addNewItemWindow.setAlwaysOnTop(true);
      addNewItemWindow.setTitle("Добавить запись");

      addNewItemWindow.getIcons().add(new Image(APP_ICON_PATH));
   }

   private void saveItem(String text){
      text = text.trim();
      text = "\n" + text;

      if (text.contains("\u002D") || text.contains("\u2013")) {
         int pos;
         pos = text.indexOf("\u002D") == 0 ? text.indexOf("\u2013") :  text.indexOf("\u002D");
          String[] result = text.split(Character.toString(text.charAt(pos)),2);
          result[0] = result[0].toUpperCase();
          text = result[0] + text.charAt(pos) + result[1];

         if (!(text.charAt(pos - 1) == '\t' || text.charAt(pos - 1) == ' ')) {
            result = text.split(Character.toString(text.charAt(pos)),2);
            result[0] += " ";
            text = result[0] + text.charAt(pos) + result[1];
            pos++;
         }

         if (!(text.charAt(pos + 1) == '\t' || text.charAt(pos + 1) == ' ')) {
            result = text.split(Character.toString(text.charAt(pos)),2);
            result[1] = " " + result[1];
            text = result[0] + text.charAt(pos) + result[1];
         }

         try {
            Files.write(Paths.get(COLLECTION_PATH), text.getBytes(), StandardOpenOption.APPEND);
         } catch (IOException e1) {
            e1.printStackTrace();
         }
      }
   }

   private void removeItem(){
       String abbreviationToRemove = abbrListView.getSelectionModel().getSelectedItem();
       if(abbreviationToRemove != null && selectedDescriptionText != null){
           abbrCollection.remove(abbreviationToRemove, selectedDescriptionText);
       }
   }

   private void resetSelectedDescriptionText(){
       selectedDescriptionText = null;
   }
}
