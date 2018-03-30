package ar.edu.itba.ati.controller.tabs;

import ar.edu.itba.ati.events.pictures.ShowPictureEvent;
import ar.edu.itba.ati.events.side_menu.ResetParametersEvent;
import ar.edu.itba.ati.io.Pictures;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.Contrast;
import ar.edu.itba.ati.model.transformations.PictureTransformer;
import ar.edu.itba.ati.model.transformations.noise.ExponentialNoise;
import ar.edu.itba.ati.model.transformations.noise.GaussianNoise;
import ar.edu.itba.ati.model.transformations.noise.RayleighNoise;
import ar.edu.itba.ati.model.transformations.noise.SaltAndPepperNoise;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.*;
import ar.edu.itba.ati.services.PictureService;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.function.BiFunction;

public class SideTab1Controller implements SideTabController{

    private final EventBus eventBus;
    private final PictureService pictureService;
    @FXML
    private ScrollPane sideTabView1;

    @FXML
    public TextField gammaVal;

    // Noise
    @FXML
    public TextField gaussVal;
    @FXML
    public TextField exponentialVal;
    @FXML
    public TextField rayleighVal;
    @FXML
    public TextField thresholdVal;
    @FXML
    public TextField saltAndPepperVal;



    // Mask Filters
    @FXML
    public TextField meanSize;
    @FXML
    public TextField medianSize;
    @FXML
    public TextField gaussSigma;
    @FXML
    public TextField highPassSize;


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
            pictureService.normalize();
            pictureService.mapPixelByPixel(p -> (double) p > value ? 255.0 : 0.0);
            eventBus.post(new ShowPictureEvent());
        } catch (NumberFormatException e){
            return;
        }
    }

    @FXML
    private void negative() {
        pictureService.mapPixelByPixel(p -> 255.0 - (double) p);
        eventBus.post(new ShowPictureEvent());
    }

    @FXML
    private void dynamicRange() {
        double productScalar = 5.0;
        pictureService.mapPixelByPixel(px -> productScalar * (double) px);
        pictureService.mapPixelByPixel(p -> (255.0 - 1) / Math.log(1 + 255.0) * Math.log(1 + (double) p));
        pictureService.normalize();
        eventBus.post(new ShowPictureEvent());
    }

    @FXML
    private void gamma() {
        try {
            Double value = Double.valueOf(gammaVal.getText());
            if(value < 0) {
                return;
            }
            pictureService.mapPixelByPixel(px -> Math.pow(255.0 -1, 1 - value) * Math.pow((double)px, value));
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
            applyTransformation(new GaussianNoise(0.0, value));
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
            applyTransformation(new ExponentialNoise(value));
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
            applyTransformation(new RayleighNoise(value));
        } catch (NumberFormatException e){
            return;
        }
    }

    @FXML
    private void saltAndPepperNoise(){
        try {
            Double value = Double.valueOf(saltAndPepperVal.getText());
            if(value < 0 || value >= 0.5){
                return;
            }
            applyTransformation(new SaltAndPepperNoise(value));
        } catch (NumberFormatException e){
            return;
        }
    }

    @FXML
    private void contrast(){
        pictureService.applyTransformation(new Contrast());
        eventBus.post(new ShowPictureEvent());
    }

    private void twoPictureOperation(BiFunction<Double,Double,Double> bf) {
        Picture otherPicture = choosePicture();

        if(otherPicture == null){
            return;
        }

        pictureService.mapPixelByPixel(bf, otherPicture);
        eventBus.post(new ShowPictureEvent());
    }

    private Picture choosePicture(){
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose picture");
        File file = fc.showOpenDialog(sideTabView1.getScene().getWindow());

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

    @FXML
    private void meanFilter(){
        try {
            Integer size = Integer.parseInt(meanSize.getText());
            if(size < 0){
                return;
            }
            applyTransformation(new MeanSmoothing(size));
        } catch (NumberFormatException e){
            return;
        }
    }

    @FXML
    private void medianFilter(){
        try {
            Integer size = Integer.parseInt(medianSize.getText());
            if(size < 0){
                return;
            }

            Integer[][] matrix = new Integer[size][size];
            for(int row = 0; row < size; row++){
                for(int col = 0; col < size; col++){
                    matrix[row][col] = 1;
                }
            }

            applyTransformation(new MedianSmoothing(new Mask(matrix)));
        } catch (NumberFormatException e){
            return;
        }
    }

    @FXML
    private void weightedMedianFilter(){
        Integer[][] mask = new Integer[][]{{1,2,1},{2,4,2},{1,2,1}};
        applyTransformation(new MedianSmoothing(new Mask(mask)));
    }

    @FXML
    private void gaussFilter(){
        try {
            Double sigma = Double.valueOf(gaussSigma.getText());
            applyTransformation(new GaussianSmoothing(sigma));
        } catch (NumberFormatException e){
            return;
        }
    }

    @FXML
    private void highPassFilter(){
        try {
            Integer size = Integer.valueOf(highPassSize.getText());
            if(size < 0){
                return;
            }

            applyTransformation(new HighPassFilter(size));
        } catch (NumberFormatException e){
            return;
        }
    }

    private void applyTransformation(PictureTransformer transformer){
        pictureService.applyTransformation(transformer);
        eventBus.post(new ShowPictureEvent());
    }

    @Override
    public void reset(ResetParametersEvent event) {
        gammaVal.setText("");
        gaussVal.setText("");
        exponentialVal.setText("");
        rayleighVal.setText("");
        thresholdVal.setText("");
        saltAndPepperVal.setText("");
        meanSize.setText("");
        medianSize.setText("");
        highPassSize.setText("");
        gaussSigma.setText("");
    }
}
