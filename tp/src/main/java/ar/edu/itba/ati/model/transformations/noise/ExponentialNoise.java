package ar.edu.itba.ati.model.transformations.noise;


public class ExponentialNoise extends Noise{

    private final double lambda;

    public ExponentialNoise(double density, double lambda) {
        super(density);
        this.lambda = lambda;
    }

    @Override
    protected Double applyNoise(Double pixel) {
        return pixel * ((-1 / lambda) * Math.log(random.nextDouble()));
    }
}
