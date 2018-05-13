package ar.edu.itba.ati.model.shapes;

public class Circle implements Shape{

    private double delta, radius, centerX, centerY;

    public Circle(double delta, double radius, double centerX, double centerY) {
        this.delta = delta;
        this.radius = radius;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    @Override
    public boolean belongs(int x, int y) {
        return Math.abs(Math.pow(radius,2) - Math.pow(x - centerX,2) - Math.pow(y - centerY,2)) <= delta;
    }
}
