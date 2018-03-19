package ar.edu.itba.ati.model.transformations.noise;


import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.PictureTransformer;

import java.util.Random;
import java.util.function.BiFunction;

public class RayleighNoise implements PictureTransformer{

    //TODO: check this parameter
    private final double psi = 0.5;
    private final Random random = new Random();

    @Override
    public void transform(Picture picture) {
        for (int i = 0; i < picture.getHeight(); i++) {
            for (int j = 0; j < picture.getHeight(); j++) {
                double value = random.nextDouble();
                value = psi * Math.sqrt(-2 * Math.log(1 - value));
                BiFunction<Double,Double,Double> function = (px, noise) -> px * noise;
                picture.mapPixel(i, j, function, value);
            }
        }
    }
}
