package ar.edu.itba.ati.model.transformations.slidingWindows.withMask;

import ar.edu.itba.ati.model.pictures.Picture;

import java.util.function.BiFunction;

public class LaplaceTransformation extends SlidingWindowWithMask<Double> {

    protected BiFunction<Double, Double, Double> bf = new BiFunction<Double, Double, Double>() {
        @Override
        public Double apply(Double prevVal, Double actualVal) {
            if(prevVal*actualVal < 0){
                return 255.0;
            }
            return 0.0;

        }
    };

    private BiFunction<Double, Double, Double> unionFunction = new BiFunction<Double, Double, Double>() {
        @Override
        public Double apply(Double aDouble, Double aDouble2) {
            if(aDouble == 255.0 || aDouble2 == 255.0)
                return 255.0;
            return 0.0;
        }
    };

    public LaplaceTransformation(Mask mask) {
        super(mask);
    }

    @Override
    public <T> void transform(Picture<T> picture) {
        T[][] windowedMatrix = (T[][]) new Object[1 + picture.getHeight() - mask.getSize()][1 + picture.getWidth() - mask.getSize()];

        for(int row = 0; row < windowedMatrix.length; row++) {
            for (int col = 0; col < windowedMatrix[0].length; col++) {
                windowedMatrix[row][col] = picture.subMatrixOperation(row, col, this.mask.getSize(), mask.getSize(), f);
            }
        }

        T[][] rowMatrix = (T[][]) new Object[1 + picture.getHeight() - mask.getSize()][1 + picture.getWidth() - mask.getSize()];

        //Find sign changes in matrix by rows
        T prevElem = windowedMatrix[0][0];
        for (int i = 0; i < windowedMatrix.length; i++) {
            for (int j = 0; j < windowedMatrix[0].length; j++) {
                rowMatrix[i][j] = picture.evaluatePixel(bf, prevElem, windowedMatrix[i][j]);
                prevElem = windowedMatrix[i][j];
            }
        }

        //Find sign changes in matrix by cols
        prevElem = windowedMatrix[0][0];
        for (int i = 0; i < windowedMatrix[0].length; i++) {
            for (int j = 0; j < windowedMatrix.length; j++) {
                windowedMatrix[j][i] = picture.evaluatePixel(bf, prevElem, windowedMatrix[j][i]);
                prevElem = windowedMatrix[j][i];
            }
        }

        //Create union between two matrices
        for (int i = 0; i < windowedMatrix.length; i++) {
            for (int j = 0; j < windowedMatrix[0].length; j++) {
                windowedMatrix[i][j] = picture.mapPixel(unionFunction, windowedMatrix[i][j], rowMatrix[i][j]);
            }
        }

        finalizeTransformation(picture, windowedMatrix);
    }


}
