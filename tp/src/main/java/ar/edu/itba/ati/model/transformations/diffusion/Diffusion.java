package ar.edu.itba.ati.model.transformations.diffusion;


import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.PictureTransformer;

import java.util.function.Function;

public abstract class Diffusion implements PictureTransformer{

    private final Function<Double, Double> detector;
    private final double iterations;
    private final double lambda = 0.25;
    private final int[][] directions = {{1,0},{-1,0},{0,1},{0,-1}};

    protected Diffusion(Function<Double, Double> detector, double iterations) {
        this.detector = detector;
        this.iterations = iterations;
    }

    @Override
    public <T> void transform(Picture<T> picture) {
        Picture<T> prevPicture = picture.getClone();

        for (int t = 0; t < iterations; t++) {
            //switch prev and actual picture
            Picture<T> aux = prevPicture;
            prevPicture = picture;
            picture = aux;

            for (int i = 1; i < prevPicture.getHeight()-1; i++) {
                for (int j = 1; j < prevPicture.getWidth()-1; j++) {
                    T accumPixel = null;
                    T originalPixel = prevPicture.getPixel(i,j);
                    //add in every direction applying border detection
                    for (int k = 0; k < 4; k++) {
                        T adjacentPixel = prevPicture.getPixel(i+directions[k][0],j+directions[k][1]);
                        T d = picture.evaluateTwoPixels((x, y) -> y - x, originalPixel, adjacentPixel);
                        T c = picture.evaluatePixel(detector, d);
                        T appliedPixel = picture.evaluateTwoPixels((x, y) -> x * y, c, d);
                        if(accumPixel == null) {
                            accumPixel = appliedPixel;
                        }
                        else {
                            accumPixel = picture.evaluateTwoPixels((x,y) -> x + y, accumPixel, appliedPixel);
                        }
                    }
                    //assign diffused pixel to picture
                    T newPixel = picture.evaluateTwoPixels((x,y) -> x + lambda * y, originalPixel, accumPixel);
                    picture.putPixel(newPixel, i, j);
                }
            }
        }
    }
}
