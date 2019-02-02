package ar.edu.itba.ati.model.transformations.licenseDetection;

import ar.edu.itba.ati.model.pictures.ColorPicture;
import ar.edu.itba.ati.model.pictures.GreyPicture;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.shapes.Rectangle;
import ar.edu.itba.ati.model.shapes.Shape;
import ar.edu.itba.ati.model.shapes.generators.RectangleSpaceGenerator;
import ar.edu.itba.ati.model.transformations.PictureTransformer;
import ar.edu.itba.ati.model.transformations.borderDetection.HoughDetector;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Set;

public class LicenseDetection implements PictureTransformer {

    final HoughDetector houghDetector;
    final double averageLicenseColor;

    public LicenseDetection(int houghThreshold, double houghDelta, double averageLicenseColor) {
        this.houghDetector = new HoughDetector(houghThreshold, houghDelta, new RectangleSpaceGenerator());
        this.averageLicenseColor = averageLicenseColor;
    }

    @Override
    public <T, R> Picture<R> transform(Picture<T> picture) {
        Set<Shape> houghRectangles = houghDetector.findShapes(picture);
        Rectangle rectangle = getBestRectangle(picture, houghRectangles);

        if (rectangle == null) {
            return (Picture<R>) picture;
        }
        return drawRectangle(picture, rectangle);
//        for (Shape houghRectangle : houghRectangles) {
//            picture = drawRectangle(picture, houghRectangle);
//        }
//        return (Picture<R>) picture;
    }

    private Rectangle getBestRectangle(Picture picture, Set<Shape> shapes) {
        double minDifference = Double.POSITIVE_INFINITY;
        Rectangle bestShape = null;

        for (Shape shape : shapes) {
            int[] coords = ((Rectangle) shape).getCorners();
            String averageStr = picture.getAverageColor(coords[0], coords[1], coords[2], coords[3]);
            double average = Double.parseDouble(averageStr.split("\\s+")[1]);
            double difference = Math.abs(average - averageLicenseColor);
            if (difference < minDifference) {
                minDifference = difference;
                bestShape = (Rectangle) shape;
            }
        }
        return bestShape;
    }

    public static void main(String[] args) {
        String licensePlate = imageToString("/Users/natinavas/Documents/ITBA/ATI/ati/tp/src/main/java/ar/edu/itba/ati/model/transformations/licenseDetection/licensePlate.png");
        String cleanLicensePlate = licensePlate.replaceAll("[^a-zA-Z0-9\\-]", "");
        cleanLicensePlate = cleanLicensePlate.replaceAll("^-", "");
        System.out.println(cleanLicensePlate);
    }

    private Picture drawRectangle(Picture picture, Shape shape) {

        ColorPicture colorPicture;
        if (picture.getType() == BufferedImage.TYPE_BYTE_GRAY) {
            colorPicture = ((GreyPicture) picture).toColorPicture();
        } else {
            colorPicture = (ColorPicture) picture;
        }

        for (int i = 0; i < colorPicture.getHeight(); i++) {
            for (int j = 0; j < colorPicture.getWidth(); j++) {
                if (shape.belongs(i, j)) {
                    colorPicture.putPixel(new Double[]{0.0, 255.0, 0.0}, i, j);
                }
            }
        }

        return colorPicture;
    }

    /**
     * Using OCR library read text in license plate
     * @param imageLocation
     * @return
     */
    public static String imageToString(String imageLocation) {
        ITesseract instance = new Tesseract();
        try {
            return instance.doOCR(new File(imageLocation));
        } catch (TesseractException e)
        {
            e.getMessage();
            return "Error while reading image";
        }
    }
}
