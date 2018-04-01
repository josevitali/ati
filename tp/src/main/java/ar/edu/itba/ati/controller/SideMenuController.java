package ar.edu.itba.ati.controller;

import ar.edu.itba.ati.events.side_menu.ShowSideMenuEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;

public class SideMenuController {

    public EventBus eventBus;
    public TabPane sideMenu;

    @Inject
    public SideMenuController(final EventBus eventBus){
        this.eventBus = eventBus;
    }

    @FXML
    @Subscribe
    public void showSideMenu(ShowSideMenuEvent showSideMenuEvent){
        sideMenu.setVisible(true);
    }

}
