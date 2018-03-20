package ar.edu.itba.ati.model.transformations.noise;


import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.PictureTransformer;

import java.util.Random;

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
        for (int i = 0; i < picture.getHeight(); i++) {
            for (int j = 0; j < picture.getHeight(); j++) {
                double value = random.nextDouble();
                if(value <= p0) {
                    picture.putPixel(0.0, i, j);
                }
                else if(value >= p1) {
                    picture.putPixel(255.0, i, j);
                }
            }
        }
    }
}
