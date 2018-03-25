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

    public void cropPicture(final int minRow, final int maxRow, final int minCol, final int maxCol){
        picture.crop(minRow, maxRow, minCol, maxCol);
    }

    public void average(final int minRow, final int maxRow, final int minCol, final int maxCol){
        System.out.println(picture.getNormalizedClone().getAverageColor(minRow, minCol, maxRow, maxCol));
    }

}
