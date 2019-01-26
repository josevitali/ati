package ar.edu.itba.ati.model.transformations.borderDetection;

import ar.edu.itba.ati.model.pictures.ColorPicture;
import ar.edu.itba.ati.model.pictures.GreyPicture;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.shapes.Rectangle;
import ar.edu.itba.ati.model.shapes.Shape;
import ar.edu.itba.ati.model.shapes.generators.ShapeGenerator;
import ar.edu.itba.ati.model.transformations.PictureTransformer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

public class HoughDetector implements PictureTransformer{

    private int threshold;

    private double delta;

    private ShapeGenerator parametricSpaceGenerator;

    private Set<Shape> parametricSpace;

    private Set<Shape> shapes;

    private int max = 0;

    private final double averageLicenseColor = 12;

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

        Map<Shape, ArrayList<Point>> whiteAccumulator = new HashMap<>();
//        Map<Shape, Integer> pixel_accumulator = new HashMap<>();

        for (Shape shape: parametricSpace) {
            whiteAccumulator.put(shape, new ArrayList<>());
//            pixel_accumulator.put(shape, 0);
        }

        max = 0;

        //For every pixel in the image
        for (int i = 0; i < greyPicture.getHeight(); i++) {
            for (int j = 0; j < greyPicture.getWidth(); j++) {
                //Check if it belongs to any of the shapes
                for (Shape shape: whiteAccumulator.keySet()) {
                    //Check if it is a white pixel
                    if (greyPicture.getPixel(i,j) == 255.0) {
                        //If the pixel belongs to a shape
                        if (shape.belongs(i, j)) {
                            //Add one in accumulator for given shape
                            whiteAccumulator.get(shape).add(new Point(i,j));
                            if(whiteAccumulator.get(shape).size() > max) {
                                max = whiteAccumulator.get(shape).size();
                            }
                        }
//                            pixel_accumulator.put(shape,pixel_accumulator.get(shape)+1);
//                            break;
                    }
                }
            }
        }



        System.out.println(whiteAccumulator.get(whiteAccumulator.keySet().toArray()[0]));

        System.out.println(max);

        Set<Shape> bestShapes = whiteAccumulator
                .entrySet()
                .stream()
                .filter(e -> (double)e.getValue().size() / ((Rectangle)e.getKey()).getSize() * 100 >= threshold)
//                .filter(e -> (double)e.getValue().size() == max)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

//        return bestShapes;

        double minDifference = Double.POSITIVE_INFINITY;
        Shape bestShape = null;

        for (Shape shape : bestShapes) {
            int[] coords = ((Rectangle)shape).getCorners();
            String averageStr = picture.getAverageColor(coords[0], coords[1], coords[2], coords[3]);
            double average = Double.parseDouble(averageStr.split("\\s+")[1]);
            double difference = Math.abs(average - averageLicenseColor);
            if(difference < minDifference) {
                minDifference = difference;
                bestShape = shape;
            }
        }

        Set<Shape> ret = new HashSet<>();
        if(bestShape != null) {
            ret.add(bestShape);
        }
        return ret;

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
        }else if(shapes.iterator().next().isRectangle()){
            return lineTransformation(picture);
        }
        else{
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
