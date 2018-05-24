package ar.edu.itba.ati.model.transformations;

import ar.edu.itba.ati.model.pictures.ColorPicture;
import ar.edu.itba.ati.model.pictures.GreyPicture;
import ar.edu.itba.ati.model.pictures.Picture;

import java.awt.image.BufferedImage;

public class DynamicRange implements PictureTransformer {
    @Override
    public <T,R> Picture transform(Picture<T> picture) {
        if(picture.getType() == BufferedImage.TYPE_BYTE_GRAY){
            greyDynamicRange((GreyPicture) picture);
        } else if(picture.getType() == BufferedImage.TYPE_3BYTE_BGR){
            colorDynamicRange((ColorPicture) picture);
        }
        return picture;
    }

    private void greyDynamicRange(GreyPicture picture) {
        Double amountOfGreys = picture.getMaxPixel() - picture.getMinPixel();
        double productScalar = 5.0;
        picture.mapPixelByPixel(px -> productScalar * (double) px);
        picture.mapPixelByPixel(p -> (amountOfGreys - 1) / Math.log(1 + amountOfGreys) * Math.log(1 + p));
    }

    private void colorDynamicRange(ColorPicture picture) {
        Double[] max = picture.getMaxPixel();
        Double[] min = picture.getMinPixel();
        Double amountOfBlues = max[ColorPicture.BLUE] - min[ColorPicture.BLUE];
        Double amountOfGreens = max[ColorPicture.GREEN] - min[ColorPicture.GREEN];
        Double amountOfReds = max[ColorPicture.RED] - min[ColorPicture.RED];
        double productScalar = 5.0;

        picture.mapPixelByPixel(px -> productScalar * px);
        picture.mapPixelByPixelInSpecificBand(p -> (amountOfBlues - 1) / Math.log(1 + amountOfBlues) * Math.log(1 + p), ColorPicture.BLUE);
        picture.mapPixelByPixelInSpecificBand(p -> (amountOfGreens - 1) / Math.log(1 + amountOfGreens) * Math.log(1 + p), ColorPicture.GREEN);
        picture.mapPixelByPixelInSpecificBand(p -> (amountOfReds - 1) / Math.log(1 + amountOfReds) * Math.log(1 + p), ColorPicture.RED);
    }
}
