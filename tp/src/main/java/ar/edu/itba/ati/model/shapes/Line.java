package ar.edu.itba.ati.model.shapes;

public class Line implements Shape {

    private double delta, ro;
    private int teta;

    public Line(double delta, int teta, double ro) {
        this.delta = delta;
        this.teta = teta;
        this.ro = ro;
    }

    public double getRo() {
        return ro;
    }

    public int getTeta() {
        return teta;
    }

    @Override
    public boolean belongs(int x, int y) {
        return Math.abs(ro - x*Math.cos(Math.toRadians(teta)) - y*Math.sin(Math.toRadians(teta))) <= delta;
    }

    @Override
    public boolean isLine() {
        return true;
    }

    @Override
    public boolean isCircle() {
        return false;
    }

}
