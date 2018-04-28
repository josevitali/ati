package ar.edu.itba.ati.model.transformations.borderDetection;

import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.Mask;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.SlidingWindowWithMask;

import java.util.function.BiFunction;
import java.util.function.Function;

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
    protected Function<Double, Function<Double, Function<Double, Double>>> triFunction = (prevVal -> (actualVal -> (nextVal -> {
        if(actualVal == 0.0) {
            if(prevVal*nextVal<0.0) {
                return 255.0;
            }
            return 0.0;
        }
        if(prevVal*actualVal<0.0) {
            return 255.0;
        }
        return 0.0;
    })));


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
                if(j+1 >= windowedMatrix[0].length) {
                    rowMatrix[i][j] = picture.evaluateTwoPixels(bf, prevElem, windowedMatrix[i][j]);
                } else {
                    rowMatrix[i][j] = picture.evaluateThreePixels(triFunction, prevElem, windowedMatrix[i][j], windowedMatrix[i][j+1]);
                }
                prevElem = windowedMatrix[i][j];
            }
        }

        T[][] colMatrix = (T[][]) new Object[1 + picture.getHeight() - mask.getSize()][1 + picture.getWidth() - mask.getSize()];

        //Find sign changes in matrix by cols
        prevElem = windowedMatrix[0][0];
        for (int i = 0; i < windowedMatrix[0].length; i++) {
            for (int j = 0; j < windowedMatrix.length; j++) {
                if(j+1 >= windowedMatrix.length) {
                    colMatrix[j][i] = picture.evaluateTwoPixels(bf, prevElem, windowedMatrix[j][i]);
                } else {
                    colMatrix[j][i] = picture.evaluateThreePixels(triFunction, prevElem, windowedMatrix[j][i], windowedMatrix[j+1][i]);
                }
                prevElem = windowedMatrix[j][i];
            }
        }

        //Create union between two matrices
        for (int i = 0; i < windowedMatrix.length; i++) {
            for (int j = 0; j < windowedMatrix[0].length; j++) {
                windowedMatrix[i][j] = picture.mapPixel(unionFunction, colMatrix[i][j], rowMatrix[i][j]);
            }
        }
        
        finalizeTransformation(picture, windowedMatrix);
    }


}
