package ar.edu.itba.ati;

import ar.edu.itba.ati.services.PictureService;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
import java.net.URL;

public class App extends Application{

    public static Stage primaryStage;

    public static void main( String[] args ){
        launch(args);
    }

    public static Injector INJECTOR = Guice.createInjector(new AbstractModule() {
        @Override
        protected void configure() {
            bind(EventBus.class).asEagerSingleton();
            bind(PictureService.class).asEagerSingleton();
        }
    });

    public void start(final Stage primaryStage) throws Exception {
        final Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/app.fxml"));
        primaryStage.setTitle("ATI Image Editor");
        primaryStage.getIcons().add(new Image("icon/icon.jpg"));

        Class util = Class.forName("com.apple.eawt.Application");
        Method getApplication = util.getMethod("getApplication", new Class[0]);
        Object application = getApplication.invoke(util);
        Class params[] = new Class[1];
        params[0] = java.awt.Image.class;
        Method setDockIconImage = util.getMethod("setDockIconImage", params);
        URL url = App.class.getClassLoader().getResource("icon/icon.jpg");
        java.awt.Image image = Toolkit.getDefaultToolkit().getImage(url);
        setDockIconImage.invoke(application, image);
        
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        this.primaryStage = primaryStage;

    }
}


