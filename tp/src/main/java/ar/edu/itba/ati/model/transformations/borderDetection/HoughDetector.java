package ar.edu.itba.ati.model.transformations.borderDetection;

import ar.edu.itba.ati.model.pictures.ColorPicture;
import ar.edu.itba.ati.model.pictures.GreyPicture;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.shapes.Shape;
import ar.edu.itba.ati.model.shapes.generators.ShapeGenerator;
import ar.edu.itba.ati.model.transformations.PictureTransformer;

import java.awt.image.BufferedImage;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HoughDetector implements PictureTransformer{

    private int threshold;

    private double delta;

    private ShapeGenerator parametricSpaceGenerator;

    private Set<Shape> parametricSpace;

    private Set<Shape> shapes;

    private int max = 0;

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
            System.out.println("parametric space size: " + parametricSpace.size());
        }

        Map<Shape, Integer> accumulator = new HashMap<>();
        for (Shape shape: parametricSpace) {
            accumulator.put(shape, 0);
        }

        max = 0;

        //For every pixel in the image
        for (int i = 0; i < greyPicture.getHeight(); i++) {
            for (int j = 0; j < greyPicture.getWidth(); j++) {
                //Check if it is a white pixel
                if (greyPicture.getPixel(i,j) == 255.0) {
                    //Check if it belongs to any of the shapes
                    for (Shape shape: accumulator.keySet()) {
                        //If the pixel belongs to a shape
                        if (shape.belongs(i, j)) {
                            //Add one in accumulator for given shape
                            accumulator.put(shape, accumulator.get(shape) + 1);
                            if(accumulator.get(shape) > max) {
                                max = accumulator.get(shape);
                            }
                        }
                    }
                }
            }
        }

        return accumulator
                .entrySet()
                .stream()
                .filter(e -> e.getValue() > threshold / 100.0* max)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public Picture transform(Picture picture) {

        this.shapes = findShapes(picture);

        System.out.println("shapes size: " + shapes.size());

        if(shapes.size() == 0) {
            return picture;
        }

        if(shapes.iterator().next().isCircle()){
            return circleTransformation(picture);
        }else if(shapes.iterator().next().isLine()){
            return lineTransformation(picture);
        }else{
            throw new InvalidParameterException("Shape is not supported in Hough transformation");
        }

    }

    private Picture circleTransformation(Picture picture){

        GreyPicture greyPicture;

        ColorPicture colorPicture;
        if(picture.getType() == BufferedImage.TYPE_BYTE_GRAY) {
            colorPicture = ((GreyPicture) picture).toColorPicture();
            greyPicture = (GreyPicture) picture;
        }
        else {
            colorPicture = (ColorPicture) picture;
            greyPicture = ((ColorPicture) picture).toGreyPicture();
        }

        for (int i = 0; i < colorPicture.getHeight(); i++) {
            for (int j = 0; j < colorPicture.getWidth(); j++) {
                if(greyPicture.getPixel(i,j) == 255.0){
                    for (Shape shape: this.shapes) {
                        if (shape.belongs(i, j)) {
                            colorPicture.putPixel(new Double[] {0.0,255.0,0.0}, i, j);
                        }
                    }
                }
            }
        }
        return colorPicture;
    }

    private Picture lineTransformation(Picture picture){

        ColorPicture colorPicture;
        if(picture.getType() == BufferedImage.TYPE_BYTE_GRAY) {
            colorPicture = ((GreyPicture) picture).toColorPicture();
        }
        else {
            colorPicture = (ColorPicture) picture;
        }

        for (int i = 0; i < colorPicture.getHeight(); i++) {
            for (int j = 0; j < colorPicture.getWidth(); j++) {
                for (Shape shape: this.shapes) {
                    if (shape.belongs(i, j)) {
                        colorPicture.putPixel(new Double[] {0.0,255.0,0.0}, i, j);
                    }
                }
            }
        }

        return colorPicture;
    }


}
