package ar.edu.itba.ati.model.transformations.borderDetection;

import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.Mask;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.Masks;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.SlidingWindowWithMask;

import java.util.function.BiFunction;

public class SusanDetector extends SlidingWindowWithMask<Integer> {

    public SusanDetector(Double t) {
        super(Masks.CIRCULAR, new BiFunction<Integer[][], Double[][], Double>() {
            @Override
            public Double apply(Integer[][] circularMask, Double[][] doubles) {
                int n = 0;
                int size = circularMask.length * circularMask[0].length;

                for(int row = 0; row < circularMask.length; row++){
                    for(int col = 0; col < circularMask[0].length; col++){
                        if(circularMask[row][col] == 1){
                            n += Math.abs(doubles[row][col] - doubles[3][3]) < t ? 1.0 : 0.0;
                        }
                    }
                }
                return 1 - ((double)n / size);
            }
        });
    }
}
