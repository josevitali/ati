package ar.edu.itba.ati.model.transformations.threshold;

import ar.edu.itba.ati.model.pictures.Picture;

public interface ThresholdCriteria {

    /**
     * Calculates a threshold for a given picture.
     *
     * @param picture
     * @return threshold value
     */
    double getThreshold(final Picture picture);

}
