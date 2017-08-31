package gui;

import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import static gui.UserScreen.DROP_SHADOW_STYLE;

/**
 * Created by sbt-shapin-ev on 31.08.2017.
 */
public class ImageViewStylized extends ImageView{

    public static ImageView get(String imgPath, String tooltip){
        Image img = new Image(imgPath);
        final ImageView imageView = new ImageView(img);
        imageView.setStyle(DROP_SHADOW_STYLE);
        Tooltip.install(imageView, new Tooltip(tooltip));
        imageView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                imageView.setStyle("");
            }
        });
        imageView.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                imageView.setStyle(DROP_SHADOW_STYLE);
            }
        });
        return imageView;
    }
}
