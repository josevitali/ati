package ar.edu.itba.ati.model.transformations.slidingWindows.withMask;

import ar.edu.itba.ati.model.pictures.Picture;

import java.util.function.BiFunction;
import java.util.function.Function;

public class HighPassFilter extends SlidingWindowWithMask<Integer> {
    public HighPassFilter(int size) {
        super(Mask.highPassMask(size));
    }

    @Override
    public <Integer> void transform(Picture<Integer> picture){
        super.transform(picture);
        picture.mapPixelByPixel(new Function<Double, Double>() {
            @Override
            public Double apply(Double aDouble) {
                return aDouble >= 255.0/2 ? 255.0 : 0.0;
            }
        });
    }
}
