package ar.edu.itba.ati.controller;

import ar.edu.itba.ati.events.pictures.CropEvent;
import ar.edu.itba.ati.events.toolbar.ShowToolbarEvent;
import ar.edu.itba.ati.services.PictureService;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class ToolbarController {

    public EventBus eventBus;
    private PictureService pictureService;
    public ToolBar toolbar;

    public Button cropMenuItem;
    public Button switchButton;
    public Button newWindow;

    @Inject
    public ToolbarController(final EventBus eventBus, PictureService pictureService){
        this.eventBus = eventBus;
        this.pictureService = pictureService;
    }

    @FXML
    @Subscribe
    public void showToolBar(ShowToolbarEvent showToolbarEvent){
        toolbar.setVisible(true);
        cropMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                System.out.println("posteo el crop event");
                eventBus.post(new CropEvent());
            }
        });
        switchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("switcheo las fotos");
                pictureService.switchPictures();
            }
        });
        newWindow.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("open new window");

                ImageView iv = new ImageView();
                iv.setImage(SwingFXUtils.toFXImage(pictureService.getAuxPicture().toBufferedImage(), null));
                StackPane secondaryLayout = new StackPane();
                secondaryLayout.getChildren().add(iv);

                Scene secondScene = new Scene(secondaryLayout, iv.getFitWidth(), iv.getFitHeight());

                // New window (Stage)
                Stage newWindow = new Stage();
                newWindow.setTitle("Image snapshot");
                newWindow.setScene(secondScene);

                newWindow.show();

            }
        });
    }


}
