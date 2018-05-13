package ar.edu.itba.ati.model.transformations.borderDetection;

import ar.edu.itba.ati.model.pictures.ColorPicture;
import ar.edu.itba.ati.model.pictures.GreyPicture;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.shapes.Shape;
import ar.edu.itba.ati.model.shapes.generators.ShapeGenerator;
import ar.edu.itba.ati.model.transformations.PictureTransformer;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    private Set<Shape> findShapes(Picture picture){
        GreyPicture greyPicture;
        if(picture.getType() == BufferedImage.TYPE_3BYTE_BGR) {
            greyPicture = ((ColorPicture)picture).toGreyPicture();
        } else {
            greyPicture = (GreyPicture)picture;
        }

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

        return accumulator
                .entrySet()
                .stream()
                .filter(e -> e.getValue() > threshold)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public <T, R> Picture<R> transform(Picture<T> picture) {

        Set<Shape> shapes = findShapes(picture);

        for(Shape s: shapes){

        }

        return null;
    }

}
