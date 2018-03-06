package ar.edu.itba.ati.model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

// TODO: cambiar a Image<Double>? porque despues el tratamiento necesitas que sean doubles.
public class ColorImage extends Image<Color> {
    public ColorImage(BufferedImage bufferedImage) {
        super(bufferedImage.getType(), createMatrix(bufferedImage), bufferedImage.getHeight(), bufferedImage.getWidth());
    }

    private static Color[][] createMatrix(BufferedImage bufferedImage) {
        Color[][] matrix = new Color[bufferedImage.getHeight()][bufferedImage.getWidth()];
        byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();

        for(int row = 0, pixel = 0; row < bufferedImage.getHeight(); row++){
            for(int col = 0; col < bufferedImage.getWidth(); col++){
                int blue = (int) pixels[pixel++] & 0xff;
                int green = (int) pixels[pixel++] & 0xff;
                int red = (int) pixels[pixel++] & 0xff;
                matrix[row][col] = new Color(red, green, blue);
            }
        }

        return matrix;
    }

    public BufferedImage toBufferedImage() {
        BufferedImage bufferedImage = new BufferedImage(width, height, type);
        WritableRaster wr = bufferedImage.getRaster();
        for(int row = 0; row < height; row++){
            for(int col = 0; col < width; col++){
                wr.setSample(col, row, 0, matrix[row][col].getRed());
                wr.setSample(col, row, 1, matrix[row][col].getGreen());
                wr.setSample(col, row, 2, matrix[row][col].getBlue());
            }
        }
        return bufferedImage;
    }
}
