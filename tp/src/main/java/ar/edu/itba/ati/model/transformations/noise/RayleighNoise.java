package ar.edu.itba.ati.model.transformations.noise;


public class RayleighNoise extends Noise{

    private final double psi;

    public RayleighNoise(double density, double psi) {
        super(density);
        this.psi = psi;
    }

    @Override
    protected Double applyNoise(Double pixel) {
        return pixel * (psi * Math.sqrt(-2 * Math.log(1 - random.nextDouble())));
    }
}
