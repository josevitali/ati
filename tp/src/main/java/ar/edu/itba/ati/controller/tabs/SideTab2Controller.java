package ar.edu.itba.ati.controller.tabs;

import ar.edu.itba.ati.events.pictures.ShowPictureEvent;
import ar.edu.itba.ati.events.side_menu.ResetParametersEvent;
import ar.edu.itba.ati.model.transformations.PictureTransformer;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.*;
import ar.edu.itba.ati.services.PictureService;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import javafx.fxml.FXML;

import java.util.List;

public class SideTab2Controller implements SideTabController {

    protected final EventBus eventBus;
    protected final PictureService pictureService;

    @Inject
    public SideTab2Controller(final EventBus eventBus, final PictureService pictureService){
        this.eventBus = eventBus;
        this.pictureService = pictureService;
    }

    protected void applyTransformation(PictureTransformer transformer){
        pictureService.applyTransformation(transformer);
        eventBus.post(new ShowPictureEvent());
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
        applyTransformation(new LaplaceTransformation(Masks.LAPLACE));
    }

    @FXML
    private void gaussianLaplaceMethod(){
        //TODO deshardcodear
        applyTransformation(new GaussianLaplaceTransformation(1, 3));
    }

    @Override
    public void reset(ResetParametersEvent event) {

    }

}
