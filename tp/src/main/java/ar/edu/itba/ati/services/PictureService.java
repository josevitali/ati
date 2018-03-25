package ar.edu.itba.ati.services;

import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.PictureTransformer;

import java.io.File;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PictureService {

    private Picture picture = null;
    private File file = null;

    public Picture getPicture() {
        return picture.getClone();
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

    public void normalize(){
        picture.normalize();
    }

    public void cropPicture(final int minRow, final int maxRow, final int minCol, final int maxCol){
        picture.crop(minRow, maxRow, minCol, maxCol);
    }

    public void average(final int minRow, final int maxRow, final int minCol, final int maxCol){
        System.out.println(picture.getNormalizedClone().getAverageColor(minRow, minCol, maxRow, maxCol));
    }

    public void applyTransformation(PictureTransformer transformer){
        transformer.transform(picture);
    }

    public void mapPixelByPixel(Function<Double,Double> function){
        picture.mapPixelByPixel(function);
    }

    public void mapPixelByPixel(BiFunction<Double,Double,Double> biFunction, Picture otherPicture){
        picture.mapPixelByPixel(biFunction, otherPicture);
    }

}
