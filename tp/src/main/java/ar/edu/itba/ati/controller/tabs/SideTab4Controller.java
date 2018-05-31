package ar.edu.itba.ati.controller.tabs;

import ar.edu.itba.ati.events.pictures.ShowPictureEvent;
import ar.edu.itba.ati.events.side_menu.ResetParametersEvent;
import ar.edu.itba.ati.io.Pictures;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.PictureTransformer;
import ar.edu.itba.ati.model.transformations.Sift;
import ar.edu.itba.ati.model.transformations.cornerDetection.HarrisCornerDetector;
import ar.edu.itba.ati.services.PictureService;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.function.BiFunction;

public class SideTab4Controller implements SideTabController {

    private final EventBus eventBus;
    private final PictureService pictureService;

    @FXML
    private ScrollPane sideTabView4;

    @FXML
    private TextField harrisVal;

    @Inject
    public SideTab4Controller(final EventBus eventBus, final PictureService pictureService){
        this.eventBus = eventBus;
        this.pictureService = pictureService;
    }

    @FXML
    private void harris() {
        double threshold = Double.valueOf(harrisVal.getText());
        if(threshold < 0 || threshold > 1) {
            return;
        }
        HarrisCornerDetector harrisCornerDetector = new HarrisCornerDetector(threshold);
        applyTransformation(harrisCornerDetector);
    }

    @FXML
    private void sift() {
        Sift sift = new Sift(choosePicture());
        applyTransformation(sift);
    }

    private Picture choosePicture(){
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose picture");
        File file = fc.showOpenDialog(sideTabView4.getScene().getWindow());

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



    @Override
    public void reset(ResetParametersEvent event) {
        harrisVal.setText("");
    }

    private void applyTransformation(PictureTransformer transformer){
        pictureService.applyTransformation(transformer);
        eventBus.post(new ShowPictureEvent());
    }
}
