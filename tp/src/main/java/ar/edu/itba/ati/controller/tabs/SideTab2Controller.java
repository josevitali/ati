package ar.edu.itba.ati.controller.tabs;

import ar.edu.itba.ati.events.pictures.ShowPictureEvent;
import ar.edu.itba.ati.events.side_menu.ResetParametersEvent;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.PictureTransformer;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.*;
import ar.edu.itba.ati.model.transformations.threshold.GlobalThreshold;
import ar.edu.itba.ati.model.transformations.threshold.OtsuThreshold;
import ar.edu.itba.ati.model.transformations.threshold.ThresholdCriteria;
import ar.edu.itba.ati.services.PictureService;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.awt.image.BufferedImage;
import java.util.List;

public class SideTab2Controller implements SideTabController {

    protected final EventBus eventBus;
    protected final PictureService pictureService;

    @FXML
    private TextField laplaceThresholdVal;
    @FXML
    private TextField gaussianLaplaceThresholdVal;
    @FXML
    private TextField gaussianLaplaceSigmaVal;

    // Threshold
    @FXML
    private Label thresholdLabel;
    @FXML
    private Button globalThresholdButton;
    @FXML
    private Button otsuThresholdButton;

    @Inject
    public SideTab2Controller(final EventBus eventBus, final PictureService pictureService){
        this.eventBus = eventBus;
        this.pictureService = pictureService;
    }

    @FXML
    private void prewittGradientMethod(){
        List<Mask> masks = Masks.rotate(Masks.PREWITT, new int[]{Masks.TOP, Masks.DOWN});
        applyTransformation(new GradientMethodTransformation(masks));
    }

    @FXML
    private void sobelGradientMethod(){
        List<Mask> masks = Masks.rotate(Masks.SOBEL, new int[]{Masks.TOP, Masks.DOWN});
        applyTransformation(new GradientMethodTransformation(masks));
    }

    @FXML
    private void unnamedMaxMethod(){
        List<Mask> masks = Masks.rotate(new Mask(new Double[][]{{1.0, 1.0, 1.0},{1.0, -2.0, 1.0},{-1.0, -1.0, -1.0}})
                , Masks.ALL_DIRECTIONS);
        applyTransformation(new MaxMethodTransformation(masks));
    }

    @FXML
    private void kirshMaxMethod(){
        List<Mask> masks = Masks.rotate(Masks.KIRSH, Masks.ALL_DIRECTIONS);
        applyTransformation(new MaxMethodTransformation(masks));
    }

    @FXML
    private void prewittMaxMethod(){
        List<Mask> masks = Masks.rotate(Masks.PREWITT, Masks.ALL_DIRECTIONS);
        applyTransformation(new MaxMethodTransformation(masks));
    }

    @FXML
    private void sobelMaxMethod(){
        List<Mask> masks = Masks.rotate(Masks.SOBEL, Masks.ALL_DIRECTIONS);
        applyTransformation(new MaxMethodTransformation(masks));
    }

    @FXML
    private void laplaceMethod(){
        Double value = Double.valueOf(laplaceThresholdVal.getText());
        if(value > 255 || value < 0) {
            return;
        }
        applyTransformation(new LaplaceThresholdTransformation(Masks.LAPLACE, value));
    }

    @FXML
    private void simpleLaplaceMethod() {
        applyTransformation(new LaplaceTransformation(Masks.LAPLACE));
    }

    @FXML
    private void gaussianLaplaceMethod(){
        Double threshold = Double.valueOf(gaussianLaplaceThresholdVal.getText());
        Double sigma = Double.valueOf(gaussianLaplaceSigmaVal.getText());
        applyTransformation(new GaussianLaplaceTransformation(sigma, threshold));
    }

    @FXML
    private void globalThreshold(){
        applyThreshold(new GlobalThreshold());
    }

    @FXML
    private void otsuThreshold(){
        applyThreshold(new OtsuThreshold());
    }

    private void applyTransformation(PictureTransformer transformer){
        pictureService.applyTransformation(transformer);
        eventBus.post(new ShowPictureEvent());
    }

    private void applyThreshold(ThresholdCriteria thresholdCriteria){
        final double t = thresholdCriteria.getThreshold(pictureService.getPicture());
        System.out.println(t);
        applyTransformation(new PictureTransformer() {
            @Override
            public <T> void transform(Picture<T> picture) {
                picture.mapPixelByPixel(px -> px < t ? 0.0 : 255.0);
            }
        });
    }

    @Subscribe
    private void buttonVisibility(ShowPictureEvent event){
        if(pictureService.getPictureType() == BufferedImage.TYPE_BYTE_GRAY){
            thresholdLabel.setVisible(true);
            thresholdLabel.setVisible(true);
            globalThresholdButton.setManaged(true);
            globalThresholdButton.setVisible(true);
            otsuThresholdButton.setManaged(true);
            otsuThresholdButton.setVisible(true);
        } else {
            thresholdLabel.setManaged(false);
            thresholdLabel.setVisible(false);
            globalThresholdButton.setManaged(false);
            globalThresholdButton.setVisible(false);
            otsuThresholdButton.setManaged(false);
            otsuThresholdButton.setVisible(false);
        }
    }

    @Override
    public void reset(ResetParametersEvent event) {

    }

}
