package ar.edu.itba.ati.model.transformations.diffusion;

import java.util.function.Function;

public abstract class AnisotropicDiffusion extends Diffusion{
    protected final double sigma;

    protected AnisotropicDiffusion(Function<Double,Double> f, double iterations, double sigma) {
        super(f, iterations);
        this.sigma = sigma;
    }
}
