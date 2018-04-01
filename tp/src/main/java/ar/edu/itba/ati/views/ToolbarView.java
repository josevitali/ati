package ar.edu.itba.ati.views;

import com.google.common.eventbus.EventBus;
import com.google.common.io.Resources;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ToolBar;

import java.io.IOException;

import static ar.edu.itba.ati.App.INJECTOR;

public class ToolbarView extends ToolBar{
    public ToolbarView(){
        FXMLLoader fxmLoader = new FXMLLoader(Resources.getResource("fxml/toolbar.fxml"));
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
