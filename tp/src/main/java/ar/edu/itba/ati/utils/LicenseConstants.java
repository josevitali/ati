package ar.edu.itba.ati.utils;

public enum LicenseConstants {

    ARGENTINA(4.7, 13.5, 7);

    private final double ratio;
    private final double avgColor;
    private final int length;

    LicenseConstants(final double ratio, final double avgColor, int length){
        this.ratio = ratio;
        this.avgColor = avgColor;
        this.length = length;
    }

    public double getRatio() {
        return ratio;
    }

    public double getAvgColor() {
        return avgColor;
    }

    public int getLength() {
        return length;
    }
}
