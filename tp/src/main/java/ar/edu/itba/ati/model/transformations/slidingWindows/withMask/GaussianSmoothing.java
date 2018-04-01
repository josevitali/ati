package ar.edu.itba.ati.model.transformations.slidingWindows.withMask;

import java.util.function.BiFunction;

public class GaussianSmoothing extends SlidingWindowWithMask<Double> {
    public GaussianSmoothing(double sigma) {
        super(gaussianMask(calculateSize(sigma), sigma));
    }

    private static int calculateSize(double sigma) {
        int size;

        if(sigma >= 1 && sigma <= 10){
            size = (int) (2.0 * sigma + 1);
        } else {
            size = (int) (3.0 * sigma);
        }

        return size % 2 == 0 ? size - 1 : size;
    }

    private static Mask<Double> gaussianMask(int size, double sigma) {
        Double[][] matrix = new Double[size][size];
        int halfSize = size / 2;
        double accum = 0;
        double value, x, y;

        for(int row = 0; row < size; row++){
            for(int col = 0; col < size; col++){
                x = row - halfSize;
                y = col  - halfSize;
                value = (1 / (2 * Math.PI * sigma * sigma)) * Math.exp(- (x * x + y * y) / (sigma * sigma));
                matrix[row][col] = value;
                accum += value;
            }
        }

        for(int row = 0; row < size; row++){
            for(int col = 0; col < size; col++){
                matrix[row][col] /= accum;
            }
        }

        return new Mask(matrix);
    }
}
