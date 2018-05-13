package ar.edu.itba.ati.controller.tabs;

import ar.edu.itba.ati.events.pictures.ShowPictureEvent;
import ar.edu.itba.ati.events.side_menu.ResetParametersEvent;
import ar.edu.itba.ati.model.transformations.PictureTransformer;
import ar.edu.itba.ati.model.transformations.borderDetection.CannyDetector;
import ar.edu.itba.ati.model.transformations.borderDetection.SusanDetector;
import ar.edu.itba.ati.services.PictureService;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SideTab3Controller implements SideTabController {

    private final EventBus eventBus;
    private final PictureService pictureService;
    @FXML
    private TextField cannySigma;


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
    private void cannyDetector() {
        Double value = Double.valueOf(cannySigma.getText());
        if(value < 0.0) {
            return;
        }

        CannyDetector cannyDetector = new CannyDetector(value);
        applyTransformation(cannyDetector);
    }

    private void applyTransformation(PictureTransformer transformer){
        pictureService.applyTransformation(transformer);
        eventBus.post(new ShowPictureEvent());
    }

    @Override
    public void reset(ResetParametersEvent event) {
    }
}
