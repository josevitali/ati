package ar.edu.itba.ati.model.functions;

import java.util.function.Function;

public class Negative implements Function<Double,Double> {
    @Override
    public Double apply(Double aDouble) {
        return 255 - aDouble;
    }
}
