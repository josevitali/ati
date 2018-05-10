package ar.edu.itba.ati.model.transformations.slidingWindows.withMask;

import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.PictureTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class SlidingMethodWithMultiMask implements PictureTransformer{

    private final List<Mask> masks;
    private final BiConsumer<Picture,List<Picture>> bc;

    public SlidingMethodWithMultiMask(List<Mask> masks, BiConsumer<Picture, List<Picture>> bc){
        this.masks = masks;
        this.bc = bc;
    }

    @Override
    public <T,R> Picture transform(Picture<T> picture) {
        List<Picture> otherPictures = new ArrayList();

        for(int i = 1; i < masks.size(); i++){
            Picture clone = picture.getClone();
            PictureTransformer transformer = new SlidingWindowWithMask(masks.get(i));
            transformer.transform(clone);
            otherPictures.add(clone);
        }

        PictureTransformer transformer = new SlidingWindowWithMask(masks.get(0));
        transformer.transform(picture);

        bc.accept(picture, otherPictures);
        return picture;
    }
}
