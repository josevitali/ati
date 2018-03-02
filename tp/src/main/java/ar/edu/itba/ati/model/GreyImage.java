package ar.edu.itba.ati.model;

import java.awt.image.BufferedImage;

public class GreyImage extends Image<Integer> {

    public GreyImage(BufferedImage bufferedImage) {
//        TODO: change null matrix
        super(bufferedImage.getType(), null, bufferedImage.getHeight(), bufferedImage.getWidth());
    }
}
