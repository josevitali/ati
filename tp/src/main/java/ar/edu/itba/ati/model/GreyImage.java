package ar.edu.itba.ati.model;

import javafx.scene.paint.Color;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;

// TODO: cambiar a Image<Double>? porque despues el tratamiento necesitas que sean doubles.
public class GreyImage extends Image<Integer> {

    public GreyImage(BufferedImage bufferedImage) {
        super(bufferedImage.getType(), createMatrix(bufferedImage), bufferedImage.getHeight(), bufferedImage.getWidth());
    }

    private static Integer[][] createMatrix(BufferedImage bufferedImage) {
        Integer[][] matrix = new Integer[bufferedImage.getHeight()][bufferedImage.getWidth()];
        byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();

        for(int row = 0, pixel = 0; row < bufferedImage.getHeight(); row++){
            for(int col = 0; col < bufferedImage.getWidth(); col++){
                matrix[row][col] = (int) pixels[pixel++] & 0xff;
            }
        }

        return matrix;
    }

    public BufferedImage toBufferedImage() {
        BufferedImage bufferedImage = new BufferedImage(width, height, type);
        WritableRaster wr = bufferedImage.getRaster();
        for(int row = 0; row < height; row++){
            for(int col = 0; col < width; col++){
                wr.setSample(col, row, 0, matrix[row][col]);
            }
        }
        return bufferedImage;
    }
}
