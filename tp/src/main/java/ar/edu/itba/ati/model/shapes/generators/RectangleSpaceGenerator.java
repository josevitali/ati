package ar.edu.itba.ati.model.shapes.generators;

import ar.edu.itba.ati.model.shapes.Rectangle;
import ar.edu.itba.ati.model.shapes.Shape;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class RectangleSpaceGenerator implements ShapeGenerator {
    @Override
    public Set<Shape> getParametricSet(int maxWidth, int maxHeight, double delta) {
        Set<Shape> parametricSpace = new HashSet<>();
        double ratio = 4.7;

//        IntStream.iterate(0, row -> row + 10).limit(maxHeight/10).parallel().
//                forEach(row ->
//                    IntStream.iterate(0, col -> col + 10).limit(maxWidth/10).
//                            forEach(col -> IntStream.iterate(20, height -> height + 20).limit((long)(Math.ceil(maxWidth / ratio)/20))
//                                    .forEach(height -> parametricSpace.add(new Rectangle(delta, ratio, row, col, height))))
//                );


        for (int row = 0; row < maxHeight; row += 10) {
            for (int col = 0; col < maxWidth; col += 10) {
                for (int height = 20; height < Math.ceil(maxWidth / ratio); height += 20) {
                    parametricSpace.add(new Rectangle(delta, ratio, row, col, height));
                }
            }
        }

        return parametricSpace;
    }
}
