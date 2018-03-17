package ar.edu.itba.ati.model.histograms;

import java.util.List;
import java.util.Map;

public class Histogram {

    private String[] categories;
    private Map<String,int[]> series;

    public Histogram(String[] categories, Map<String,int[]> series) {
        this.categories = categories;
        this.series = series;
    }

    public String[] getCategories() {
        return categories;
    }

    public Map<String,int[]> getSeries() {
        return series;
    }
}
