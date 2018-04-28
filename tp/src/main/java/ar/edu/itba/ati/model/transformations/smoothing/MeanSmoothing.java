package ar.edu.itba.ati.model.transformations.smoothing;

import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.Masks;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.SlidingWindowWithMask;

public class MeanSmoothing extends SlidingWindowWithMask<Double> {

    public MeanSmoothing(int size) {
        super(Masks.meanMask(size));
    }
}
