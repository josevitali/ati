package ar.edu.itba.ati.model.shapes.generators;

import ar.edu.itba.ati.model.shapes.Rectangle;
import ar.edu.itba.ati.model.shapes.Shape;

import java.util.HashSet;
import java.util.Set;

public class RectangleSpaceGenerator implements ShapeGenerator {
    @Override
    public Set<Shape> getParametricSet(int maxX, int maxY, double delta) {
        Set<Shape> parametricSpace = new HashSet<>();
        double ratio = 3.5;

        for (int row = 0; row < maxX; row+=10) {
            for (int col = 0; col < maxY; col+=10) {
                for (int height = 50; height < maxX / 4; height+=10) {
                    parametricSpace.add(new Rectangle(delta, ratio, row, col, height));
                }
            }
        }

        return parametricSpace;
    }
}
