package ar.edu.itba.ati.model;

import java.awt.image.BufferedImage;

public abstract class Image <T> {

    private final int type;
    private T[][] matrix;
    private final int height;
    private final int width;

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
}
