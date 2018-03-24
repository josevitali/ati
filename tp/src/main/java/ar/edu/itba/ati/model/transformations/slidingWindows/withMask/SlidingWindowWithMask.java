package ar.edu.itba.ati.model.transformations.slidingWindows.withMask;

import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.slidingWindows.SlidingWindowTransformation;

import java.util.function.BiFunction;
import java.util.function.Function;

public class SlidingWindowWithMask<T extends Number> extends SlidingWindowTransformation<T> {

    private final Mask<T> mask;
    private final BiFunction<T[][],Double[][],Double> bf;

    public SlidingWindowWithMask(Mask<T> mask, BiFunction<T[][], Double[][], Double> bf) {
        super(mask.getSize(), mask.getSize(), curry(bf, mask));
        this.mask = mask;
        this.bf = bf;
    }

    public SlidingWindowWithMask(Mask<T> mask){
        this(mask,new BiFunction<T[][], Double[][], Double>() {
            @Override
            public Double apply(T[][] ts, Double[][] doubles) {
                double ret = 0;
                for(int row = 0; row < ts.length; row++){
                    for(int col = 0; col < ts[0].length; col++){
                        ret += ts[row][col].doubleValue() * doubles[row][col];
                    }
                }
                return ret;
            }
        });
    }

    private static <T extends Number> Function<Double[][], Double> curry(BiFunction<T[][], Double[][], Double> bf, Mask<T> mask) {
        return new Function<Double[][], Double>() {
            @Override
            public Double apply(Double[][] doubles) {
                return bf.apply(mask.getMatrix(), doubles);
            }
        };
    }

    @Override
    protected <T> void finalizeTransformation(Picture<T> picture, T[][] transformedMatrix) {
        for(int row = 0; row < transformedMatrix.length; row++){
            for(int col = 0; col < transformedMatrix[0].length; col++){
                picture.putPixel(transformedMatrix[row][col], (row + mask.getLengthToUpBorder()), (col + mask.getLengthToLeftBorder()));
            }
        }
    }
}
