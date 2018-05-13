package ar.edu.itba.ati.model.transformations.borderDetection;

import ar.edu.itba.ati.model.pictures.GreyPicture;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.shapes.Shape;
import ar.edu.itba.ati.model.shapes.generators.ShapeGenerator;
import ar.edu.itba.ati.model.transformations.PictureTransformer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HoughDetector implements PictureTransformer{

    private int threshold;

    private double delta;

    private ShapeGenerator parametricSpaceGenerator;

    private Set<Shape> parametricSpace;

    public HoughDetector(int threshold, double delta, ShapeGenerator sg) {
        this.threshold = threshold;
        this.delta = delta;
        this.parametricSpaceGenerator = sg;
    }

    @Override
    public <T, R> Picture<R> transform(Picture<T> picture) {
        GreyPicture greyPicture = (GreyPicture) picture;
        if(this.parametricSpace == null){
            this.parametricSpace = parametricSpaceGenerator.getParametricSet(picture.getWidth(), picture.getHeight(), delta);
        }

        Map<Shape, Integer> accumulator = new HashMap<>();
        for (Shape shape: parametricSpace) {
            accumulator.put(shape, 0);
        }

        for (int i = 0; i < greyPicture.getWidth(); i++) {
            for (int j = 0; j < greyPicture.getHeight(); j++) {
                if (greyPicture.getPixel(i,j) == 255.0) {
                    for (Shape shape: accumulator.keySet()) {
                        if (shape.belongs(i, j)) {
                            accumulator.put(shape, accumulator.get(shape) + 1);
                        }
                    }
                }
            }
        }

        for(Map.Entry e: accumulator.entrySet()){
            System.out.println(e);
        }

        return null;
    }

}
