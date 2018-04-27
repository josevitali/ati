package ar.edu.itba.ati.model.transformations.slidingWindows.withMask;

import ar.edu.itba.ati.model.pictures.Picture;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class GradientMethodTransformation extends SlidingMethodWithMultiMask {

    private static final Function<Double,Double> prod = px -> px * px;

    private static final BiFunction<Double,Double,Double> bf = new BiFunction<Double, Double, Double>() {
        @Override
        public Double apply(Double aDouble, Double aDouble2) {
            return aDouble + aDouble2 * aDouble2;
        }
    };

    private static final Function<Double,Double> sqrt = px -> Math.sqrt(px);

    public GradientMethodTransformation(List<Mask> masks){
        super(masks, new BiConsumer<Picture, List<Picture>>() {
            @Override
            public void accept(Picture picture, List<Picture> pictures) {

                picture.mapPixelByPixel(prod);

                for(Picture otherPicture : pictures){
                    picture.mapPixelByPixel(bf , otherPicture);
                }

                picture.mapPixelByPixel(sqrt);
            }
        });
    }


}
