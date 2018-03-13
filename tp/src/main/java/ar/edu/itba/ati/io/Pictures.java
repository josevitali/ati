package ar.edu.itba.ati.io;

import ar.edu.itba.ati.model.ColorPicture;
import ar.edu.itba.ati.model.GreyPicture;
import ar.edu.itba.ati.model.Picture;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Pictures {

    public static Picture getPicture(String path) throws IOException {
//        TODO testear que ande
        File file = new File(path);
        if(file == null){
            return null;
        }
        return getPicture(file);
    }

    public static Picture getPicture(File file) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(file);
        return getPicture(bufferedImage);
    }

    public static Picture getPicture(BufferedImage bufferedImage) throws IOException {
        final int type = bufferedImage.getType();

        //        TODO: agregar diferentes tipos de imagenes
        if(type == BufferedImage.TYPE_BYTE_GRAY){
            return new GreyPicture(bufferedImage);
        } else if(type == BufferedImage.TYPE_3BYTE_BGR){
            return new ColorPicture(bufferedImage);
        }

//        TODO: esta bien que tire excepcion?
        throw new IllegalStateException("Picture type (" + bufferedImage.getType() + ") not supported.");
    }

    public static void save(Picture picture, File file){
        String formatName = FilenameUtils.getExtension(file.getAbsolutePath());
        try {
            ImageIO.write(picture.toBufferedImage(), formatName, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
