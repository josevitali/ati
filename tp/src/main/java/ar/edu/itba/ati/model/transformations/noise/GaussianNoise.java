package ar.edu.itba.ati.model.transformations.noise;


import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.PictureTransformer;

import java.util.Random;
import java.util.function.Function;

public class GaussianNoise implements PictureTransformer{

    private final double deviation;
    private final double mean;
    private final Random random;

    public GaussianNoise(double mean, double deviation) {
        this.mean = mean;
        this.deviation = deviation;
        this.random = new Random();
    }

    @Override
    public void transform(Picture picture) {
        for (int i = 0; i < picture.getHeight(); i++) {
            for (int j = 0; j < picture.getHeight(); j++) {
                Function<Double,Double> function = px -> px + (random.nextGaussian() * deviation + mean);
                picture.mapPixel(i, j, function);
//                }
            }
        }
    }
}
