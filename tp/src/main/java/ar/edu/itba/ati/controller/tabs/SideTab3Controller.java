package ar.edu.itba.ati.controller.tabs;

import ar.edu.itba.ati.events.returnValues.ReturnEndsEvent;
import ar.edu.itba.ati.events.pictures.GetEndsEvent;
import ar.edu.itba.ati.events.pictures.ShowPictureEvent;
import ar.edu.itba.ati.events.side_menu.ResetParametersEvent;
import ar.edu.itba.ati.model.transformations.PictureTransformer;
import ar.edu.itba.ati.model.transformations.borderDetection.PixelExchangeMethod;
import ar.edu.itba.ati.model.transformations.borderDetection.SusanDetector;
import ar.edu.itba.ati.services.PictureService;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.awt.image.BufferedImage;

public class SideTab3Controller implements SideTabController {

    private final EventBus eventBus;
    private final PictureService pictureService;

    private int[] ends;

    @FXML
    private Button susanButton;

    @FXML
    private TextField pixelExchangeIterationsVal;
    @FXML
    private TextField pixelExchangeRestrictionVal;

    @Inject
    public SideTab3Controller(final EventBus eventBus, final PictureService pictureService){
        this.eventBus = eventBus;
        this.pictureService = pictureService;
    }

    @FXML
    private void susanDetector() {
        SusanDetector susanDetector = new SusanDetector(1.0);
        applyTransformation(susanDetector);
    }

    @FXML
    private void pixelExchange() {
        eventBus.post(new GetEndsEvent());
        int iterations = Integer.valueOf(pixelExchangeIterationsVal.getText());
        double restriction = Double.valueOf(pixelExchangeRestrictionVal.getText());
        if(iterations < 0 || restriction < 0 || restriction > 1) {
            return;
        }
        PixelExchangeMethod pixelExchange = new PixelExchangeMethod(ends[0],ends[1],ends[2],ends[3],iterations, restriction);
        applyTransformation(pixelExchange);
    }

    private void applyTransformation(PictureTransformer transformer){
        pictureService.applyTransformation(transformer);
        eventBus.post(new ShowPictureEvent());
    }

    @Subscribe
    private void buttonVisibility(ShowPictureEvent event){
        if(pictureService.getPictureType() == BufferedImage.TYPE_BYTE_GRAY){
            susanButton.setManaged(true);
            susanButton.setVisible(true);
        } else {
            susanButton.setManaged(false);
            susanButton.setVisible(false);
        }
    }

    @Override
    public void reset(ResetParametersEvent event) {
    }

    @FXML
    @Subscribe
    public void setEnds(ReturnEndsEvent returnEvent){
        ends = (int[]) returnEvent.getReturnValue();
    }
}
