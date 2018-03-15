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


    public void crop(int x0, int x1, int y0, int y1){
        System.out.println("current height and width " + height + " " + width);
        Double[][] newpic = new Double[x1-x0][y1-y0];
        for (int i = x0, i2 = 0; i < x1-1; i++, i2++) {
            for (int j = y0, j2 = 0; j < y1-1; j++, j2++) {
                newpic[i2][j2] = matrix[i][j];
            }
        }
        this.matrix = newpic;
        this.width = y1-y0-1;
        this.height = x1-x0-1;
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
