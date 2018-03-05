package ar.edu.itba.ati.model;

import java.awt.image.BufferedImage;

public abstract class Image <T> {

    protected final int type;
    protected T[][] matrix;
    protected final int height;
    protected final int width;

    public Image(int type, T[][] matrix, int height, int width) {
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

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public abstract BufferedImage toBufferedImage();
}
