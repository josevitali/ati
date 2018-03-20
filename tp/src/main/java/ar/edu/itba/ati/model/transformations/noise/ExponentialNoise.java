package ar.edu.itba.ati.model.transformations.noise;


import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.PictureTransformer;

import java.util.Random;
import java.util.function.Function;

public class ExponentialNoise implements PictureTransformer{

    private final double lambda;
    private final Random random;

    public ExponentialNoise(double lambda) {
        this.lambda = lambda;
        this.random = new Random();
    }

    @Override
    public void transform(Picture picture) {
        for (int i = 0; i < picture.getHeight(); i++) {
            for (int j = 0; j < picture.getHeight(); j++) {
                Function<Double,Double> function = px -> px * ((-1 / lambda) * Math.log(random.nextDouble()));
                picture.mapPixel(i, j, function);
            }
        }

    }
}
