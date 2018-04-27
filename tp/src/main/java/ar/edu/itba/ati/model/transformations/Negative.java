package ar.edu.itba.ati.model.transformations;

import ar.edu.itba.ati.model.pictures.ColorPicture;
import ar.edu.itba.ati.model.pictures.GreyPicture;
import ar.edu.itba.ati.model.pictures.Picture;

import java.awt.image.BufferedImage;

public class Negative implements PictureTransformer {
    @Override
    public <T> void transform(Picture<T> picture) {
        if(picture.getType() == BufferedImage.TYPE_BYTE_GRAY){
            greyNegative((GreyPicture) picture);
        } else if(picture.getType() == BufferedImage.TYPE_3BYTE_BGR){
            colorNegative((ColorPicture) picture);
        }
    }

    private void colorNegative(ColorPicture picture) {
        Double[] max = picture.getMaxPixel();
        picture.mapPixelByPixelInSpecificBand((p -> max[ColorPicture.BLUE] - p), ColorPicture.BLUE);
        picture.mapPixelByPixelInSpecificBand((p -> max[ColorPicture.GREEN] - p), ColorPicture.GREEN);
        picture.mapPixelByPixelInSpecificBand((p -> max[ColorPicture.RED] - p), ColorPicture.RED);
    }

    private void greyNegative(GreyPicture picture) {
        Double max = picture.getMaxPixel();
        picture.mapPixelByPixel(p -> max - p);
    }
}
