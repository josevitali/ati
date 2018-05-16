package ar.edu.itba.ati.controller.tabs;

import ar.edu.itba.ati.events.returnValues.ReturnEndsEvent;
import ar.edu.itba.ati.events.pictures.GetEndsEvent;
import ar.edu.itba.ati.events.pictures.ShowPictureEvent;
import ar.edu.itba.ati.events.side_menu.ResetParametersEvent;
import ar.edu.itba.ati.io.Pictures;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.PictureTransformer;
import ar.edu.itba.ati.model.transformations.borderDetection.PixelExchangeMethod;
import ar.edu.itba.ati.model.transformations.borderDetection.SusanDetector;
import ar.edu.itba.ati.services.PictureService;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SideTab3Controller implements SideTabController {

    private final EventBus eventBus;
    private final PictureService pictureService;
    @FXML
    private ScrollPane sideTabView3;

    private int[] ends;

    private List<Picture> video;
    private int actualFrame;
    private boolean playing = false;

    @FXML
    private Button susanButton;
    @FXML
    private Button priorFrameButton;
    @FXML
    private Button nextFrameButton;
    @FXML
    private Button toFirstFrameButton;
    @FXML
    private Button playButton;
    @FXML
    private Button stopButton;

    @FXML
    private TextField pixelExchangeIterationsVal;
    @FXML
    private TextField pixelExchangeRestrictionVal;

    @Inject
    public SideTab3Controller(final EventBus eventBus, final PictureService pictureService){
        this.eventBus = eventBus;
        this.pictureService = pictureService;
        this.video = new ArrayList();
        this.actualFrame = 0;
    }

    @FXML
    private void susanDetector() {
        SusanDetector susanDetector = new SusanDetector(1.0);
        applyTransformation(susanDetector);
    }

    @FXML
    private void pixelExchangeStatic() {
        eventBus.post(new GetEndsEvent());
        int iterations = Integer.valueOf(pixelExchangeIterationsVal.getText());
        double restriction = Double.valueOf(pixelExchangeRestrictionVal.getText());
        if(iterations < 0 || restriction < 0 || restriction > 1) {
            return;
        }
        PixelExchangeMethod pixelExchange = new PixelExchangeMethod(ends[0],ends[1],ends[2],ends[3],iterations, restriction);
        applyTransformation(pixelExchange);
    }

    @FXML
    private void getVideo(){
        FileChooser fileChooser = new FileChooser();
        List<File> files = fileChooser.showOpenMultipleDialog(sideTabView3.getScene().getWindow());
        video.clear();
        actualFrame = 0;

        if(files == null){
            return;
        }

        files.forEach(f -> {
            try {
                video.add(Pictures.getPicture(f));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        pictureService.setFile(files.get(0));
        pictureService.setPicture(video.get(0));
        eventBus.post(new ShowPictureEvent());
        setVideoButtonsVisibility(true);
    }

    @FXML
    private void pixelExchangeVideo() {
        eventBus.post(new GetEndsEvent());
        int iterations = Integer.valueOf(pixelExchangeIterationsVal.getText());
        double restriction = Double.valueOf(pixelExchangeRestrictionVal.getText());
        if(iterations < 0 || restriction < 0 || restriction > 1) {
            return;
        }
        PixelExchangeMethod pixelExchange = new PixelExchangeMethod(ends[0],ends[1],ends[2],ends[3],iterations, restriction);

        for(Picture picture : video){
            pixelExchange.transform(picture);
            pixelExchange = new PixelExchangeMethod(pixelExchange);
        }

        eventBus.post(new ShowPictureEvent());
    }

    @FXML
    private void next() {
        applyTransformation(new PictureTransformer() {
            @Override
            public <T, R> Picture<R> transform(Picture<T> picture) {
                if(actualFrame == video.size() - 1){
                    actualFrame = -1;
                }
                return video.get(++actualFrame);
            }
        });
    }

    @FXML
    private void prior() {
        applyTransformation(new PictureTransformer() {
            @Override
            public <T, R> Picture<R> transform(Picture<T> picture) {
                if(actualFrame == 0){
                    actualFrame = video.size();
                }
                return video.get(--actualFrame);
            }
        });
    }

    @FXML
    private void toFirstFrame() {
        applyTransformation(new PictureTransformer() {
            @Override
            public <T, R> Picture<R> transform(Picture<T> picture) {
                actualFrame = 0;
                return video.get(0);
            }
        });
    }

    @FXML
    private void play() {
        // FIXME
        playing = true;
        while (playing){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            next();
        }
    }

    @FXML
    private void stop() {
        playing = false;
    }

    private void applyTransformation(PictureTransformer transformer){
        pictureService.applyTransformation(transformer);
        eventBus.post(new ShowPictureEvent());
    }

    @Subscribe
    private void buttonVisibility(ShowPictureEvent event){
        if(pictureService.getPictureType() == BufferedImage.TYPE_BYTE_GRAY){
            susanButton.setManaged(true);
            susanButton.setVisible(true);
        } else {
            susanButton.setManaged(false);
            susanButton.setVisible(false);
        }
    }

    @Override
    public void reset(ResetParametersEvent event) {
        pixelExchangeIterationsVal.setText("");
        pixelExchangeRestrictionVal.setText("");
        setVideoButtonsVisibility(false);
        playing = false;
    }

    private void setVideoButtonsVisibility(boolean b){
        priorFrameButton.setManaged(b);
        priorFrameButton.setVisible(b);
        nextFrameButton.setManaged(b);
        nextFrameButton.setVisible(b);
        playButton.setManaged(b);
        playButton.setVisible(b);
        toFirstFrameButton.setManaged(b);
        toFirstFrameButton.setVisible(b);
        stopButton.setManaged(b);
        stopButton.setVisible(b);
    }

    @FXML
    @Subscribe
    public void setEnds(ReturnEndsEvent returnEvent){
        ends = returnEvent.getReturnValue();
    }
}
