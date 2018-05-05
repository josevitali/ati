package ar.edu.itba.ati.services;

import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.PictureTransformer;
import ar.edu.itba.ati.model.transformations.borderDetection.SusanDetector;

import java.io.File;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PictureService {

    private Picture picture = null;
    private File file = null;

    private Deque<Picture> undoPictures = new LinkedList<>();
    private Deque<Picture> redoPictures = new LinkedList<>();
    private static final int UNDO_AMOUNT = 15;

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
        undoPictures.clear();
        redoPictures.clear();
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void cropPicture(final int minRow, final int maxRow, final int minCol, final int maxCol){
        pushUndo();
        picture.crop(minRow, maxRow, minCol, maxCol);
    }

    public void average(final int minRow, final int maxRow, final int minCol, final int maxCol){
        System.out.println(picture.getNormalizedClone().getAverageColor(minRow, minCol, maxRow, maxCol));
    }

    public void applyTransformation(PictureTransformer transformer){
        pushUndo();
        redoPictures.clear();
        transformer.transform(picture);
        //TODO: remove
        setPicture(((SusanDetector) transformer).transformedPicture);
    }

    public int getPictureType() {
        return picture.getType();
    }

    public void undo(){
        if(!undoPictures.isEmpty()){
            pushRedo();
            picture = undoPictures.pop();
        }
    }

    public void redo(){
        if(!redoPictures.isEmpty()){
            pushUndo();
            picture = redoPictures.pop();
        }
    }

    /**
     * Pushes picture into undoPictures.
     * Should be called before applying a transformation.
     */
    private void pushUndo(){
        if(undoPictures.size() == UNDO_AMOUNT){
            undoPictures.pollLast();
        }
        undoPictures.push(picture.getClone());
    }

    private void pushRedo(){
        redoPictures.push(picture.getClone());
    }

}
