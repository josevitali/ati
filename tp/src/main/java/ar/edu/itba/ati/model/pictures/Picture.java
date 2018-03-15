package ar.edu.itba.ati.model.pictures;

import java.awt.image.BufferedImage;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class Picture<T> {

    protected final int type;
    protected T[][] matrix;
    protected final int height;
    protected final int width;

    public Picture(int type, T[][] matrix, int height, int width) {
        this.type = type;
        this.matrix = matrix;
        this.height = height;
        this.width = width;
    }

    public T getPixel(int row, int column){
        return matrix[row][column];
    }

    public void putPixel(T pixel, int row, int column){
        matrix[row][column] = pixel;
    }

    public abstract T getAverageColor(int row1, int col1, int row2, int col2);

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public abstract BufferedImage toBufferedImage();

    public void mapPixelByPixel(Function<Double,Double> f){
        for(int row = 0; row < height; row++){
            for(int col = 0; col < width; col++){
                matrix[row][col] = mapPixel(f, matrix[row][col]);
            }
        }
    }

    protected abstract T mapPixel(Function<Double,Double>f, T pixel);

    public void mapPixelByPixel(BiFunction<Double,Double,Double> bf, Picture otherPicture){
        final int minHeight = height <= otherPicture.getHeight() ? height : otherPicture.getHeight();
        final int minWidth = width <= otherPicture.getWidth() ? width : otherPicture.getWidth();

        for(int row = 0; row < minHeight; row++){
            for(int col = 0; col < minWidth; col++){
                matrix[row][col] = mapPixel(bf, matrix[row][col], (T) otherPicture.getPixel(row,col));
            }
        }
    }

    protected abstract T mapPixel(BiFunction<Double, Double, Double> bf, T myPixel, T otherPixel);

    public abstract void normalize();



}