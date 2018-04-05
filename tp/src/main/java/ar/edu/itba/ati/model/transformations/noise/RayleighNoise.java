package ar.edu.itba.ati.model.transformations.noise;


import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.PictureTransformer;

import java.util.Random;
import java.util.function.Function;

public class RayleighNoise implements PictureTransformer{

    private final double psi;
    private final Random random;

    public RayleighNoise(double psi) {
        this.psi = psi;
        this.random = new Random();
    }

    @Override
    public void transform(Picture picture) {
        for (int row = 0; row < picture.getHeight(); row++) {
            for (int col = 0; col < picture.getWidth(); col++) {
                Function<Double,Double> function = px -> px * (psi * Math.sqrt(-2 * Math.log(1 - random.nextDouble())));
                picture.mapPixel(row, col, function);
            }
        }
    }
}
