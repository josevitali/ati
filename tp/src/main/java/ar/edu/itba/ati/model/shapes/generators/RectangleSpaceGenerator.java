package ar.edu.itba.ati.model.shapes.generators;

import ar.edu.itba.ati.model.shapes.Rectangle;
import ar.edu.itba.ati.model.shapes.Shape;

import java.util.HashSet;
import java.util.Set;

public class RectangleSpaceGenerator implements ShapeGenerator {

    private final double ratio;

    public RectangleSpaceGenerator(double ratio) {
        this.ratio = ratio;
    }

    @Override
    public Set<Shape> getParametricSet(int maxWidth, int maxHeight, double delta) {
        Set<Shape> parametricSpace = new HashSet<>();

        for (int row = 0; row < maxHeight; row += 10) {
            for (int col = 0; col < maxWidth; col += 5) {
                for (int height = 20; height < Math.ceil(maxWidth / ratio); height += 20) {
                    parametricSpace.add(new Rectangle(delta, ratio, row, col, height));
                }
            }
        }

        return parametricSpace;
    }
}
