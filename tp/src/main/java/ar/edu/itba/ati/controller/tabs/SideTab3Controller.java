package ar.edu.itba.ati.controller.tabs;

import ar.edu.itba.ati.events.side_menu.ResetParametersEvent;
import ar.edu.itba.ati.services.PictureService;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

public class SideTab3Controller implements SideTabController {

    private final EventBus eventBus;
    private final PictureService pictureService;


    @Inject
    public SideTab3Controller(final EventBus eventBus, final PictureService pictureService){
        this.eventBus = eventBus;
        this.pictureService = pictureService;
    }

    @Override
    public void reset(ResetParametersEvent event) {

    }
}
