package ar.edu.itba.ati.controller.tabs;

import ar.edu.itba.ati.events.pictures.ShowPictureEvent;
import ar.edu.itba.ati.events.side_menu.ResetParametersEvent;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.PictureTransformer;
import ar.edu.itba.ati.model.transformations.borderDetection.*;
import ar.edu.itba.ati.model.transformations.diffusion.IsotropicDiffusion;
import ar.edu.itba.ati.model.transformations.diffusion.LeclercDiffusion;
import ar.edu.itba.ati.model.transformations.diffusion.LorentzDiffusion;
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

    private final EventBus eventBus;
    private final PictureService pictureService;

    @FXML
    private TextField laplaceThresholdVal;
    @FXML
    private TextField gaussianLaplaceThresholdVal;
    @FXML
    public TextField gaussianLaplaceSigmaVal;
    @FXML
    public TextField isotropicIterationsVal;
    @FXML
    public TextField leclercIterationsVal;
    @FXML
    public TextField leclercSigmaVal;
    @FXML
    public TextField lorentzIterationsVal;
    @FXML
    public TextField lorentzSigmaVal;

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
        List<Mask> masks = Masks.rotate(Masks.PREWITT, new int[]{Masks.TOP, Masks.RIGHT});
        applyTransformation(new GradientMethodTransformation(masks));
    }

    @FXML
    private void sobelGradientMethod(){
        List<Mask> masks = Masks.rotate(Masks.SOBEL, new int[]{Masks.TOP, Masks.RIGHT});
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
    private void isotropicDiffusion(){
        Integer value = Integer.valueOf(isotropicIterationsVal.getText());
        if(value < 1) {
            return;
        }
        applyTransformation(new IsotropicDiffusion(value));
    }

    @FXML
    private void leclercDiffusion() {
        Integer iterations = Integer.valueOf(leclercIterationsVal.getText());
        Double sigma = Double.valueOf(leclercSigmaVal.getText());
        if(iterations < 0 || sigma < 0) {
            return;
        }
        applyTransformation(new LeclercDiffusion(iterations, sigma));
    }

    @FXML
    private void lorentzDiffusion() {
        Integer iterations = Integer.valueOf(lorentzIterationsVal.getText());
        Double sigma = Double.valueOf(lorentzSigmaVal.getText());
        if(iterations < 0 || sigma < 0) {
            return;
        }
        applyTransformation(new LorentzDiffusion(iterations, sigma));
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
        applyTransformation(new PictureTransformer() {
            @Override
            public <T,R> Picture transform(Picture<T> picture) {
                picture.mapPixelByPixel(px -> px < t ? 0.0 : 255.0);
                return picture;
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
        laplaceThresholdVal.setText("");
        gaussianLaplaceThresholdVal.setText("");
        gaussianLaplaceSigmaVal.setText("");
        isotropicIterationsVal.setText("");
        leclercIterationsVal.setText("");
        leclercSigmaVal.setText("");
        lorentzIterationsVal.setText("");
        lorentzSigmaVal.setText("");
    }

}
