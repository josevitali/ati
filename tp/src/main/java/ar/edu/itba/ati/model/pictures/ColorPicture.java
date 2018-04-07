package ar.edu.itba.ati.model.pictures;

import ar.edu.itba.ati.model.histograms.Histogram;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ColorPicture extends Picture<Double[]> {

    public static final int BLUE = 0;
    public static final int GREEN = 1;
    public static final int RED = 2;

    public ColorPicture(BufferedImage bufferedImage) {
        super(bufferedImage.getType(), createMatrix(bufferedImage), bufferedImage.getHeight(),
                bufferedImage.getWidth());
    }

    public ColorPicture(int type, Double[][][] matrix, int height, int width){
        super(type, matrix, height, width);
        if(matrix.length != height || matrix[0].length != width){
            throw new IllegalArgumentException("Dimensions of the matrix don't match with the specified height or width");
        }
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

    public String getAverageColor(int row1, int col1, int row2, int col2) {
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
        return "Blue: " + avg[BLUE] + "\tGreen: " + avg[GREEN] + "\tRed: " + avg[RED];
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

    @Override
    protected Double[] mapPixel(Function<Double, Double> f, Double[] pixel) {
        pixel[BLUE] = f.apply(pixel[BLUE]);
        pixel[GREEN] = f.apply(pixel[GREEN]);
        pixel[RED] = f.apply(pixel[RED]);
        return pixel;
    }

    public void mapPixelByPixelInSpecificBand(Function<Double, Double> f, int band){
        for(int row = 0; row < height; row++){
            for(int col = 0; col < width; col++){
                matrix[row][col][band] = f.apply(matrix[row][col][band]);
            }
        }
    }

    public void crop(int x0, int x1, int y0, int y1){
        Double[][][] newpic = new Double[x1-x0+1][y1-y0+1][];
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
    public Double[] subMatrixOperation(int firstRow, int firstCol, int height, int width, Function<Double[][], Double> f) {
        final Double[][][] subMatrix = getSubMatrix(firstRow, firstCol, height, width);
        final Double[][] blueSubMatrix = Arrays.stream(subMatrix).map(row -> Arrays.stream(row).map(px -> px[BLUE]).toArray(size -> new Double[size])).toArray(size -> new Double[size][]);
        final Double[][] greenSubMatrix = Arrays.stream(subMatrix).map(row -> Arrays.stream(row).map(px -> px[GREEN]).toArray(size -> new Double[size])).toArray(size -> new Double[size][]);
        final Double[][] redSubMatrix = Arrays.stream(subMatrix).map(row -> Arrays.stream(row).map(px -> px[RED]).toArray(size -> new Double[size])).toArray(size -> new Double[size][]);
        Double[] ret = new Double[3];
        ret[BLUE] = f.apply(blueSubMatrix);
        ret[GREEN] = f.apply(greenSubMatrix);
        ret[RED] = f.apply(redSubMatrix);
        return ret;
    }

    @Override
    protected Double[][][] tMatrix(int height, int width) {
        return new Double[height][width][3];
    }

    @Override
    public Double[] getMaxPixel() {
        Double[] ret = new Double[3];
        ret[BLUE] = getMaxPixelByBand(BLUE);
        ret[GREEN] = getMaxPixelByBand(GREEN);
        ret[RED] = getMaxPixelByBand(RED);
        return ret;
    }

    @Override
    public Double[] getMinPixel() {
        Double[] ret = new Double[3];
        ret[BLUE] = getMinPixelByBand(BLUE);
        ret[GREEN] = getMinPixelByBand(GREEN);
        ret[RED] = getMinPixelByBand(RED);
        return ret;
    }

    @Override
    protected Double[] mapPixel(BiFunction<Double, Double, Double> bf, Double[] myPixel, Double[] otherPixel) {
        myPixel[BLUE] = bf.apply(myPixel[BLUE], otherPixel[BLUE]);
        myPixel[GREEN] = bf.apply(myPixel[GREEN], otherPixel[GREEN]);
        myPixel[RED] = bf.apply(myPixel[RED], otherPixel[RED]);
        return myPixel;
    }

    @Override
    public void normalize() {
        normalizeBand(BLUE);
        normalizeBand(GREEN);
        normalizeBand(RED);
    }

    @Override
    public Picture getClone() {
        return new ColorPicture(type, duplicateMatrix(), height, width);
    }

    @Override
    public Histogram getHistogram() {
        final int minBlue = (int) Math.floor(getMinPixelByBand(BLUE));
        final int maxBlue = (int) Math.floor(getMaxPixelByBand(BLUE));
        final int minGreen = (int) Math.floor(getMinPixelByBand(GREEN));
        final int maxGreen = (int) Math.floor(getMaxPixelByBand(GREEN));
        final int minRed = (int) Math.floor(getMinPixelByBand(RED));
        final int maxRed = (int) Math.floor(getMaxPixelByBand(RED));
        final int min = minBlue < minGreen && minBlue < minRed ? minBlue :
                (minGreen < minRed ? minGreen : minRed);
        final int max = maxBlue > maxGreen && maxBlue > maxRed ? maxBlue :
                (maxGreen > maxRed ? maxGreen : maxRed);
        double[] blueValues = new double[max - min + 1];
        double[] redValues = new double[max - min + 1];
        double[] greenValues = new double[max - min + 1];

        double blueAccum = 0;
        double greenAccum = 0;
        double redAccum = 0;

        for(Double[][] row : matrix){
            for(Double[] pixel : row){
                blueValues[((int) Math.floor(pixel[BLUE])) - min]++;
                greenValues[((int) Math.floor(pixel[GREEN])) - min]++;
                redValues[((int) Math.floor(pixel[RED])) - min]++;

                blueAccum += pixel [BLUE];
                redAccum += pixel[RED];
                greenAccum += pixel[GREEN];
            }
        }

        int size = (matrix.length * matrix[0].length);
        final double blueAvg = blueAccum / size;
        final double greenAvg = greenAccum / size;
        final double redAvg = redAccum / size;
        double[] average = new double[3];
        average[BLUE] = blueAvg;
        average[GREEN] = greenAvg;
        average[RED] = redAvg;

        double blueAccumSq = 0;
        double greenAccumSq = 0;
        double redAccumSq = 0;

        for(Double[][] row : matrix){
            for(Double[] pixel : row){
                blueAccumSq += (pixel[BLUE] - blueAvg) * (pixel[BLUE] - blueAvg);
                greenAccumSq += (pixel[GREEN] - greenAvg) * (pixel[GREEN] - greenAvg);
                redAccumSq += (pixel[RED] - redAvg) * (pixel[RED] - redAvg);
            }
        }


        final double blueVar = Math.sqrt((1.0 / size) * blueAccumSq);
        final double greenVar = Math.sqrt((1.0 / size) * greenAccumSq);
        final double redVar = Math.sqrt((1.0 / size) * redAccumSq);
        double[] variance = new double[3];
        variance[BLUE] = blueVar;
        variance[GREEN] = greenVar;
        variance[RED] = redVar;

        for(int i = 0; i < blueValues.length; i++) {
            blueValues[i] /= (matrix.length * matrix[0].length);
            greenValues[i] /= (matrix.length * matrix[0].length);
            redValues[i] /= (matrix.length * matrix[0].length);
        }

        String[] categories = new String[blueValues.length];
        for(int i = min; i <= max; i++){
            categories[i - min] = new Integer(i).toString();
        }
        Map<String,double[]> series = new HashMap<>();
        series.put("Blue", blueValues);
        series.put("Green", greenValues);
        series.put("Red", redValues);

        return new Histogram(categories, series, average, variance);
    }

    private void normalizeBand(int band) {
        final double max = getMaxPixelByBand(band);
        final double min = getMinPixelByBand(band);
        final double m = 255 / (max - min);
        final double b = - min * m;
        for(Double[][] row : matrix){
            for(Double[] pixel : row){
                pixel[band] = m * pixel[band] + b;
            }
        }
    }

    private double getMaxPixelByBand(int band) {
        double max = matrix[0][0][band];
        for(Double[][] row : matrix){
            for(Double[] pixel : row){
                if(pixel[band] > max)
                    max = pixel[band];
            }
        }
        return max;
    }

    private double getMinPixelByBand(int band) {
        double min = matrix[0][0][band];
        for(Double[][] row : matrix){
            for(Double[] pixel : row){
                if(pixel[band] < min)
                    min = pixel[band];
            }
        }
        return min;
    }

    private Double[][][] duplicateMatrix(){
        Double[][][] otherMatrix = new Double[height][width][3];
        for(int row = 0; row < height; row++){
            for(int col = 0; col < width; col ++){
                Double[] pixel = new Double[3];
                pixel[BLUE] = matrix[row][col][BLUE].doubleValue();
                pixel[GREEN] = matrix[row][col][GREEN].doubleValue();
                pixel[RED] = matrix[row][col][RED].doubleValue();
                otherMatrix[row][col] = pixel;
            }
        }
        return otherMatrix;
    }
}
