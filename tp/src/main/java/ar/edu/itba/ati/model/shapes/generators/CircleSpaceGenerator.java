package ar.edu.itba.ati.model.shapes.generators;

import ar.edu.itba.ati.model.shapes.Circle;
import ar.edu.itba.ati.model.shapes.Shape;

import java.util.HashSet;
import java.util.Set;

public class CircleSpaceGenerator implements ShapeGenerator{

    @Override
    public Set<Shape> getParametricSet(int maxX, int maxY, double delta) {
        Set<Shape> parametricSpace = new HashSet<>();
        for (int radius = 20; radius <= Math.min(maxX, maxY)/2; radius += 10) {
            for (int x = radius; x <= maxX-radius; x+=1) {
                for (int y = radius; y <= maxY - radius; y+=1) {
                    parametricSpace.add(new Circle(delta, radius, x, y));
                }
//poner radio desde 20 hasta el ancho de la imagen/2, del maximo el 80% o el maximo a lo sumo
            }
        }
        return parametricSpace;
    }
}
