package ar.edu.itba.ati.model.transformations;

import ar.edu.itba.ati.model.pictures.ColorPicture;
import ar.edu.itba.ati.model.pictures.GreyPicture;
import ar.edu.itba.ati.model.pictures.Picture;

import java.awt.image.BufferedImage;

public class GreyColorToggle implements PictureTransformer {
    @Override
    public <T, R> Picture<R> transform(Picture<T> picture) {
        Picture newPicture = null;
        if(picture.getType() == BufferedImage.TYPE_BYTE_GRAY){
            newPicture = ((GreyPicture) picture).toColorPicture();
        } else if(picture.getType() == BufferedImage.TYPE_3BYTE_BGR){
            newPicture = ((ColorPicture) picture).toGreyPicture();
        }
        return newPicture;
    }
}
