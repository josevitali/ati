package ar.edu.itba.ati.model.transformations.licenseDetection;

import ar.edu.itba.ati.model.pictures.ColorPicture;
import ar.edu.itba.ati.model.pictures.GreyPicture;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.shapes.Rectangle;
import ar.edu.itba.ati.model.shapes.Shape;
import ar.edu.itba.ati.model.shapes.generators.RectangleSpaceGenerator;
import ar.edu.itba.ati.model.transformations.PictureTransformer;
import ar.edu.itba.ati.model.transformations.borderDetection.CannyDetector;
import ar.edu.itba.ati.model.transformations.borderDetection.HoughDetector;
import ar.edu.itba.ati.model.transformations.smoothing.MeanSmoothing;
import ar.edu.itba.ati.utils.LicenseConstants;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Set;

public class LicenseDetection implements PictureTransformer {

    private final HoughDetector houghDetector;
    private final double averageLicenseColor;
    private final CannyDetector cannyDetector;
    private final MeanSmoothing meanSmoothing;

    public LicenseDetection(int houghThreshold, double houghDelta, LicenseConstants licenseConstants) {
        this.houghDetector = new HoughDetector(houghThreshold, houghDelta, new RectangleSpaceGenerator(licenseConstants.getRatio()));
        this.cannyDetector = new CannyDetector(1);
        this.meanSmoothing = new MeanSmoothing(7);
        this.averageLicenseColor = licenseConstants.getAvgColor();
    }

    @Override
    public <T, R> Picture<R> transform(Picture<T> picture) {
        Picture originalPicture = picture.getClone();

        for (int i = 0; i < 2; i++) {
            picture = meanSmoothing.transform(picture);
        }

        picture = cannyDetector.transform(picture);

        long startTime = System.currentTimeMillis();

        Set<Shape> houghRectangles = houghDetector.findShapes(picture);
        Rectangle rectangle = getBestRectangle(picture, houghRectangles);

        if (rectangle == null) {
            return (Picture<R>) originalPicture;
        }

        int[] coords = rectangle.getCorners();
        originalPicture.crop(coords[0], coords[2], coords[1], coords[3]);
        BufferedImage image = originalPicture.toBufferedImage();
        
        String cleanLicense = getCleanLicense(imageToString(image));
        System.out.println("License: " + cleanLicense);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Time: " + elapsedTime);
        return (Picture<R>) originalPicture;
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

    private String getCleanLicense(String licensePlate) {
        String cleanLicensePlate = licensePlate.replaceAll("[^a-zA-Z0-9\\-]", "");
        cleanLicensePlate = cleanLicensePlate.replaceAll("^-", "");
        return cleanLicensePlate;
    }

    /**
     * Using OCR library read text in license plate
     *
     * @param image
     * @return
     */
    public static String imageToString(BufferedImage image) {
        ITesseract instance = new Tesseract();
        try {
            return instance.doOCR(image);
        } catch (TesseractException e) {
            e.getMessage();
            return "Error while reading image";
        }
    }

    /**
     * Using OCR library read text in license plate
     *
     * @param imageLocation
     * @return
     */
    public static String imageToString(String imageLocation) {
        ITesseract instance = new Tesseract();
        try {
            return instance.doOCR(new File(imageLocation));
        } catch (TesseractException e) {
            e.getMessage();
            return "Error while reading image";
        }
    }
}
