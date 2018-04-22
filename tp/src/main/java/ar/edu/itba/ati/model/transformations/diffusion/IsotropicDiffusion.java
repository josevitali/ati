package ar.edu.itba.ati.model.transformations.diffusion;

public class IsotropicDiffusion extends Diffusion{

    public IsotropicDiffusion(double iterations) {
        super(x -> 1.0, iterations);
    }
}
