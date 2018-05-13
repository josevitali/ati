package ar.edu.itba.ati.model.shapes.generators;

import ar.edu.itba.ati.model.shapes.Circle;
import ar.edu.itba.ati.model.shapes.Shape;

import java.util.HashSet;
import java.util.Set;

public class CircleSpaceGenerator implements ShapeGenerator{

    @Override
    public Set<Shape> getParametricSet(int maxX, int maxY, double delta) {
        Set<Shape> parametricSpace = new HashSet<>();
        for (double radius = Math.sqrt(2); radius <= Math.sqrt(2) * Math.max(maxX, maxY)/2; radius += 10 * Math.sqrt(2)) {
            for (int x = 0; x <= maxX; x+=5) {
                for (int y = 0; y <= maxY; y+=5) {
                    parametricSpace.add(new Circle(delta, radius, x, y));
                }

            }
        }
        return parametricSpace;
    }
}
