package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
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
import logic.KeyPressHandler;

import java.util.*;

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
   private static final String BG_COLOR = "#40c080";
   private static final String USER_TEXT_FIELD_BORDER_COLOR = "#ffffff";
   private static final double ABBR_LIST_WIDTH = 400;
   private static final double DESCR_LIST_WIDTH = 610;
   private static final double DESCR_TEXT_WIDTH = DESCR_LIST_WIDTH - 20;
   private static final double DESCR_TEXT_AREA_SINGLE_ROW_WITH = 88;
   private static final int SCREEN_WIDTH = 1000;
   private static final int SCREEN_HEIGHT = 400;
   private static final String REFRESH_BUTTON_TOOLTIP = "Обновить из файла";
   private static final String REFRESH_BUTTON_ICON_PATH = "file:res/refresh2.png";
   private static final String EMPTY_STR = "";

   private KeyPressHandler keyPressHandler = new KeyPressHandler();

   public UserScreen() {
      createRoot();
      createScene();
      createHeader();
      createBody();
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
            }
         }
      });
   }

   private void createHeader() {
      //кнопка "Обновить из файла"
      createRefreshFromFileButton();
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
      return headerBox;
   }

   private void createRefreshFromFileButton() {
      Image img = new Image(REFRESH_BUTTON_ICON_PATH);
      ImageView view = new ImageView(img);
      Tooltip.install(view, new Tooltip(REFRESH_BUTTON_TOOLTIP));

      view.setOnMouseClicked(new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent event) {
            abbrCollection.refresh();
         }
      });
      root.add(view, 1, 0);
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
      descrListView.setMinWidth(DESCR_LIST_WIDTH);
      descrListView.setMaxWidth(DESCR_LIST_WIDTH);
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
      abbrListView.setMinWidth(ABBR_LIST_WIDTH);
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
            ListView<String> abbrList =  (ListView<String>) event.getSource();
            String abbrName = abbrList.getSelectionModel().getSelectedItem();
            if (abbrName != null)
            {
               ListView<TextArea> descrListView = (ListView<TextArea>) abbrList.getParent().getParent().lookup("#descriptionsList");
               descrListView.setItems(createDescrList2(abbrCollection.get(abbrName), abbrName));
            }
         }
      };
   }

   private EventHandler<KeyEvent> abbrListKeyPressedHandler() {
      return new EventHandler<KeyEvent>() {
         @Override
         public void handle(KeyEvent event) {
            if (event.getCode().equals(KeyCode.ENTER))
            {
               ListView<String> abbrList =  (ListView<String>) event.getSource();
               ListView<TextArea> descrListView = (ListView<TextArea>) abbrList.getParent().getParent().lookup("#descriptionsList");
               if (abbrList.getSelectionModel().getSelectedItem() != null) {
                  descrListView.setItems(createDescrList2(abbrCollection.get(abbrList.getSelectionModel().getSelectedItem()), abbrList.getSelectionModel().getSelectedItem()));
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
               ListView<String> abbrList =  (ListView<String>) event.getSource();
               ListView<TextArea> descrListView = (ListView<TextArea>) abbrList.getParent().getParent().lookup("#descriptionsList");
               if (abbrList.getSelectionModel().getSelectedItem() != null) {
                  descrListView.setItems(createDescrList2(abbrCollection.get(abbrList.getSelectionModel().getSelectedItem()), abbrList.getSelectionModel().getSelectedItem()));
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

      if (!list.isEmpty())
      {
         updateDescrList(createDescrList2(abbrCollection.get(text), list.get(0)));
      }
      else
      {
         updateDescrList(createDescrList2(abbrCollection.get(EMPTY_STR),EMPTY_STR));
      }
   }

   public ObservableList<String> createAbbrList(Map<String, List<String>> abbrCollection) {
      ObservableList<String> list = FXCollections.observableArrayList();

      for (String abbr : abbrCollection.keySet())
      {
         list.add(abbr);
      }
      Collections.sort(list);
      return list;
   }

   public ObservableList<String> createDescrList(Map<String, List<String>> abbrCollection, String abbrName) {
      ObservableList<String> list = FXCollections.observableArrayList();

      if (!abbrCollection.isEmpty() && abbrCollection.get(abbrName) != null)
      {
         for (String descr : abbrCollection.get(abbrName))
         {
            list.add(descr);
         }
         Collections.sort(list);
      }
      return list;
   }

   public ObservableList<TextArea> createDescrList2(Map<String, List<String>> abbrCollection, String abbrName) {
      ObservableList<TextArea> list = FXCollections.observableArrayList();

      if (!abbrCollection.isEmpty() && abbrCollection.get(abbrName) != null) {
         for (String descr : abbrCollection.get(abbrName))
         {
            TextArea area = new TextArea(descr);
            area.setWrapText(true);
            area.setPrefWidth(DESCR_TEXT_WIDTH);
            area.setPadding(new Insets(10,0,0,0));

            area.setStyle("-fx-focus-color: transparent; -fx-text-box-border: transparent; -fx-background-color: transparent; -fx-background-insets: -0.4, 1, 2; -fx-background-radius: 3.4, 2, 2;");
            if(area.getText().length() > DESCR_TEXT_AREA_SINGLE_ROW_WITH)
            {
               area.setPrefHeight(70);
            }
            else
            {
               area.setPrefHeight(10);
            }
            //area.setEditable(false);
            //area.setMouseTransparent(true);
            area.setPrefColumnCount(1);
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
}
