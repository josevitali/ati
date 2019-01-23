package ar.edu.itba.ati.controller;

import ar.edu.itba.ati.events.pictures.AverageEvent;
import ar.edu.itba.ati.events.pictures.CropEvent;
import ar.edu.itba.ati.events.pictures.ShowPictureEvent;
import ar.edu.itba.ati.events.toolbar.ShowToolbarEvent;
import ar.edu.itba.ati.model.histograms.Histogram;
import ar.edu.itba.ati.model.transformations.GreyColorToggle;
import ar.edu.itba.ati.services.PictureService;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ToolbarController {

    public EventBus eventBus;
    private PictureService pictureService;
    public ToolBar toolbar;

    public Button cropMenuItem;
    public Button newWindow;
    public Button averagePixel;

    @Inject
    public ToolbarController(final EventBus eventBus, PictureService pictureService){
        this.eventBus = eventBus;
        this.pictureService = pictureService;
    }

    @FXML
    @Subscribe
    public void showToolBar(ShowToolbarEvent showToolbarEvent){
        toolbar.setVisible(true);
    }

    @FXML
    private void crop(){
        eventBus.post(new CropEvent());
    }

    @FXML
    private void average(){
       eventBus.post(new AverageEvent());
    }

    @FXML
    private void greyColorTransform() {
        pictureService.applyTransformation(new GreyColorToggle());
        eventBus.post(new ShowPictureEvent());
    }

    @FXML
    private void openPictureInNewWindow(){
        ImageView iv = new ImageView();
        iv.setImage(SwingFXUtils.toFXImage(pictureService.getPicture().getNormalizedClone().toBufferedImage(), null));
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(iv);

        newWindow(iv, "Image snapshot", null);
    }

    @FXML
    private void showHistogram(){
        Histogram histogram = pictureService.getPicture().getHistogram();

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.observableArrayList(histogram.getCategories()));

        NumberAxis yAxis = new NumberAxis();
        ObservableList<BarChart.Series> barChartData = FXCollections.observableArrayList();
        addSeries(barChartData, histogram.getCategories(), histogram.getSeries());

        BarChart chart = new BarChart(xAxis, yAxis, barChartData);

        List<String> stylesheets = new ArrayList<>();
        stylesheets.add("style/histogram.css");

        newWindow(chart, "Histogram", stylesheets);
    }

    @FXML
    private void undo(){
        pictureService.undo();
        eventBus.post(new ShowPictureEvent());
    }

    @FXML
    private void redo(){
        pictureService.redo();
        eventBus.post(new ShowPictureEvent());
    }

    private void addSeries(ObservableList<BarChart.Series> barChartData, String[] categories, Map<String,double[]> series){
        for(Map.Entry<String,double[]> entry : series.entrySet()){
            ObservableList<BarChart.Data> data = FXCollections.observableArrayList();
            for(int i = 0; i < categories.length; i++){
                data.add(new BarChart.Data<>(categories[i],entry.getValue()[i]));
            }
            BarChart.Series aSeries = new BarChart.Series(entry.getKey(),data);
            barChartData.add(aSeries);
        }
    }

    private void newWindow(Node node, String title, List<String> stylesheets){
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(node);
        Scene secondScene = new Scene(secondaryLayout);
        Stage newWindow = new Stage();
        newWindow.setTitle(title);
        newWindow.setScene(secondScene);

        if(stylesheets != null){
            stylesheets.forEach(s -> secondScene.getStylesheets().add(s));
        }

        newWindow.show();
    }


}
