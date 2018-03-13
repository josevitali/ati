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
import javafx.stage.Stage;

public class App extends Application{

    //TODO sacar cabezeada
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
        primaryStage.setScene(new Scene(root));
        primaryStage.setFullScreen(true);
        primaryStage.show();
        this.primaryStage = primaryStage;

    }
}


