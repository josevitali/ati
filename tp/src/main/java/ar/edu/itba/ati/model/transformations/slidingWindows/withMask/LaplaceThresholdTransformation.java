package ar.edu.itba.ati.model.transformations.slidingWindows.withMask;


public class LaplaceThresholdTransformation extends LaplaceTransformation{
    public LaplaceThresholdTransformation(Mask mask, double threshold) {
        super(mask);

        this.bf = (prevVal, actualVal) -> {
            if (prevVal * actualVal < 0 && Math.abs(prevVal) + Math.abs(actualVal) > threshold)
                return 255.0;
            return 0.0;
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
}
