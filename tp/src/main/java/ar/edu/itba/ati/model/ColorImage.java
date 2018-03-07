package ar.edu.itba.ati.model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

public class ColorImage extends Image<Double[]> {

    private static final int BLUE = 0;
    private static final int GREEN = 1;
    private static final int RED = 2;

    public ColorImage(BufferedImage bufferedImage) {
        super(bufferedImage.getType(), createMatrix(bufferedImage), bufferedImage.getHeight(), bufferedImage.getWidth());
    }

    private static Double[][][] createMatrix(BufferedImage bufferedImage) {
        Double[][][] matrix = new Double[bufferedImage.getHeight()][bufferedImage.getWidth()][3];
        byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();

        for(int row = 0, pixel = 0; row < bufferedImage.getHeight(); row++){
            for(int col = 0; col < bufferedImage.getWidth(); col++){
                double blue = (double) (pixels[pixel++] & 0xff);
                double green = (double) (pixels[pixel++] & 0xff);
                double red = (double) (pixels[pixel++] & 0xff);
                matrix[row][col] = new Double[]{blue,green,red};
            }
        }

        return matrix;
    }

    public Double[] getAverageColor(int row1, int col1, int row2, int col2) {
        int amount = (1 + row2 - row1) * (1 + col2 - col1);
        Double[] avg = new Double[]{new Double(0), new Double(0), new Double(0)};

        for(int row = row1; row <= row2; row++){
            for(int col = col1; col <= col2; col++){
                avg[BLUE] += matrix[row][col][BLUE];
                avg[GREEN] += matrix[row][col][GREEN];
                avg[RED] += matrix[row][col][RED];
            }
        }

        avg[BLUE] /= amount;
        avg[GREEN] /= amount;
        avg[RED] /= amount;

        return avg;
    }

    public BufferedImage toBufferedImage() {
        BufferedImage bufferedImage = new BufferedImage(width, height, type);
        WritableRaster wr = bufferedImage.getRaster();
        for(int row = 0; row < height; row++){
            for(int col = 0; col < width; col++){
                wr.setSample(col, row, 0, matrix[row][col][RED]);
                wr.setSample(col, row, 1, matrix[row][col][GREEN]);
                wr.setSample(col, row, 2, matrix[row][col][BLUE]);
            }
        }
        return bufferedImage;
    }
}
