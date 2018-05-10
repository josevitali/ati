package ar.edu.itba.ati.model.transformations.noise;


import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.PictureTransformer;

import java.util.Random;
import java.util.function.Function;

public class SaltAndPepperNoise extends Noise{

    private final Function<Double,Double> blackFunction = px -> 0.0;
    private final Function<Double,Double> whiteFunction = px -> 255.0;

    public SaltAndPepperNoise(double p0) {
        super(2 * p0);
    }

    @Override
    public Picture transform(Picture picture) {
        picture.normalize();
        super.transform(picture);
        return picture;
    }

    @Override
    protected Double applyNoise(Double pixel) {
        if(random.nextBoolean()){
            return blackFunction.apply(pixel);
        }
        return whiteFunction.apply(pixel);
    }
}
