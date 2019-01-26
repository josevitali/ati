package ar.edu.itba.ati.model.shapes;

public class Rectangle implements Shape {

    private final double delta, ratio;
    private final int cornerRow, cornerCol, height;
    private int matchesLeft = 0;
    private int matchesUp = 0;
    private int matchesRight = 0;
    private int matchesDown = 0;

    public Rectangle(double delta, double ratio, int cornerRow, int cornerCol, int height) {
        this.delta = delta;
        this.ratio = ratio;
        this.cornerRow = cornerRow;
        this.cornerCol = cornerCol;
        this.height = height;
    }

    @Override
    public boolean belongs(int row, int col) {
        if (insideRectangle(row, col, cornerRow - delta, cornerCol, cornerRow + delta, cornerCol + ratio * height)) {
            matchesUp++;
            return true;
        } else if (insideRectangle(row, col, cornerRow + height - delta, cornerCol, cornerRow + height + delta, cornerCol + ratio * height)) {
            matchesDown++;
            return true;
        } else if (insideRectangle(row, col, cornerRow, cornerCol - delta, cornerRow + height, cornerCol + delta)) {
            matchesLeft++;
            return true;
        } else if (insideRectangle(row, col, cornerRow, cornerCol + ratio * height - delta, cornerRow + height, cornerCol + ratio * height + delta)) {
            matchesRight++;
            return true;
        }
        return false;

//        return insideRectangle(row, col, cornerRow-delta-height, cornerCol-delta, cornerRow+delta, cornerCol+delta+height*ratio)
//                && !insideRectangle(row, col, cornerRow+delta-height, cornerCol+delta, cornerRow-delta, cornerCol-delta+ height*ratio);
//        return insideRectangle(row, col, cornerRow+delta+height, cornerCol-delta, cornerRow-delta, cornerCol+delta+height*ratio)
//                && !insideRectangle(row, col, cornerRow-delta+height, cornerCol+delta, cornerRow+delta, cornerCol-delta+ height*ratio);
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
        return (double) row >= minRow && (double) row <= maxRow && col >= minCol && (double) col <= maxCol;
    }

    public int[] getCorners() {
        return new int[]{cornerRow, cornerCol, cornerRow + height - 1, (int) (cornerCol - 1 + height * ratio)};
    }

    @Override
    public boolean matches(double threshold) {
        final double width = ratio * height;
        return (matchesLeft / height >= threshold) && (matchesRight / height >= threshold)
                && (matchesUp / width >= threshold) && (matchesDown / width >= threshold);
    }
}
