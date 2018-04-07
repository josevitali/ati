package ar.edu.itba.ati.model.transformations.slidingWindows.withMask;

import java.util.function.BiFunction;

public class GaussianLaplaceTransformation extends LaplaceTransformation{

    public GaussianLaplaceTransformation(double sigma, double threshold) {
        super(gaussianMask(sigma));

        this.bf = new BiFunction<Double, Double, Double>() {
            @Override
            public Double apply(Double prevVal, Double actualVal) {
                if (prevVal * actualVal < 0 && Math.abs(prevVal) + Math.abs(actualVal) > threshold)
                    return 255.0;
                return 0.0;
            }
        };
    }


    //TODO pasar a masks y ver tema de tamaño
    private static Mask<Double> gaussianMask(double sigma) {
        int size = 2*(int)sigma + 1;
        size = 7;
        if(size%2 == 0)
            size+=1;
        System.out.println(size);
        Double[][] matrix = new Double[size][size];
        int halfSize = size / 2;
        double accum = 0;
        double value, x, y;
        for(int row = 0; row < size; row++){
            for(int col = 0; col < size; col++){
                x = row - halfSize;
                y = col  - halfSize;
                double aux = ((Math.pow(x,2)+Math.pow(y,2))/Math.pow(sigma,2));
                value = -(1/((Math.sqrt(2*Math.PI))*Math.pow(sigma,3)))*(2-aux)*Math.exp(-0.5*aux);
                matrix[row][col] = value;
                accum += value;
            }
        }
        for(int row = 0; row < size; row++){
            for(int col = 0; col < size; col++){
                matrix[row][col] /= accum;
            }
        }
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.println();
        }
        return new Mask(matrix);
    }


}
