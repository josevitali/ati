package ar.edu.itba.ati.model.transformations.threshold;

import ar.edu.itba.ati.model.pictures.GreyPicture;
import ar.edu.itba.ati.model.pictures.Picture;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class GlobalThreshold implements ThresholdCriteria {

    private final double dt = 1;

    @Override
    public double getThreshold(final Picture picture) {
        if(picture.getType() != BufferedImage.TYPE_BYTE_GRAY){
            throw new IllegalArgumentException(picture.getType() + " image type not supported. Only grey images ("
                    + BufferedImage.TYPE_BYTE_GRAY + ") are supported.");
        }

        GreyPicture greyPicture = (GreyPicture) picture;

        // TODO: make getAverage at Picture
        double oldT = greyPicture.getHistogram().getAverage()[0];
        double t = oldT + 2 * dt;
        final Double[][] matrix = greyPicture.getMatrix();

        while (Math.abs(oldT - t) >= dt){
            oldT = t;
            t = calculateT(oldT, matrix);
        }

        return t;
    }

    private double calculateT(final double oldT, final Double[][] matrix){
        final List<Double> g1 = new ArrayList();
        final List<Double> g2 = new ArrayList();

        for(Double[] row : matrix){
            for(double pixel : row){
                if(pixel < oldT){
                    g1.add(pixel);
                } else {
                    g2.add(pixel);
                }
            }
        }

        final double m1 = g1.stream().mapToDouble(i -> i).average()
                .orElseThrow(() -> new RuntimeException("This should not happen"));
        final double m2 = g2.stream().mapToDouble(i -> i).average()
                .orElseThrow(() -> new RuntimeException("This should not happen"));

        return (m1 + m2) / 2;
    }

}
