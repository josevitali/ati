package ar.edu.itba.ati.model.transformations.noise;

import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.PictureTransformer;

import java.util.Random;

public abstract class Noise implements PictureTransformer{

    private final double density;
    protected final Random random;

    public Noise(double density) {
        this.density = density;
        this.random = new Random();
    }

    @Override
    public Picture transform(Picture picture) {
        for (int row = 0; row < picture.getHeight(); row++) {
            for (int col = 0; col < picture.getWidth(); col++) {
                if(random.nextDouble() <= density){
                    picture.mapPixel(row, col, px -> applyNoise((Double) px));
                }
            }
        }

        return picture;
    }

    protected abstract Double applyNoise(Double pixel);


}
