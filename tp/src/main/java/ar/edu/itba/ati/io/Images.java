package ar.edu.itba.ati.io;

import ar.edu.itba.ati.model.GreyImage;
import ar.edu.itba.ati.model.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Images {

    public static Image newImage(String direction) throws IOException {
        File file = new File(direction);
        BufferedImage bufferedImage = ImageIO.read(file);

//        TODO: add different types images
        if(bufferedImage.getType() == BufferedImage.TYPE_BYTE_GRAY){
            return new GreyImage(bufferedImage);
        }

        return null;
    }
}
