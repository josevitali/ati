package ar.edu.itba.ati.model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

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
}
