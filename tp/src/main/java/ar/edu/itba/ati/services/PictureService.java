package ar.edu.itba.ati.services;

import ar.edu.itba.ati.model.Picture;

import java.io.File;

public class PictureService {

    private Picture picture = null;
    private Picture auxPicture = null;
    private File file = null;

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public Picture getAuxPicture() {
        return auxPicture;
    }

    public void setAuxPicture(Picture auxPicture) {
        this.auxPicture = auxPicture;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void switchPictures(){
        Picture aux = picture;
        picture = auxPicture;
        auxPicture = aux;
    }
}
