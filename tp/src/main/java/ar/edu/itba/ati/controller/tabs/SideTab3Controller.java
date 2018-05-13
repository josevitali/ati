package ar.edu.itba.ati.controller.tabs;

import ar.edu.itba.ati.events.ReturnEvent;
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
import javafx.geometry.Bounds;

public class SideTab3Controller implements SideTabController {

    private final EventBus eventBus;
    private final PictureService pictureService;

    private int[] ends;


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
        PixelExchangeMethod pixelExchange = new PixelExchangeMethod(ends[0],ends[1],ends[2],ends[3],300);
        ends = null;
        applyTransformation(pixelExchange);
    }

    private void applyTransformation(PictureTransformer transformer){
        pictureService.applyTransformation(transformer);
        eventBus.post(new ShowPictureEvent());
    }

    @Override
    public void reset(ResetParametersEvent event) {
    }

    @FXML
    @Subscribe
    public void setEnds(ReturnEvent returnEvent){
        ends = (int[]) returnEvent.getReturnValue();
    }
}
