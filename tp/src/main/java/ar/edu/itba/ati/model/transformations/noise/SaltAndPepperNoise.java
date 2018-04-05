package ar.edu.itba.ati.model.transformations.noise;


import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.PictureTransformer;

import java.util.Random;
import java.util.function.Function;

public class SaltAndPepperNoise implements PictureTransformer{

    private final double p0;
    private final double p1;
    private final Random random;

    public SaltAndPepperNoise(double p0) {
        this.p0 = p0;
        this.p1 = 1 - p0;
        this.random = new Random();
    }

    @Override
    public void transform(Picture picture) {
        picture.normalize();
        Function<Double,Double> blackFunction = px -> 0.0;
        Function<Double,Double> whiteFunction = px -> 255.0;
        for (int row = 0; row < picture.getHeight(); row++) {
            for (int col = 0; col < picture.getWidth(); col++) {
                double value = random.nextDouble();
                if(value <= p0) {
                    picture.mapPixel(row, col, blackFunction);
                }
                else if(value >= p1) {
                    picture.mapPixel(row, col, whiteFunction);
                }
            }
        }
    }
}
