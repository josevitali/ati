package ar.edu.itba.ati.controller.tabs;

import ar.edu.itba.ati.events.pictures.ShowPictureEvent;
import ar.edu.itba.ati.events.side_menu.ResetParametersEvent;
import ar.edu.itba.ati.io.Pictures;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.*;
import ar.edu.itba.ati.model.transformations.noise.ExponentialNoise;
import ar.edu.itba.ati.model.transformations.noise.GaussianNoise;
import ar.edu.itba.ati.model.transformations.noise.RayleighNoise;
import ar.edu.itba.ati.model.transformations.noise.SaltAndPepperNoise;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.*;
import ar.edu.itba.ati.services.PictureService;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.BiFunction;

public class SideTab1Controller implements SideTabController{

    protected final EventBus eventBus;
    protected final PictureService pictureService;

    @FXML
    private ScrollPane sideTabView1;

    @FXML
    private Button contrastButton;
    @FXML
    private Button equalizationButton;

    @FXML
    public TextField gammaVal;

    // Noise
    @FXML
    public TextField densityVal;
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
            applyTransformation(new PictureTransformer() {
                @Override
                public <T> void transform(Picture<T> picture) {
                    picture.normalize();
                    picture.mapPixelByPixel(p -> (double) p > value ? 255.0 : 0.0);
                }
            });
        } catch (NumberFormatException e){
            return;
        }
    }

    @FXML
    private void negative() {
        applyTransformation(new Negative());
    }

    @FXML
    private void dynamicRange() {
        applyTransformation(new DynamicRange());
    }

    @FXML
    private void gamma() {
        try {
            Double value = Double.valueOf(gammaVal.getText());
            if(value < 0) {
                return;
            }
            applyTransformation(new Gamma(value));
        } catch (NumberFormatException e){
            return;
        }
    }

    @FXML
    private void gaussNoise() {
        try {
            Double value = Double.valueOf(gaussVal.getText());
            Double density = Double.valueOf(densityVal.getText());
            if(value < 0 || density < 0 || density > 1) {
                return;
            }
            applyTransformation(new GaussianNoise(density,0.0, value));
        } catch (NumberFormatException e){
            return;
        }
    }

    @FXML
    private void exponentialNoise() {
        try {
            Double value = Double.valueOf(exponentialVal.getText());
            Double density = Double.valueOf(densityVal.getText());
            if(value < 0 || density < 0 || density > 1) {
                return;
            }
            applyTransformation(new ExponentialNoise(density, value));
        } catch (NumberFormatException e){
            return;
        }
    }

    @FXML
    private void rayleighNoise() {
        try {
            Double value = Double.valueOf(rayleighVal.getText());
            Double density = Double.valueOf(densityVal.getText());
            if(value < 0 || density < 0 || density > 1) {
                return;
            }
            applyTransformation(new RayleighNoise(density, value));
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
        applyTransformation(new Contrast());
    }

    @FXML
    private void equalization() {
        applyTransformation(new Equalization());
    }

    private void twoPictureOperation(BiFunction<Double,Double,Double> bf) {
        Picture otherPicture = choosePicture();

        if(otherPicture == null){
            return;
        }

        applyTransformation(new PictureTransformer() {
            @Override
            public <T> void transform(Picture<T> picture) {
                picture.mapPixelByPixel(bf, otherPicture);
            }
        });
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

    @Subscribe
    private void buttonVisibility(ShowPictureEvent event){
        if(pictureService.getPictureType() == BufferedImage.TYPE_BYTE_GRAY){
            contrastButton.setManaged(true);
            contrastButton.setVisible(true);
            equalizationButton.setManaged(true);
            equalizationButton.setVisible(true);
        } else {
            contrastButton.setManaged(false);
            contrastButton.setVisible(false);
            equalizationButton.setManaged(false);
            equalizationButton.setVisible(false);
        }
    }

    @Override
    public void reset(ResetParametersEvent event) {
        gammaVal.setText("");
        densityVal.setText("");
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
