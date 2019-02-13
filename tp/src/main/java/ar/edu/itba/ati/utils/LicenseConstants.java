package ar.edu.itba.ati.utils;

import java.util.function.Function;

public enum LicenseConstants {

    ARGENTINA(4.7, 13.5, 7, (license -> {
        String letter = "[A-Z]";
        String number = "[0-9]";
        String lazyLetter = "[A-Z086]";
        String lazyNumber = "[0-9GOB]";
        String letterOrNumber = "[A-Z0-9]";
        int score = 0;
        if(license.length() != 7) {
            score-= (Math.abs(7-license.length()));
        }
        score += license.matches(letter+letterOrNumber+"*") ? 1 : 0;
        score += license.matches(lazyLetter+letterOrNumber+"*") ? 1 : 0;
        score += license.matches(letterOrNumber+letter+letterOrNumber+"*") ? 1 : 0;
        score += license.matches(letterOrNumber+lazyLetter+letterOrNumber+"*") ? 1 : 0;
        score += license.matches(letterOrNumber+letterOrNumber+number+letterOrNumber+"*") ? 1 : 0;
        score += license.matches(letterOrNumber+letterOrNumber+lazyNumber+letterOrNumber+"*") ? 1 : 0;
        score += license.matches(letterOrNumber+letterOrNumber+letterOrNumber+number+letterOrNumber+"*") ? 1 : 0;
        score += license.matches(letterOrNumber+letterOrNumber+letterOrNumber+lazyNumber+letterOrNumber+"*") ? 1 : 0;
        score += license.matches(letterOrNumber+"*"+number+letterOrNumber+letterOrNumber) ? 1 : 0;
        score += license.matches(letterOrNumber+"*"+lazyNumber+letterOrNumber+letterOrNumber) ? 1 : 0;
        score += license.matches(letterOrNumber+"*"+letter+letterOrNumber) ? 1 : 0;
        score += license.matches(letterOrNumber+"*"+lazyLetter+letterOrNumber) ? 1 : 0;
        score += license.matches(letterOrNumber+"*"+letter) ? 1 : 0;
        score += license.matches(letterOrNumber+"*"+lazyLetter) ? 1 : 0;

        return score;
    }));

    private final double ratio;
    private final double avgColor;
    private final int length;
    private final Function<String, Integer> scoreFunction;

    LicenseConstants(final double ratio, final double avgColor, int length, Function<String, Integer> scoreFunction){
        this.ratio = ratio;
        this.avgColor = avgColor;
        this.length = length;
        this.scoreFunction = scoreFunction;
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

    public Function<String, Integer> getScoreFunction() {
        return scoreFunction;
    }
}
