package ar.edu.itba.ati.model.transformations.slidingWindows.withMask;

import java.util.function.BiFunction;

public class MeanSmoothing extends SlidingWindowWithMask<Double> {

    public MeanSmoothing(int size) {
        super(Mask.meanMask(size));
    }
}
