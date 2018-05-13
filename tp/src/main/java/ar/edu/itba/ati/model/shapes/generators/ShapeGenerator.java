package ar.edu.itba.ati.model.shapes.generators;

import ar.edu.itba.ati.model.shapes.Shape;

import java.util.Set;

public interface ShapeGenerator {

    Set<Shape> getParametricSet(int maxX, int maxY, double delta);
}
