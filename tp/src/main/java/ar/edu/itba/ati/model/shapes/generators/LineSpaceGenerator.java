package ar.edu.itba.ati.model.shapes.generators;

import ar.edu.itba.ati.model.shapes.Line;
import ar.edu.itba.ati.model.shapes.Shape;

import java.util.HashSet;
import java.util.Set;

public class LineSpaceGenerator implements ShapeGenerator{

    public Set<Shape> getParametricSet(int maxX, int maxY, double delta) {
        Set<Shape> parametricSpace = new HashSet<>();
        int d = Math.max(maxX, maxY);
        for (int teta = -90; teta <= 90; teta+=5) {
            for (double ro = -Math.sqrt(2.0)*d; ro <= Math.sqrt(2.0)*d; ro+= 2*Math.sqrt(2.0)) {
                parametricSpace.add(new Line(delta, teta, ro));
            }
        }
        return parametricSpace;
    }

}
