package ar.edu.itba.ati.model.pictures;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.function.BiFunction;
import java.util.function.Function;

public class GreyPicture extends Picture<Double> {

    public GreyPicture(BufferedImage bufferedImage) {
        super(bufferedImage.getType(), createMatrix(bufferedImage), bufferedImage.getHeight(),
                bufferedImage.getWidth());
    }

    private static Double[][] createMatrix(BufferedImage bufferedImage) {
        Double[][] matrix = new Double[bufferedImage.getHeight()][bufferedImage.getWidth()];
        byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();

        for(int row = 0, pixel = 0; row < bufferedImage.getHeight(); row++){
            for(int col = 0; col < bufferedImage.getWidth(); col++){
                matrix[row][col] = (double) (pixels[pixel++] & 0xff);
            }
        }

        return matrix;
    }

    public Double getAverageColor(int row1, int col1, int row2, int col2) {
        int amount = (1 + row2 - row1) * (1 + col2 - col1);
        double avg = 0;

        for(int row = row1; row <= row2; row++){
            for(int col = col1; col <= col2; col++){
                avg += matrix[row][col];
            }
        }

        return avg / amount;
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

    @Override
    protected Double mapPixel(Function<Double, Double> f, Double pixel) {
        return f.apply(pixel);
    }

    @Override
    protected Double mapPixel(BiFunction<Double, Double, Double> bf, Double myPixel, Double otherPixel) {
        return bf.apply(myPixel, otherPixel);
    }

    @Override
    public void normalize() {
        final double max = maxPixel();
        final double min = minPixel();
        final double m = 255 / (max - min);
        final double b = - min * m;
        mapPixelByPixel(p -> m * p + b);
    }

    private double maxPixel() {
        double max = matrix[0][0];
        for(Double[] row : matrix){
            for(Double pixel : row){
                if(pixel > max)
                    max = pixel;
            }
        }
        return max;
    }

    private double minPixel() {
        double min = matrix[0][0];
        for(Double[] row : matrix){
            for(Double pixel : row){
                if(pixel < min)
                    min = pixel;
            }
        }
        return min;
    }

}
