package ar.edu.itba.ati.controller;

import ar.edu.itba.ati.events.pictures.ShowPictureEvent;
import ar.edu.itba.ati.events.side_menu.ShowSideMenuEvent;
import ar.edu.itba.ati.events.toolbar.ShowToolbarEvent;
import ar.edu.itba.ati.io.Pictures;
import ar.edu.itba.ati.model.Picture;
import ar.edu.itba.ati.services.PictureService;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public class MenuBarController {

    private EventBus eventBus;
    private PictureService pictureService;

    public MenuBar menuBar;

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
            throw new IllegalStateException("Could not find file.");
        }

        Picture picture = null;
        Picture auxPicture = null;
        try {
            picture = Pictures.getPicture(file);
            auxPicture = Pictures.getPicture(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        pictureService.setPicture(picture);
        pictureService.setAuxPicture(auxPicture);
        pictureService.setFile(file);

        eventBus.post(new ShowPictureEvent());
        eventBus.post(new ShowToolbarEvent());
        eventBus.post(new ShowSideMenuEvent());
    }

    @FXML
    protected void handleSaveAsAction(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Text");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(
                        "Bmp files",
                        "*.bmp"));
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