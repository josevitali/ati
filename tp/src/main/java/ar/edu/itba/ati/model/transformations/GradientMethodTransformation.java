package ar.edu.itba.ati.model.transformations;

import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.Mask;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.SlidingWindowWithMask;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class GradientMethodTransformation implements PictureTransformer {

    private final List<Mask> masks;

    public GradientMethodTransformation(List<Mask> masks){
        this.masks = masks;
    }

    @Override
    public <T> void transform(Picture<T> picture) {
        List<Picture> otherPictures = new ArrayList();

        for(int i = 1; i < masks.size(); i++){
            Picture clone = picture.getClone();
            PictureTransformer transformer = new SlidingWindowWithMask(masks.get(i));
            transformer.transform(clone);
            otherPictures.add(clone);
        }

        PictureTransformer transformer = new SlidingWindowWithMask(masks.get(0));
        transformer.transform(picture);

        picture.mapPixelByPixel(px -> px * px);

        for(Picture otherPicture : otherPictures){
            picture.mapPixelByPixel((d1, d2) -> d1 + d2 * d2, otherPicture);
        }

        picture.mapPixelByPixel(px -> Math.sqrt(px));
    }
}
