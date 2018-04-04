package ar.edu.itba.ati.model.transformations.slidingWindows.withMask;

import ar.edu.itba.ati.model.pictures.Picture;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class MaxMethodTransformation extends SlidingMethodWithMultiMask {

    private static final BiFunction<Double,Double,Double> max = (aDouble, aDouble2) -> Math.max(aDouble, aDouble2);

    public MaxMethodTransformation(List<Mask> masks) {
        super(masks, new BiConsumer<Picture, List<Picture>>() {
            @Override
            public void accept(Picture picture, List<Picture> pictures) {
                for (Picture p : pictures){
                    picture.mapPixelByPixel(max, p);
                }
            }
        });
    }
}
