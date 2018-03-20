package ar.edu.itba.ati.controller.tabs;

import ar.edu.itba.ati.events.pictures.ShowPictureEvent;
import ar.edu.itba.ati.io.Pictures;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.PictureTransformer;
import ar.edu.itba.ati.model.transformations.noise.ExponentialNoise;
import ar.edu.itba.ati.model.transformations.noise.GaussianNoise;
import ar.edu.itba.ati.model.transformations.noise.RayleighNoise;
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
    public TextField gaussVal;
    @FXML
    public TextField exponentialVal;
    @FXML
    public TextField rayleighVal;
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

    @FXML
    private void gaussNoise() {
        try {
            Double value = Double.valueOf(gaussVal.getText());
            if(value < 0) {
                return;
            }
            PictureTransformer gaussianNoise = new GaussianNoise(0.0, value);
            gaussianNoise.transform(pictureService.getPicture());
            eventBus.post(new ShowPictureEvent());
        } catch (NumberFormatException e){
            return;
        }
    }

    @FXML
    private void exponentialNoise() {
        try {
            Double value = Double.valueOf(exponentialVal.getText());
            if(value < 0) {
                return;
            }
            PictureTransformer exponentialNoise = new ExponentialNoise(value);
            exponentialNoise.transform(pictureService.getPicture());
            eventBus.post(new ShowPictureEvent());
        } catch (NumberFormatException e){
            return;
        }
    }

    @FXML
    private void rayleighNoise() {
        try {
            Double value = Double.valueOf(rayleighVal.getText());
            if(value < 0) {
                return;
            }
            PictureTransformer rayleighNoise = new RayleighNoise(value);
            rayleighNoise.transform(pictureService.getPicture());
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
        eventBus.post(new ShowPictureEvent(pictureService.getPicture()));
    }

    @FXML
    private void negative() {
        pictureService.getPicture().mapPixelByPixel(p -> 255.0 - (double) p);
        eventBus.post(new ShowPictureEvent(pictureService.getPicture()));
    }

    @FXML
    private void dynamicRange() {
        double productScalar = 5.0;
        pictureService.getPicture().mapPixelByPixel(px -> productScalar * (double) px);
        pictureService.getPicture().mapPixelByPixel(p -> (255.0 - 1) / Math.log(1 + 255.0) * Math.log(1 + (double) p));
        pictureService.getPicture().normalize();
        eventBus.post(new ShowPictureEvent(pictureService.getPicture()));
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
