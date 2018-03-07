package ar.edu.itba.ati.model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public abstract class Image <T> {

    protected final int type;
    protected T[][] matrix;
    protected final int height;
    protected final int width;
    protected File file;

    public Image(int type, T[][] matrix, int height, int width, File file) {
        this.type = type;
        this.matrix = matrix;
        this.height = height;
        this.width = width;
        this.file = file;
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

    public File getFile() {
        return file;
    }
}
