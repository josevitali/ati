package ar.edu.itba.ati.model.transformations.borderDetection;

import ar.edu.itba.ati.model.pictures.ColorPicture;
import ar.edu.itba.ati.model.pictures.GreyPicture;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.shapes.Line;
import ar.edu.itba.ati.model.shapes.Shape;
import ar.edu.itba.ati.model.shapes.generators.ShapeGenerator;
import ar.edu.itba.ati.model.transformations.PictureTransformer;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HoughDetector implements PictureTransformer{

    private int threshold;

    private double delta;

    private ShapeGenerator parametricSpaceGenerator;

    private Set<Shape> parametricSpace;

    private Set<Shape> shapes;

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

        for (int i = 0; i < greyPicture.getHeight(); i++) {
            for (int j = 0; j < greyPicture.getWidth(); j++) {
                if (greyPicture.getPixel(i,j) == 255.0) {
                    for (Shape shape: accumulator.keySet()) {
                        if (shape.belongs(i, j)) {
                            accumulator.put(shape, accumulator.get(shape) + 1);
                        }
                    }
                }
            }
        }

        return accumulator
                .entrySet()
                .stream()
                .filter(e -> e.getValue() > threshold)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public Picture transform(Picture picture) {

        this.shapes = findShapes(picture);

        System.out.println("shapes size: " + shapes.size());

        ColorPicture colorPicture;
        if(picture.getType() == BufferedImage.TYPE_BYTE_GRAY) {
            colorPicture = ((GreyPicture) picture).toColorPicture();
        }
        else {
            colorPicture = (ColorPicture) picture;
        }

        Set<Shape> seletedShapes = new HashSet<>();


        for (int i = 0; i < colorPicture.getHeight(); i++) {
            for (int j = 0; j < colorPicture.getWidth(); j++) {
                for (Shape shape: this.shapes) {
                    if (shape.belongs(i, j)) {
                        colorPicture.putPixel(new Double[] {0.0,255.0,0.0}, i, j);
                        seletedShapes.add(shape);
                    }
                }
            }
        }

        System.out.println("selectedShapes size: " + seletedShapes.size());
        for (Shape shape: this.shapes) {
            System.out.println("teta: " + ((Line)shape).getTeta());
            System.out.println("ro: " + ((Line)shape).getRo());
        }
        
        return colorPicture;
    }


}
