package ar.edu.itba.ati.model.shapes;

public interface Shape {

    boolean belongs(int x, int y);

    boolean isLine();

    boolean isCircle();

    boolean isRectangle();

    boolean matches(double threshold);

}
