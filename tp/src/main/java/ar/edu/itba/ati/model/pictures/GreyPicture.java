package ar.edu.itba.ati.model.pictures;

import ar.edu.itba.ati.model.histograms.Histogram;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class GreyPicture extends Picture<Double> {

    public GreyPicture(BufferedImage bufferedImage) {
        super(bufferedImage.getType(), createMatrix(bufferedImage), bufferedImage.getHeight(),
                bufferedImage.getWidth());
    }

    public GreyPicture(int type, Double[][] matrix, int height, int width){
        super(type, matrix, height, width);
        if(matrix.length != height || matrix[0].length != width){
            throw new IllegalArgumentException("Dimensions of the matrix don't match with the specified height or width");
        }
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

    public String getAverageColor(int row1, int col1, int row2, int col2) {
        int amount = (1 + row2 - row1) * (1 + col2 - col1);
        double avg = 0;

        for(int row = row1; row <= row2; row++){
            for(int col = col1; col <= col2; col++){
                avg += matrix[row][col];
            }
        }

        return "Grey: " + Double.toString(avg/amount);
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
    public Double evaluatePixel(Function<Double,Double> f, Double pixel) {
        return f.apply(pixel);
    }

    @Override
    public Double evaluateTwoPixels(BiFunction<Double, Double, Double> bf, Double pixel, Double otherPixel) {
        return bf.apply(pixel, otherPixel);
    }

    @Override
    public Double evaluateThreePixels(Function<Double, Function<Double, Function<Double, Double>>> triFunction,
                                      Double firstPixel, Double secondPixel, Double thirdPixel) {
        double ret = triFunction.apply(firstPixel).apply(secondPixel).apply(thirdPixel);
        return ret;
    }

    public void crop(int x0, int x1, int y0, int y1){
        Double[][] newpic = new Double[x1-x0+1][y1-y0+1];
        for (int i = x0, i2 = 0; i <= x1; i++, i2++) {
            for (int j = y0, j2 = 0; j <= y1; j++, j2++) {
                newpic[i2][j2] = matrix[i][j];
            }
        }
        this.matrix = newpic;
        this.width = y1-y0+1;
        this.height = x1-x0+1;
    }

    @Override
    public Double subMatrixOperation(int firstRow, int firstCol, int height, int width, Function<Double[][], Double> f) {
        Double[][] subMatrix = getSubMatrix(firstRow, firstCol, height, width);
        return f.apply(subMatrix);
    }

    @Override
    protected Double[][] tMatrix(int height, int width) {
        return new Double[height][width];
    }

    @Override
    public Double mapPixel(BiFunction<Double, Double, Double> bf, Double myPixel, Double otherPixel) {
        return bf.apply(myPixel, otherPixel);
    }

    @Override
    public void normalize() {
        final double max = getMaxPixel();
        final double min = getMinPixel();
        final double m = 255 / (max - min);
        final double b = - min * m;
        mapPixelByPixel(p -> m * p + b);
    }

    @Override
    public Picture getClone() {
        return new GreyPicture(type, duplicateMatrix(), height, width);
    }

    @Override
    public Histogram getHistogram() {
        final int min = (int) Math.floor(getMinPixel());
        final int max = (int) Math.floor(getMaxPixel());
        double[] greyValues = new double[max - min + 1];

        int size = width * height;

        double accumsq = 0;
        double accum = 0;

        for(Double[] row : matrix){
            for(Double pixel : row){
                int n = ((int) Math.floor(pixel)) - min;
                greyValues[n]++;
                accum += pixel;
            }
        }

        double average = accum / size;

        for(Double[] row : matrix){
            for(Double pixel : row){
                accumsq += (average - pixel) * (average - pixel);
            }
        }

        double variance = Math.sqrt((1.0 / size) * accumsq);

        for(int i = 0; i < greyValues.length; i++) {
            greyValues[i] /= size;
        }

        String[] categories = new String[greyValues.length];
        for(int i = min; i <= max; i++){
            categories[i - min] = new Integer(i).toString();
        }

        Map<String,double[]> series = new HashMap<>();
        series.put("Grey", greyValues);

        double[] avgArray = new double[]{average};
        double[] varArray = new double[]{variance};
        return new Histogram(categories, series, avgArray, varArray);
    }

    @Override
    public Double getMaxPixel() {
        double max = matrix[0][0];
        for(Double[] row : matrix){
            for(Double pixel : row){
                if(pixel > max)
                    max = pixel;
            }
        }
        return max;
    }

    @Override
    public Double getMinPixel() {
        double min = matrix[0][0];
        for(Double[] row : matrix){
            for(Double pixel : row){
                if(pixel < min)
                    min = pixel;
            }
        }
        return min;
    }

    private Double[][] duplicateMatrix(){
        Double[][] otherMatrix = new Double[height][width];
        for(int row = 0; row < height; row++){
            for(int col = 0; col < width; col ++){
                Double pixel = matrix[row][col].doubleValue();
                otherMatrix[row][col] = pixel;
            }
        }
        return otherMatrix;
    }

    public ColorPicture toColorPicture(){
        Double[][][] colorMatrix = new Double[this.height][this.width][3];
        double px;
        for(int row = 0; row < this.height; row++){
            for(int col = 0; col < this.width; col++){
                px = matrix[row][col];
                colorMatrix[row][col] = new Double[]{px,px,px};
            }
        }
        return new ColorPicture(BufferedImage.TYPE_3BYTE_BGR, colorMatrix, this.height, this.width);
    }

}
