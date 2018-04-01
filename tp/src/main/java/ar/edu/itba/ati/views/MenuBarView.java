package ar.edu.itba.ati.views;


import com.google.common.eventbus.EventBus;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;
import static ar.edu.itba.ati.App.INJECTOR;

public class MenuBarView extends VBox {

    public MenuBarView() throws Exception {
        FXMLLoader fxmLoader = new FXMLLoader(Resources.getResource("fxml/menu_bar.fxml"));
        fxmLoader.setRoot(this);
        fxmLoader.setControllerFactory(clazz -> {
            final Object controller = INJECTOR.getInstance(clazz);
            INJECTOR.getInstance(EventBus.class).register(controller);
            return controller;
        });
        try {
            fxmLoader.load();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
