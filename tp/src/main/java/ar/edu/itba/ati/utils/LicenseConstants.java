package ar.edu.itba.ati.utils;

public enum LicenseConstants {

    ARGENTINA(4.7, 13.5);

    private final double ratio;
    private final double avgColor;

    LicenseConstants(final double ratio, final double avgColor){
        this.ratio = ratio;
        this.avgColor = avgColor;
    }

    public double getRatio() {
        return ratio;
    }

    public double getAvgColor() {
        return avgColor;
    }
}
