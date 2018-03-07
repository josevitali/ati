package ar.edu.itba.ati.io;

import ar.edu.itba.ati.model.ColorImage;
import ar.edu.itba.ati.model.GreyImage;
import ar.edu.itba.ati.model.Image;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Images {

    public static Image getImage(String path) throws IOException {
//        TODO testear que ande
        File file = new File(path);
        if(file == null){
            return null;
        }
        return getImage(file);
    }

    public static Image getImage(File file) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(file);

//        TODO: agregar diferentes tipos de imagenes
        if(bufferedImage.getType() == BufferedImage.TYPE_BYTE_GRAY){
            return new GreyImage(bufferedImage, file);
        } else if(bufferedImage.getType() == BufferedImage.TYPE_3BYTE_BGR){
            return new ColorImage(bufferedImage, file);
        }

//        TODO: esta bien que tire excepcion?
        throw new IllegalStateException("Image type (" + bufferedImage.getType() + ") not supported.");
    }

    public static void save(Image image, File file){
        String formatName = FilenameUtils.getExtension(image.getFile().getAbsolutePath());
        try {
            ImageIO.write(image.toBufferedImage(), formatName, image.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
