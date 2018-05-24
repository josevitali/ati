package ar.edu.itba.ati.model.transformations;


import ar.edu.itba.ati.model.pictures.ColorPicture;
import ar.edu.itba.ati.model.pictures.GreyPicture;
import ar.edu.itba.ati.model.pictures.Picture;

import java.awt.image.BufferedImage;

public class Gamma implements PictureTransformer {

    private final double gamma;

    public Gamma(double gamma) {
        this.gamma = gamma;
    }

    @Override
    public <T,R> Picture transform(Picture<T> picture) {
        if(picture.getType() == BufferedImage.TYPE_BYTE_GRAY){
            greyGamma((GreyPicture) picture);
        } else if(picture.getType() == BufferedImage.TYPE_3BYTE_BGR){
            colorGamma((ColorPicture) picture);
        }
        return picture;
    }

    private void greyGamma(GreyPicture picture) {
        Double amountOfGreys = picture.getMaxPixel() - picture.getMinPixel();
        picture.mapPixelByPixel(px -> Math.pow(amountOfGreys -1, 1 - this.gamma) * Math.pow(px, this.gamma));
    }

    private void colorGamma(ColorPicture picture) {
        Double[] max = picture.getMaxPixel();
        Double[] min = picture.getMinPixel();
        Double amountOfBlues = max[ColorPicture.BLUE] - min[ColorPicture.BLUE];
        Double amountOfGreens = max[ColorPicture.GREEN] - min[ColorPicture.GREEN];
        Double amountOfReds = max[ColorPicture.RED] - min[ColorPicture.RED];

        picture.mapPixelByPixelInSpecificBand(px -> Math.pow(amountOfBlues -1, 1 - this.gamma) * Math.pow(px, this.gamma),ColorPicture.BLUE);
        picture.mapPixelByPixelInSpecificBand(px -> Math.pow(amountOfGreens -1, 1 - this.gamma) * Math.pow(px, this.gamma),ColorPicture.GREEN);
        picture.mapPixelByPixelInSpecificBand(px -> Math.pow(amountOfReds -1, 1 - this.gamma) * Math.pow(px, this.gamma),ColorPicture.RED);
    }
}
