package ar.edu.itba.ati;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.*;

/**
 * Hello world!
 *
 */
public class App extends Application{

    public static void main( String[] args ){
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Un título");
        Button button = new Button("soy un botón");

        StackPane layout = new StackPane();
        layout.getChildren().add(button);

        Scene scene = new Scene(layout, 200, 300);
        primaryStage.setScene(scene);
        primaryStage.show();


    }
}
