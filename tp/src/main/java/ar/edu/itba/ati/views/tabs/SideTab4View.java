package ar.edu.itba.ati.views.tabs;

import com.google.common.eventbus.EventBus;
import com.google.common.io.Resources;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;

import java.io.IOException;

import static ar.edu.itba.ati.App.INJECTOR;

public class SideTab4View extends ScrollPane {
    public SideTab4View() {
        FXMLLoader fxmLoader = new FXMLLoader(Resources.getResource("fxml/tabs/side_tab_4.fxml"));
        fxmLoader.setRoot(this);
        fxmLoader.setControllerFactory(clazz -> {
            final Object controller = INJECTOR.getInstance(clazz);
            INJECTOR.getInstance(EventBus.class).register(controller);
            return controller;
        });
        try {
            fxmLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
