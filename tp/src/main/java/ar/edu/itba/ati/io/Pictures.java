package ar.edu.itba.ati.io;

import ar.edu.itba.ati.model.ColorPicture;
import ar.edu.itba.ati.model.GreyPicture;
import ar.edu.itba.ati.model.Picture;
import org.apache.commons.io.FilenameUtils;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;

public class Pictures {

    static {
        nu.pattern.OpenCV.loadShared();
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
    }

    public static Picture getPicture(String path) throws IOException {
//        TODO testear que ande
        File file = new File(path);
        if(file == null){
            return null;
        }
        return getPicture(file);
    }

    public static Picture getPicture(File file) throws IOException {
        final String extension = FilenameUtils.getExtension(file.getName());
        BufferedImage bufferedImage = null;

        switch(extension){
            case "PGM":
            case "pgm":
            case "PPM":
            case "ppm":
                Mat mat = Highgui.imread(file.getAbsolutePath());
                bufferedImage = matToBufferedImage(mat);
                break;
            case "BMP":
            case "bmp":
                bufferedImage = ImageIO.read(file);
                break;
//                TODO
//            case "RAW":
//            case "raw":
//
//                break;
            default:
                throw new IllegalStateException("Extension " + extension + " not supported.");
        }

        final int type = bufferedImage.getType();

        if(type == BufferedImage.TYPE_BYTE_GRAY){
            return new GreyPicture(bufferedImage);
        } else if(type == BufferedImage.TYPE_3BYTE_BGR){
            return new ColorPicture(bufferedImage);
        }

        // TODO: esta bien que tire excepcion?
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

    private static BufferedImage matToBufferedImage(Mat mat) {
        int type = mat.channels() > 1 ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_BYTE_GRAY;
        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] buffer = new byte[bufferSize];

        mat.get(0, 0, buffer);
        BufferedImage bufferedImage = new BufferedImage(mat.cols(), mat.rows(), type);
        byte[] targetPixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return bufferedImage;

    }
}
