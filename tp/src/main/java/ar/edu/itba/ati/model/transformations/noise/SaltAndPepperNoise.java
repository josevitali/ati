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
        Function<Double,Double> blackFunction = px -> px * 0;
        Function<Double,Double> whiteFunction = px -> px * 255;
        for (int i = 0; i < picture.getHeight(); i++) {
            for (int j = 0; j < picture.getHeight(); j++) {
                double value = random.nextDouble();
                if(value <= p0) {
                    picture.mapPixel(i, j, blackFunction);
                }
                else if(value >= p1) {
                    picture.mapPixel(i, j, whiteFunction);
                }
            }
        }
    }
}
