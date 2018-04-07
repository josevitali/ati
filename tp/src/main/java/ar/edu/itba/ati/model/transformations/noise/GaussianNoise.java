package ar.edu.itba.ati.model.transformations.noise;


public class GaussianNoise extends Noise{

    private final double deviation;
    private final double mean;

    public GaussianNoise(double density, double mean, double deviation) {
        super(density);
        this.mean = mean;
        this.deviation = deviation;
    }

    @Override
    protected Double applyNoise(Double pixel) {
        return pixel + (random.nextGaussian() * deviation + mean);
    }
}
