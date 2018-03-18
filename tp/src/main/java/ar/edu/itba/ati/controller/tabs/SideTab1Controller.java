package ar.edu.itba.ati.controller.tabs;

import ar.edu.itba.ati.events.pictures.ShowPictureEvent;
import ar.edu.itba.ati.io.Pictures;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.services.PictureService;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.util.function.BiFunction;

public class SideTab1Controller {

    private final EventBus eventBus;
    private final PictureService pictureService;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    public TextField dotProductVal;
    @FXML
    public TextField thresholdVal;

    @Inject
    public SideTab1Controller(final EventBus eventBus, final PictureService pictureService){
        this.eventBus = eventBus;
        this.pictureService = pictureService;
    }

    @FXML
    private void addPicture(){
        twoPictureOperation((px1,px2) -> px1 + px2);
    }

    @FXML
    private void subtractPicture(){
        twoPictureOperation((px1,px2) -> px1 - px2);
    }

    @FXML
    private void productPicture(){
        twoPictureOperation((px1,px2) -> px1 * px2);
    }

    @FXML
    private void dotProduct(){
        try {
            Double value = Double.valueOf(dotProductVal.getText());
            pictureService.getPicture().mapPixelByPixel(px -> value * (double) px);
            eventBus.post(new ShowPictureEvent());
        } catch (NumberFormatException e){
            return;
        }
    }

    @FXML
    private void threshold() {
        try {
            Double value = Double.valueOf(thresholdVal.getText());
            if(value > 255 || value < 0) {
                return;
            }
            pictureService.getPicture().mapPixelByPixel(p -> (double) p > value ? 255.0 : 0.0);
            eventBus.post(new ShowPictureEvent());
        } catch (NumberFormatException e){
            return;
        }
    }

    private void twoPictureOperation(BiFunction<Double,Double,Double> bf) {
        Picture otherPicture = choosePicture();

        if(otherPicture == null){
            return;
        }

        pictureService.getPicture().mapPixelByPixel(bf, otherPicture);
        eventBus.post(new ShowPictureEvent(pictureService.getPicture().getNormalizedClone()));
    }

    @FXML
    private void negative() {
        pictureService.getPicture().mapPixelByPixel(p -> 255.0 - (double) p);
        eventBus.post(new ShowPictureEvent(pictureService.getPicture().getNormalizedClone()));
    }

    @FXML
    private void dynamicRange() {
        pictureService.getPicture().mapPixelByPixel(p -> (255.0 - 1) / Math.log(1 + 255.0) * Math.log(1 + (double) p));
        pictureService.getPicture().normalize();
        eventBus.post(new ShowPictureEvent(pictureService.getPicture().getNormalizedClone()));
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
