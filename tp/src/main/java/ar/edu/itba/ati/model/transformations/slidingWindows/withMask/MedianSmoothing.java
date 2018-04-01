package ar.edu.itba.ati.model.transformations.slidingWindows.withMask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

public class MedianSmoothing extends SlidingWindowWithMask<Integer> {
    public MedianSmoothing(Mask<Integer> mask) {
        super(mask, new BiFunction<Integer[][], Double[][], Double>() {
            @Override
            public Double apply(Integer[][] integers, Double[][] subMatrix) {
                List<Double> l = new ArrayList();
                for(int row = 0; row < subMatrix.length; row++){
                    for(int col = 0; col < subMatrix[0].length; col++){
                        for(int i = 0; i < integers[row][col]; i++){
                            l.add(subMatrix[row][col]);
                        }
                    }
                }

                l.sort(Comparator.naturalOrder());

                return l.get(l.size() / 2);
            }
        });
    }
}
