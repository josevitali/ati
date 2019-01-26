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

    @Override
    public boolean isLine() {
        return false;
    }

    @Override
    public boolean isCircle() {
        return true;
    }

    @Override
    public boolean isRectangle() {
        return false;
    }

  @Override
  public int getPerimeter() {
    return (int)(2 * Math.PI * radius);
  }


  public double getDelta() {
        return delta;
    }

    public double getRadius() {
        return radius;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    @Override
    public String toString() {
        return "Circle{" +
                "radius=" + radius +
                ", centerX=" + centerX +
                ", centerY=" + centerY +
                '}';
    }
}
