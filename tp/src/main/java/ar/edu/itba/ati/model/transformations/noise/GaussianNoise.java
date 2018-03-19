package ar.edu.itba.ati.model.transformations.noise;


import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.PictureTransformer;

import java.util.Random;
import java.util.function.BiFunction;

public class GaussianNoise implements PictureTransformer{

    private final double deviation = 0.05;
    private final double mean = 0;
    private final Random random = new Random();

    @Override
    public void transform(Picture picture) {
        for (int i = 0; i < picture.getHeight(); i++) {
            for (int j = 0; j < picture.getHeight(); j++) {
                Double value = random.nextGaussian() * deviation + mean;
                BiFunction<Double,Double,Double> function = (px,noise) -> px + noise;
                picture.mapPixel(i, j, function, value);
            }
        }
    }
}
