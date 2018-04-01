package ar.edu.itba.ati.model.transformations;

import ar.edu.itba.ati.model.histograms.Histogram;
import ar.edu.itba.ati.model.pictures.ColorPicture;
import ar.edu.itba.ati.model.pictures.GreyPicture;
import ar.edu.itba.ati.model.pictures.Picture;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.function.Function;

public class Contrast implements PictureTransformer {

    private final Double s1;
    private final Double s2;

    public Contrast(double s1, double s2) {
        this.s1 = s1;
        this.s2 = s2;
    }

    public Contrast(){
        this.s1 = null;
        this.s2 = null;
    }

    @Override
    public <T> void transform(Picture<T> picture) {

        switch (picture.getType()){

            case BufferedImage.TYPE_3BYTE_BGR:
                addContrastColorImage((ColorPicture) picture);
                break;

            case BufferedImage.TYPE_BYTE_GRAY:
                addContrastGreyImage((GreyPicture) picture);
                break;

            default:
                throw new IllegalArgumentException("The image type is not supported");
        }
    }

    private void addContrastColorImage(ColorPicture picture){
        final Histogram histogram = picture.getHistogram();
        final Double[] minValues = picture.getMinPixel();
        final Double[] maxValues = picture.getMaxPixel();
        final double[] avgs = histogram.getAverage();
        final double[] vars = histogram.getVariance();
        double[] r1s = new double[3];
        double[] r2s = new double[3];


        for (int i = 0; i < 3; i++) {
            r1s[i] = avgs[i] - vars[i];
            r2s[i] = avgs[i] + vars[i];

            if(r1s[i] < minValues[i] || r2s[i] > maxValues[i]){
                r1s[i] = avgs[i] - vars[i] / 2;
                r2s[i] = avgs[i] + vars[i] / 2;
            }
        }

        final double r1 = Arrays.stream(r1s).average().getAsDouble();
        final double r2 = Arrays.stream(r2s).average().getAsDouble();
        final double min = Arrays.stream(minValues).min(Double::compareTo).get();
        final double max = Arrays.stream(maxValues).max(Double::compareTo).get();

        Double s1 = this.s1 == null ? (min + r1) / 2 : this.s1;
        Double s2 = this.s2 == null ? (max + r2) / 2 : this.s2;

        picture.mapPixelByPixel(px -> addContrastToPixel(px, r1, r2, s1, s2, min, max));

    }

    private void addContrastGreyImage(GreyPicture picture){
        final Histogram histogram = picture.getHistogram();
        double average = histogram.getAverage()[0];
        double variance = histogram.getVariance()[0];
        double x1 = average - variance;
        double x2 = average + variance;
        double min = picture.getMinPixel();
        double max = picture.getMaxPixel();

        if(x1 < min || x2 > max){
            x1 = average - variance / 2;
            x2 = average + variance / 2;
        }

        double r1 = x1;
        double r2 = x2;

        Double s1 = this.s1 == null ? (min + r1) / 2 : this.s1;
        Double s2 = this.s2 == null ? (max + r2) / 2 : this.s2;

        picture.mapPixelByPixel(px -> addContrastToPixel(px, r1, r2, s1, s2, min, max));
    }

    private double addContrastToPixel(final double pixel, final double r1, final double r2,
                                      final double s1, final double s2,
                                      final double minValue, final double maxValue){

        final Function<Double,Double> f1 = linealTransformation(minValue, minValue, r1, s1);
        final Function<Double,Double> f2 = linealTransformation(r1, s1, r2, s2);
        final Function<Double,Double> f3 = linealTransformation(r2, s2, maxValue, maxValue);


        if(pixel > r2){
            return f3.apply(pixel);
        }else if(pixel > r1){
            return f2.apply(pixel);
        }else{
            return f1.apply(pixel);
        }
    }

    private Function<Double,Double> linealTransformation(final double x1, final double y1, final double x2, final double y2){
        final double m = (y2 - y1) / (x2 - x1);
        final double b = y2 - m * x2;
        return (x -> m * x + b);
    }


}
