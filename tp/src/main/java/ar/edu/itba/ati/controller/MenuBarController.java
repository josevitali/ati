package ar.edu.itba.ati.controller;

import ar.edu.itba.ati.events.pictures.ShowPictureEvent;
import ar.edu.itba.ati.events.side_menu.ResetParametersEvent;
import ar.edu.itba.ati.events.side_menu.ShowSideMenuEvent;
import ar.edu.itba.ati.events.toolbar.ShowToolbarEvent;
import ar.edu.itba.ati.io.Pictures;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.services.PictureService;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

public class MenuBarController {

    private EventBus eventBus;
    private PictureService pictureService;
    @FXML
    private MenuBar menuBar;


    @Inject
    public MenuBarController(final EventBus eventBus, final PictureService pictureService){
        this.eventBus = eventBus;
        this.pictureService = pictureService;
    }


    @FXML
    protected void handleOpenAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        File file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
        if(file == null){
            return;
        }

        Picture picture = null;
        try {
            picture = Pictures.getPicture(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        pictureService.setPicture(picture);
        pictureService.setFile(file);

        eventBus.post(new ShowPictureEvent());
        eventBus.post(new ShowToolbarEvent());
        eventBus.post(new ShowSideMenuEvent());
        eventBus.post(new ResetParametersEvent());
    }

    @FXML
    protected void handleSaveAsAction(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Text");
        String extension = FilenameUtils.getExtension(pictureService.getFile().getAbsolutePath());
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(
                        extension + " files",
                        "*." + extension));
        File file =
                fc.showSaveDialog(
                        menuBar.getScene().getWindow());
        if (file != null) {
            try {
                Pictures.save(pictureService.getPicture(), file);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            pictureService.setFile(file);
        }
    }

    @FXML
    protected void handleSaveAction(ActionEvent actionEvent){
        Picture picture = pictureService.getPicture();
        File file = pictureService.getFile();
        if (picture != null && file != null) {
            try {
                Pictures.save(picture, file);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    protected void handleQuit(ActionEvent event){
        System.exit(0);
    }

}
