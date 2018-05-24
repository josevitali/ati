package ar.edu.itba.ati.model.transformations.borderDetection;

import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.Mask;

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

        this.triFunction = (prevVal -> (actualVal -> (nextVal -> {
            if(actualVal == 0) {
                if(prevVal*nextVal<0 && Math.abs(prevVal) + Math.abs(nextVal) > threshold) {
                    return 255.0;
                }
                return 0.0;
            }
            if(prevVal*actualVal<0 && Math.abs(prevVal) + Math.abs(actualVal) > threshold) {
                return 255.0;
            }
            return 0.0;
        })));
    }


    //TODO pasar a masks
    private static Mask<Double> gaussianMask(double sigma) {
        int size = 8*(int)sigma + 1;
        Double[][] matrix = new Double[size][size];
        int halfSize = (size-1) / 2;
        for(int row = -halfSize; row <= halfSize; row++){
            for(int col = -halfSize; col <= halfSize; col++){
                double member1= -1/(Math.sqrt(2*Math.PI)* Math.pow(sigma, 3));
                double member2=2- (  (row*row+ col*col)/(sigma*sigma) );
                double member3=Math.exp(-(row*row+col*col)/(2*sigma*sigma));
                matrix[row+halfSize][col+halfSize] = member1*member2*member3;
            }
        }
        return new Mask(matrix);
    }


}
