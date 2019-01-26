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
//        return insideRectangle(row, col, cornerRow-delta-height, cornerCol-delta, cornerRow+delta, cornerCol+delta+height*ratio)
//                && !insideRectangle(row, col, cornerRow+delta-height, cornerCol+delta, cornerRow-delta, cornerCol-delta+ height*ratio);
        return insideRectangle(row, col, cornerRow+delta+height, cornerCol-delta, cornerRow-delta, cornerCol+delta+height*ratio)
                && !insideRectangle(row, col, cornerRow-delta+height, cornerCol+delta, cornerRow+delta, cornerCol-delta+ height*ratio);
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

    private boolean insideRectangle(int row, int col, double maxRow, double minCol, double minRow, double maxCol) {
        return (double)row >= minRow && (double)row <= maxRow && col >= minCol && (double)col <=maxCol;
    }

    public double getSize() {
        return this.height * 2 + this.height * this.ratio * 2;
    }

    public int[] getCorners() {
        return new int[] {cornerRow, cornerCol, cornerRow + height - 1, (int)(cornerCol - 1 + height * ratio)};
    }
}
