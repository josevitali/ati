package ar.edu.itba.ati.model.shapes;

public class Rectangle implements Shape {

    private final double delta, ratio;
    private final int cornerRow, cornerCol, height;

    public Rectangle(double delta, double ratio, int cornerRow, int cornerCol, int height) {
        this.delta = delta;
        this.ratio = ratio;
        this.cornerRow = cornerRow;
        this.cornerCol = cornerCol;
        this.height = height;
    }

    @Override
    public boolean belongs(int row, int col) {
        return insideRectangle(row, col, cornerRow-delta-height, cornerCol-delta, cornerRow+delta, cornerCol+delta+height*ratio)
                && !insideRectangle(row, col, cornerRow+delta-height, cornerCol+delta, cornerRow-delta, cornerCol-delta+ height*ratio);
    }

    @Override
    public boolean isLine() {
        return false;
    }

    @Override
    public boolean isCircle() {
        return false;
    }

    @Override
    public boolean isRectangle() {
        return true;
    }

    private boolean insideRectangle(int row, int col, double minRow, double minCol, double maxRow, double maxCol) {
        return row >= minRow && row <= maxRow && col >= minCol && col <=maxCol;
    }
}
