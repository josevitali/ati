package ar.edu.itba.ati.services;

import ar.edu.itba.ati.model.pictures.Picture;

import java.io.File;

public class PictureService {

    private Picture picture = null;
    private File file = null;

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void cropPicture(int x0, int x1, int y0, int y1){picture.crop(x0, x1, y0, y1);}

}
