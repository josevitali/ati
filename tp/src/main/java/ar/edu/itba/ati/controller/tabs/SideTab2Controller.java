package ar.edu.itba.ati.controller.tabs;

import ar.edu.itba.ati.events.pictures.ShowPictureEvent;
import ar.edu.itba.ati.events.side_menu.ResetParametersEvent;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.GradientMethodTransformation;
import ar.edu.itba.ati.model.transformations.PictureTransformer;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.Mask;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.SlidingWindowWithMask;
import ar.edu.itba.ati.services.PictureService;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;

import java.util.ArrayList;
import java.util.List;

public class SideTab2Controller implements SideTabController {

    private final EventBus eventBus;
    private final PictureService pictureService;

    @FXML
    private ScrollPane sideTabView2;

    @Inject
    public SideTab2Controller(final EventBus eventBus, final PictureService pictureService) {
        this.eventBus = eventBus;
        this.pictureService = pictureService;
    }

    @FXML
    private void prewittGradientMethod(){
        Double[][] prewittHorizontal = new Double[][]{{-1.0, -1.0, -1.0},{0.0, 0.0, 0.0},{1.0, 1.0, 1.0}};
        Double[][] prewittVertical = new Double[][]{{-1.0, 0.0, 1.0},{-1.0, 0.0, 1.0},{-1.0, 0.0, 1.0}};
        List<Mask> masks = new ArrayList();
        masks.add(new Mask(prewittHorizontal));
        masks.add(new Mask(prewittVertical));

        applyTransformation(new GradientMethodTransformation(masks));
    }

    @FXML
    private void sobelGradientMethod(){
        Double[][] sobelHorizontal = new Double[][]{{-1.0, -2.0, -1.0},{0.0, 0.0, 0.0},{1.0, 2.0, 1.0}};
        Double[][] sobelVertical = new Double[][]{{-1.0, 0.0, 1.0},{-2.0, 0.0, 2.0},{-1.0, 0.0, 1.0}};
        List<Mask> masks = new ArrayList();
        masks.add(new Mask(sobelHorizontal));
        masks.add(new Mask(sobelVertical));

        applyTransformation(new GradientMethodTransformation(masks));
    }

    private void applyTransformation(PictureTransformer transformer){
        pictureService.applyTransformation(transformer);
        eventBus.post(new ShowPictureEvent());
    }

    @Override
    public void reset(ResetParametersEvent event) {

    }

}
