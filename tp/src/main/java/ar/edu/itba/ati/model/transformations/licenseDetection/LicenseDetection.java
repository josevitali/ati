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
import org.javatuples.Triplet;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LicenseDetection implements PictureTransformer {

    private final HoughDetector houghDetector;
    private final double averageLicenseColor;
    private final CannyDetector cannyDetector;
    private final MeanSmoothing meanSmoothing;
    private final int licenseLength;
    private final Function<String, Integer> scoreFunction;

    public LicenseDetection(int houghThreshold, double houghDelta, LicenseConstants licenseConstants) {
        this.houghDetector = new HoughDetector(houghThreshold, houghDelta, new RectangleSpaceGenerator(licenseConstants.getRatio()));
        this.cannyDetector = new CannyDetector(1);
        this.meanSmoothing = new MeanSmoothing(3);
        this.averageLicenseColor = licenseConstants.getAvgColor();
        this.licenseLength = licenseConstants.getLength();
        this.scoreFunction = licenseConstants.getScoreFunction();
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

        if (houghRectangles.isEmpty()) {
            return (Picture<R>) originalPicture;
        }

        System.out.println("Retangles found: " + houghRectangles.size());

        List candidatePictures = houghRectangles.stream().map(r -> {
            int[] coords = ((Rectangle)r).getCorners();
            Picture croppedPic = originalPicture.getClone();
            croppedPic.crop(coords[0]-15, coords[2]+15, coords[1]-15, coords[3]+15);
            BufferedImage image = croppedPic.toBufferedImage();
            String cleanLicense = getCleanLicense(imageToString(image));
            return new Triplet(croppedPic, cleanLicense, scoreFunction.apply(cleanLicense));
        }).sorted((o1, o2) -> (Integer) o2.getValue2() - (Integer) o1.getValue2()).collect(Collectors.toList());

        if(!candidatePictures.isEmpty()){
            Triplet<Picture, String, Integer> triplet = (Triplet<Picture, String, Integer>)candidatePictures.get(0);
            System.out.println("License: " + triplet.getValue1());
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println("Time: " + elapsedTime);
            return (Picture<R>) triplet.getValue0();
        }


        System.out.println("License not found");
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Time: " + elapsedTime);
        return (Picture<R>) picture;
    }

    private Set<Rectangle> getBestRectangle(Picture picture, Set<Shape> shapes) {
        Set<Rectangle> bestRectangles = new TreeSet<>(new Comparator<Rectangle>() {
            @Override
            public int compare(Rectangle o1, Rectangle o2) {
                int[] coords1 = o1.getCorners();
                String averageStr1 = picture.getAverageColor(coords1[0], coords1[1], coords1[2], coords1[3]);
                double average1 = Double.parseDouble(averageStr1.split("\\s+")[1]);
                double difference1 = Math.abs(average1 - averageLicenseColor);
                int[] coords2 = o2.getCorners();
                String averageStr2 = picture.getAverageColor(coords2[0], coords2[1], coords2[2], coords2[3]);
                double average2 = Double.parseDouble(averageStr2.split("\\s+")[1]);
                double difference2 = Math.abs(average2 - averageLicenseColor);
                return Double.compare(difference1, difference2);
            }
        });
        bestRectangles.addAll(shapes.stream().map(s -> (Rectangle)s).collect(Collectors.toSet()));
        return bestRectangles;
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
        String cleanLicensePlate = licensePlate.replaceAll("[^A-Z0-9\\-]", "");
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

    public static BufferedImage scale(BufferedImage imageToScale, int dWidth, int dHeight) {
        BufferedImage scaledImage = null;
        if (imageToScale != null) {
            scaledImage = new BufferedImage(dWidth, dHeight, imageToScale.getType());
            Graphics2D graphics2D = scaledImage.createGraphics();
            graphics2D.drawImage(imageToScale, 0, 0, dWidth, dHeight, null);
            graphics2D.dispose();
        }
        return scaledImage;
    }

}
