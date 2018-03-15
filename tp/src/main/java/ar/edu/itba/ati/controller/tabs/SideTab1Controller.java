package ar.edu.itba.ati.controller.tabs;

import ar.edu.itba.ati.events.pictures.ShowPictureEvent;
import ar.edu.itba.ati.services.PictureService;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import javafx.fxml.FXML;

import java.util.function.Function;

public class SideTab1Controller {

    private final EventBus eventBus;
    private final PictureService pictureService;

    @Inject
    public SideTab1Controller(final EventBus eventBus, final PictureService pictureService){
        this.eventBus = eventBus;
        this.pictureService = pictureService;
    }

    @FXML
    protected void negative(){
        pictureService.getPicture().mapElementByElement(new Function<Double, Double>() {
            @Override
            public Double apply(Double aDouble) {
                return 255 - aDouble;
            }
        });
        eventBus.post(new ShowPictureEvent());
    }
}
