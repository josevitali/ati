package ar.edu.itba.ati.model.transformations.slidingWindows;

import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.PictureTransformer;

import java.util.function.Function;

public abstract class SlidingWindowTransformation<T> implements PictureTransformer {

    private final int windowHeight;
    private final int windowWidth;
    private final Function<Double[][],Double> f;

    public SlidingWindowTransformation(int windowHeight, int windowWidth, Function<Double[][], Double> f) {
        this.windowHeight = windowHeight;
        this.windowWidth = windowWidth;
        this.f = f;
    }

    @Override
    public <T> void transform(Picture<T> picture) {
        T[][] auxMatrix = (T[][]) new Object[1 + picture.getHeight() - windowHeight][1 + picture.getWidth() - windowWidth];

        for(int row = 0; row < auxMatrix.length; row++){
            for(int col = 0; col < auxMatrix[0].length; col++){
                auxMatrix[row][col] = picture.subMatrixOperation(row, col, windowHeight, windowWidth, f);
            }
        }

        finalizeTransformation(picture, auxMatrix);
    }

    protected abstract <T> void finalizeTransformation(Picture<T> picture, T[][] transformedMatrix);
}
