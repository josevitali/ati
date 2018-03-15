package ar.edu.itba.ati.controller.tabs;

import ar.edu.itba.ati.events.pictures.ShowPictureEvent;
import ar.edu.itba.ati.io.Pictures;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.services.PictureService;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SideTab1Controller {

    private final EventBus eventBus;
    private final PictureService pictureService;
    @FXML
    private AnchorPane anchorPane;

    @Inject
    public SideTab1Controller(final EventBus eventBus, final PictureService pictureService){
        this.eventBus = eventBus;
        this.pictureService = pictureService;
    }

    @FXML
    private void addPicture(){
        Picture otherPicture = choosePicture();

        if(otherPicture == null){
            return;
        }

        pictureService.getPicture().mapPixelByPixel(new BiFunction<Double, Double, Double>() {
            @Override
            public Double apply(Double aDouble, Double aDouble2) {
                return aDouble + aDouble2;
            }
        }, otherPicture);
        pictureService.getPicture().normalize();
        eventBus.post(new ShowPictureEvent());
    }

    @FXML
    private void subtractPicture(){
        Picture otherPicture = choosePicture();

        if(otherPicture == null){
            return;
        }

        pictureService.getPicture().mapPixelByPixel(new BiFunction<Double, Double, Double>() {
            @Override
            public Double apply(Double aDouble, Double aDouble2) {
                return aDouble - aDouble2;
            }
        }, otherPicture);
        pictureService.getPicture().normalize();
        eventBus.post(new ShowPictureEvent());
    }

    @FXML
    private void negative(){
        pictureService.getPicture().mapPixelByPixel(p -> 255.0 - (double) p);
        eventBus.post(new ShowPictureEvent());
    }

    private Picture choosePicture(){
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose picture");
        File file = fc.showOpenDialog(anchorPane.getScene().getWindow());

        if(file == null){
            return null;
        }

        try {
            return Pictures.getPicture(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
