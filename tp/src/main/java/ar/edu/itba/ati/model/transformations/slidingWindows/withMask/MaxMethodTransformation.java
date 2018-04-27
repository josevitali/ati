package ar.edu.itba.ati.model.transformations.slidingWindows.withMask;

import ar.edu.itba.ati.model.pictures.Picture;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MaxMethodTransformation extends SlidingMethodWithMultiMask {

    private static final BiFunction<Double,Double,Double> max = (aDouble, aDouble2) -> Math.max(aDouble, aDouble2);
    private static final Function<Double,Double> abs = px -> Math.abs(px);

    public MaxMethodTransformation(List<Mask> masks) {
        super(masks, new BiConsumer<Picture, List<Picture>>() {
            @Override
            public void accept(Picture picture, List<Picture> pictures) {
                picture.mapPixelByPixel(abs);
                for (Picture p : pictures){
                    p.mapPixelByPixel(abs);
                    picture.mapPixelByPixel(max, p);
                }
            }
        });
    }
}
