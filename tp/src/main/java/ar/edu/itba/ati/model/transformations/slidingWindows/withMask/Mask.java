package ar.edu.itba.ati.model.transformations.slidingWindows.withMask;

public class Mask<T extends Number> {

    private final T[][] matrix;
    private final int size;
    private final int nucleusRow;
    private final int nucleusCol;

    public Mask(T[][] matrix){
        this.matrix = matrix;
        int rows = matrix.length;
        int cols = matrix[0].length;

        if(rows != cols){
            throw new IllegalArgumentException("Matrix should be square.");
        }

        this.size = rows;
        this.nucleusRow = rows / 2;
        this.nucleusCol = cols / 2;
    }

    public Mask(T[][] matrix, int nucleusRow, int nucleusCol){
        this.matrix = matrix;
        int rows = matrix.length;
        int cols = matrix[0].length;

        if(rows != cols){
            throw new IllegalArgumentException("Matrix should be square.");
        }

        this.size = rows;
        this.nucleusRow = nucleusRow;
        this.nucleusCol = nucleusCol;
    }

    public static Mask<Double> meanMask(int size){
        Double[][] matrix = new Double[size][size];

        for(int row = 0; row < size; row++){
            for(int col = 0; col < size; col++){
                matrix[row][col] = 1.0 / (size * size);
            }
        }

        return new Mask(matrix);
    }

    public static Mask<Integer> highPassMask(int size){
        Integer[][] matrix = new Integer[size][size];

        for(int row = 0; row < size; row++){
            for(int col = 0; col < size; col++){
                matrix[row][col] = -1;
            }
        }

        Mask mask = new Mask(matrix);
        mask.putElement(size * size - 1, mask.getNucleusRow(), mask.getNucleusCol());
        return mask;
    }

    public T[][] getMatrix() {
        return matrix;
    }

    public int getSize() {
        return size;
    }

    public int getNucleusRow() {
        return nucleusRow;
    }

    public int getNucleusCol() {
        return nucleusCol;
    }

    public int getLengthToUpBorder(){
        return nucleusRow;
    }

    public int getLengthToDownBorder(){
        return size - nucleusRow - 1;
    }

    public int getLengthToLeftBorder(){
        return nucleusCol;
    }

    public int getLengthToRightBorder(){
        return size - nucleusCol - 1;
    }

    public T getElement(int row, int col){
        return matrix[row][col];
    }

    public void putElement(T value, int row, int col){
        matrix[row][col] = value;
    }
}
