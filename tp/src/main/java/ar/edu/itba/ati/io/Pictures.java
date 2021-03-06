package ar.edu.itba.ati.io;

import ar.edu.itba.ati.model.pictures.ColorPicture;
import ar.edu.itba.ati.model.pictures.GreyPicture;
import ar.edu.itba.ati.model.pictures.Picture;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

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
        final String extension = FilenameUtils.getExtension(file.getName()).toLowerCase();
        BufferedImage bufferedImage = null;

        switch(extension){
            case "pgm":
            case "ppm":
                bufferedImage = ppmPgmToBufferedImage(file);
                break;
            case "bmp":
            case "jpg":
            case "jpeg":
                bufferedImage = bmpToBufferedImage(file);
                break;
            case "raw":
                bufferedImage = rawToBufferedImage(file);
                break;
            default:
                throw new IllegalStateException("Extension " + extension + " not supported.");
        }

        final int type = bufferedImage.getType();

        if(type == BufferedImage.TYPE_BYTE_GRAY){
            return new GreyPicture(bufferedImage);
        } else if(type == BufferedImage.TYPE_3BYTE_BGR){
            return new ColorPicture(bufferedImage);
        }

        throw new IllegalStateException("Picture type (" + bufferedImage.getType() + ") not supported.");
    }

    private static BufferedImage ppmPgmToBufferedImage(File file){
        Mat mat = Highgui.imread(file.getAbsolutePath(), -1);
        return matToBufferedImage(mat);
    }

    private static BufferedImage bmpToBufferedImage(File file) throws IOException {
        return ImageIO.read(file);
    }

    private static BufferedImage rawToBufferedImage(File file) throws IOException {
        String fileName = FilenameUtils.getFullPath(file.getPath()) + FilenameUtils.getBaseName(file.getName());
        BufferedReader reader = new BufferedReader(new FileReader(fileName + ".data"));
        int width = Integer.parseInt(reader.readLine());
        int height = Integer.parseInt(reader.readLine());
        int imageType = Integer.parseInt(reader.readLine());
        BufferedImage bufferedImage = new BufferedImage(width, height, imageType);
        byte[] array = IOUtils.toByteArray(new FileInputStream(file));
        bufferedImage.getRaster().setDataElements(0,0,width,height,array);
        return bufferedImage;
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

    public static void save(Picture picture, File file) throws IOException {

        String extension = FilenameUtils.getExtension(file.getAbsolutePath());

        switch (extension.toLowerCase()){
            case "pgm":
            case "ppm":
                savePgmAndPpm(picture, file);
                break;
            case "bmp":
            case "jpg":
            case "jpeg":
                saveBmp(picture, file);
                break;
            case "raw":
                saveRaw(picture, file);
                break;
            default:
                throw new IllegalStateException("Extension " + extension + " not supported.");
        }
    }

    private static void saveBmp(Picture picture, File file) {
        try {
            ImageIO.write(picture.getNormalizedClone().toBufferedImage(), "bmp", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void savePgmAndPpm(Picture picture, File file) {
        BufferedImage bufferedImage = picture.getNormalizedClone().toBufferedImage();
        byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        Mat mat;
        switch(picture.getType()) {
            case BufferedImage.TYPE_3BYTE_BGR:
                mat = new Mat(bufferedImage.getHeight(),bufferedImage.getWidth(), CvType.CV_8UC3);
                break;
            case BufferedImage.TYPE_BYTE_GRAY:
                mat = new Mat(bufferedImage.getHeight(),bufferedImage.getWidth(), CvType.CV_8UC1);
                break;
            default:
                throw new IllegalStateException("Image type " + picture.getType() + " not supported.");
        }
        mat.put(0, 0, data);
        try {
            Highgui.imwrite(file.getCanonicalPath(), mat);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveRaw(Picture picture, File file) {
        BufferedImage bufferedImage = picture.getNormalizedClone().toBufferedImage();
        String fileName = FilenameUtils.getFullPath(file.getPath()) + FilenameUtils.getBaseName(file.getName());
        String dataFileName = fileName + ".data";
        String rawFileName = fileName + ".RAW";
        String width = String.valueOf(bufferedImage.getWidth());
        String height = String.valueOf(bufferedImage.getHeight());
        String type = String.valueOf(bufferedImage.getType());

        List<String> lines = Arrays.asList(width, height, type);
        Path dataFile = Paths.get(dataFileName);

        byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        Path rawFile = Paths.get(rawFileName);

        try {
            Files.write(dataFile, lines, Charset.forName("UTF-8"));
            Files.write(rawFile, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
