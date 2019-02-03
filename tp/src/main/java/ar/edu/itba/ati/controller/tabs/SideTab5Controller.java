package ar.edu.itba.ati.controller.tabs;

import ar.edu.itba.ati.events.pictures.ShowPictureEvent;
import ar.edu.itba.ati.events.side_menu.ResetParametersEvent;
import ar.edu.itba.ati.model.transformations.PictureTransformer;
import ar.edu.itba.ati.model.transformations.licenseDetection.LicenseDetection;
import ar.edu.itba.ati.services.PictureService;
import ar.edu.itba.ati.utils.LicenseConstants;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class SideTab5Controller implements SideTabController {

    private final EventBus eventBus;
    private final PictureService pictureService;

    @FXML
    private TextField houghThreshold;

    @FXML
    private TextField houghDelta;

    @FXML
    private ChoiceBox licenseConstants;

    @Inject
    public SideTab5Controller(final EventBus eventBus, final PictureService pictureService) {
        this.eventBus = eventBus;
        this.pictureService = pictureService;
    }

    @FXML
    private void licenseDetection() {
        int rectangleThresholdVal = Integer.valueOf(houghThreshold.getText());
        double rectangleDeltaVal = Double.valueOf(houghDelta.getText());
        if(rectangleThresholdVal < 0 || rectangleThresholdVal > 100 || rectangleDeltaVal < 0) {
            return;
        }
        LicenseDetection licenseDetection = new LicenseDetection(rectangleThresholdVal, rectangleDeltaVal,
                LicenseConstants.valueOf(((String)licenseConstants.getValue()).toUpperCase()));
        applyTransformation(licenseDetection);
    }

    @Override
    public void reset(ResetParametersEvent event) {
        houghThreshold.setText("");
        houghDelta.setText("");
    }

    private void applyTransformation(PictureTransformer transformer){
        pictureService.applyTransformation(transformer);
        eventBus.post(new ShowPictureEvent());
    }
}
