package ar.edu.itba.ati.controller.tabs;

import ar.edu.itba.ati.events.side_menu.ResetParametersEvent;
import com.google.common.eventbus.Subscribe;

public interface SideTabController {

    /**
     * Resets the value of every parameter of every element in the tab.
     */
    @Subscribe
    void reset(ResetParametersEvent event);
}
