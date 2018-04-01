package ar.edu.itba.ati.model.histograms;

import java.util.Map;

public class Histogram {

    private String[] categories;
    private Map<String,double[]> series;
    private double[] average;
    private double[] variance;

    public Histogram(String[] categories, Map<String,double[]> series, double[] average, double[] variance) {
        this.categories = categories;
        this.series = series;
        this.average = average;
        this.variance = variance;
    }

    public String[] getCategories() {
        return categories;
    }

    public Map<String,double[]> getSeries() {
        return series;
    }

    public double[] getAverage() {
        return average;
    }

    public double[] getVariance() {
        return variance;
    }
}
