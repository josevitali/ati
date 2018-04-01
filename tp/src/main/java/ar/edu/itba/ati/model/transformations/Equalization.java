package ar.edu.itba.ati.model.transformations;


import ar.edu.itba.ati.model.histograms.Histogram;
import ar.edu.itba.ati.model.pictures.ColorPicture;
import ar.edu.itba.ati.model.pictures.GreyPicture;
import ar.edu.itba.ati.model.pictures.Picture;
import java.awt.image.BufferedImage;
import java.util.Map;

public class Equalization implements PictureTransformer {

    @Override
    public <T> void transform(Picture<T> picture) {
        switch (picture.getType()) {
            case BufferedImage.TYPE_3BYTE_BGR:
                equalizeColorHistogram((ColorPicture) picture);
                break;
            case BufferedImage.TYPE_BYTE_GRAY:
                equalizeGreyHistogram((GreyPicture) picture);
                break;
            default:
                throw new IllegalArgumentException("The image type is not supported");
        }
    }

    private void equalizeColorHistogram(ColorPicture picture) {
        Histogram histogram = picture.getHistogram();
        Map<String,double[]> series = histogram.getSeries();
        double[][] equalizedBands = new double[series.size()][];
        Double[] maxPixels = picture.getMaxPixel();
        Double[] minPixels = picture.getMinPixel();
        for (Map.Entry<String,double[]> bandFrequencies:series.entrySet()) {
            switch (bandFrequencies.getKey()) {
                case "Blue":
                    equalizedBands[0] = equalizeBand(bandFrequencies.getValue(), maxPixels[ColorPicture.BLUE]);
//                    picture.mapPixelByPixelInSpecificBand(px -> equalizePixel(px, equalizedBands[0], minPixels[ColorPicture.BLUE]), ColorPicture.BLUE);
                    break;
                case "Green":
                    equalizedBands[1] = equalizeBand(bandFrequencies.getValue(), maxPixels[ColorPicture.GREEN]);
//                    picture.mapPixelByPixelInSpecificBand(px -> equalizePixel(px, equalizedBands[1], minPixels[ColorPicture.GREEN]), ColorPicture.GREEN);
                    break;
                case "Red":
                    equalizedBands[2] = equalizeBand(bandFrequencies.getValue(), maxPixels[ColorPicture.RED]);
//                    picture.mapPixelByPixelInSpecificBand(px -> equalizePixel(px, equalizedBands[2], minPixels[ColorPicture.RED]), ColorPicture.RED);
                    break;
            }
        }
        double[] equalizedBandMean = new double[equalizedBands[0].length];
        for (int i = 0; i < equalizedBandMean.length; i++) {
            double value = 0;
            for (int j = 0; j < 3; j++) {
                value+= equalizedBands[j][i];
            }
            value/=3;
            equalizedBandMean[i] = value;
        }
        double n = 0;
        for (int i = 0; i < minPixels.length; i++) {
            n+= minPixels[i];
        }
        double averageMin = n / minPixels.length;
        picture.mapPixelByPixel(px -> equalizePixel(px, equalizedBandMean, averageMin));
    }

    private void equalizeGreyHistogram(GreyPicture picture) {
        Histogram histogram = picture.getHistogram();
        double[] frequencies = histogram.getSeries().get("Grey");
        double[] equalizedBand = equalizeBand(frequencies, picture.getMaxPixel());
        double minPixel = picture.getMinPixel();
        picture.mapPixelByPixel(px -> equalizePixel(px, equalizedBand, minPixel));
    }

    private double[] equalizeBand(double[] band, double maxPixel) {
        double[] equalizedBand = new double[band.length];
        double smin = band[0];
        double accumFrequency = 0.0;
        for (int i = 0; i < band.length; i++) {
            accumFrequency+= band[i];
            equalizedBand[i] = Math.floor((accumFrequency - smin) / (1 - smin) * maxPixel + 0.5);
        }
        return equalizedBand;
    }

    private double equalizePixel(double pixel, double[] equalizedBand, double minPixel) {
        int index = ((int) Math.floor(pixel)) - (int) Math.floor(minPixel);
        return equalizedBand[index];
    }
}
