package ar.edu.itba.ati.model.transformations.diffusion;

public class LorentzDiffusion extends AnisotropicDiffusion{
    public LorentzDiffusion(double iterations, double sigma) {
        super(x -> 1 / ((Math.pow(Math.abs(x),2)) / (sigma*sigma) + 1), iterations, sigma);
    }
}
