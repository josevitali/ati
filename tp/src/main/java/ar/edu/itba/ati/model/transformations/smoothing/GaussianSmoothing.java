package ar.edu.itba.ati.model.transformations.smoothing;

import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.Mask;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.Masks;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.SlidingWindowWithMask;

public class GaussianSmoothing extends SlidingWindowWithMask<Double> {
    public GaussianSmoothing(double sigma) {
        super(Masks.gaussianMask(calculateSize(sigma), sigma));
    }

    private static int calculateSize(double sigma) {
        int size;

        if(sigma >= 1 && sigma <= 10){
            size = (int) (2.0 * sigma + 1);
        } else {
            size = (int) (3.0 * sigma);
        }

        return size % 2 == 0 ? size - 1 : size;
    }
}
