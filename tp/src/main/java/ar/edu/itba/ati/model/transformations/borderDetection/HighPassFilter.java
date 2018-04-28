package ar.edu.itba.ati.model.transformations.borderDetection;

import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.Masks;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.SlidingWindowWithMask;

import java.util.function.BiFunction;
import java.util.function.Function;

public class HighPassFilter extends SlidingWindowWithMask<Integer> {
    public HighPassFilter(int size) {
        super(Masks.highPassMask(size));
    }

    @Override
    public <Integer> void transform(Picture<Integer> picture){
        super.transform(picture);
        picture.normalize();
        picture.mapPixelByPixel(new Function<Double, Double>() {
            @Override
            public Double apply(Double aDouble) {
                return aDouble >= 255.0/2 ? 255.0 : 0.0;
            }
        });
    }
}
