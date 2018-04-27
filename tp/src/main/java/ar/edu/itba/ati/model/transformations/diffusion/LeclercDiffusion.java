package ar.edu.itba.ati.model.transformations.diffusion;

public class LeclercDiffusion extends AnisotropicDiffusion{
    public LeclercDiffusion(double iterations, double sigma) {
        super(x -> Math.exp(-Math.pow(Math.abs(x), 2) / (sigma*sigma)), iterations, sigma);
    }
}
