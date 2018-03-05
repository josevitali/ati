package ar.edu.itba.ati.model;

import ar.edu.itba.ati.model.GreyImage;
import ar.edu.itba.ati.model.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Images {

    public static Image getImage(File file) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(file);

//        TODO: agregar diferentes tipos de imagenes
        if(bufferedImage.getType() == BufferedImage.TYPE_BYTE_GRAY){
            return new GreyImage(bufferedImage);
        }

//        TODO: esta bien que tire excepcion?
        throw new IllegalStateException("Image type not supported.");
    }
}
