package ar.edu.itba.ati.model.transformations.threshold;

import ar.edu.itba.ati.model.pictures.GreyPicture;
import ar.edu.itba.ati.model.pictures.Picture;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class OtsuThreshold implements ThresholdCriteria {

    @Override
    public double getThreshold(Picture picture) {
        if(picture.getType() != BufferedImage.TYPE_BYTE_GRAY){
            throw new IllegalArgumentException(picture.getType() + " image type not supported. Only grey images ("
                    + BufferedImage.TYPE_BYTE_GRAY + ") are supported.");
        }

        GreyPicture greyPicture = (GreyPicture) picture;

        greyPicture.normalize();
        final double[] freqs = greyPicture.getHistogram().getSeries().get("Grey");
        final double[] cumulativeFreqs = new double[256];
        final double[] cumulativeMeans = new double[256];

        int i = 0;
        for(double freq : freqs){
            cumulativeFreqs[i] = i == 0 ? 0 : cumulativeFreqs[i - 1];
            cumulativeFreqs[i++] += freq;
        }

        for(i = 0; i < cumulativeMeans.length; i++){
            cumulativeMeans[i] = 0;
            for(int j = 0; j <= i; j++){
                cumulativeMeans[i] += j * freqs[j];
            }
        }

        final double globalMean = cumulativeMeans[255];

        double maxVariance = 0;
        List<Integer> ts = new ArrayList();
        for(i = 0; i < 256; i++){
            double variance = (globalMean * cumulativeFreqs[i] - cumulativeMeans[i]) * (globalMean * cumulativeFreqs[i] - cumulativeMeans[i]) /
                    (cumulativeFreqs[i] * (1 - cumulativeFreqs[i]));
            if(variance > maxVariance){
                ts.clear();
                ts.add(i);
                maxVariance = variance;
            } else if(variance == maxVariance){
                ts.add(i);
            }
        }

        return ts.stream().mapToDouble(d -> d).average().getAsDouble();
    }

}
