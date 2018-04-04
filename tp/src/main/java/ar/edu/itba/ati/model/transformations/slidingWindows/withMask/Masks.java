package ar.edu.itba.ati.model.transformations.slidingWindows.withMask;

import ar.edu.itba.ati.controller.tabs.SideTab2Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Masks {

    public static int TOP = 0;
    public static int TOP_RIGHT = 1;
    public static int RIGHT = 2;
    public static int DOWN_RIGHT = 3;
    public static int DOWN = 4;
    public static int DOWN_LEFT = 5;
    public static int LEFT = 6;
    public static int TOP_LEFT = 7;
    public static int[] ALL_DIRECTIONS = new int[]{TOP,TOP_RIGHT,RIGHT,DOWN_RIGHT,DOWN,DOWN_LEFT,LEFT,TOP_LEFT};

    // Masks
    public static Mask PREWITT = new Mask(new Double[][]{{-1.0, -1.0, -1.0},{0.0, 0.0, 0.0},{1.0, 1.0, 1.0}});
    public static Mask SOBEL = new Mask(new Double[][]{{-1.0, -2.0, -1.0},{0.0, 0.0, 0.0},{1.0, 2.0, 1.0}});
    public static Mask KIRSH = new Mask(new Double[][]{{5.0, 5.0, 5.0},{-3.0, 0.0, -3.0},{-3.0, -3.0, -3.0}});

    public static List<Mask> rotate(Mask mask, int[] rotations){
        Number[][] matrix = mask.getMatrix();

        if(matrix.length != 3 || matrix[0].length != 3){
            throw new IllegalArgumentException("Matrix should be 3x3.");
        }

        rotations = Arrays.stream(rotations).sorted().toArray();
        List<Mask> ret = new ArrayList();
        int rotAmount = 0;

        for(int rotation : rotations){
            while (rotAmount < rotation){
                matrix = rotate(matrix);
                rotAmount++;
            }
            ret.add(new Mask(matrix));
        }

        return ret;
    }

    /**
     * Rotates matrix clockwise 45˚
     * @param matrix
     * @return
     */
    private static Number[][] rotate(Number[][] matrix){
        Number[][] ret = new Number[3][3];
        ret[0][0] = matrix[1][0];
        ret[1][1] = matrix[1][1];
        ret[2][2] = matrix[1][2];
        ret[0][1] = matrix[0][0];
        ret[0][2] = matrix[0][1];
        ret[1][2] = matrix[0][2];
        ret[1][0] = matrix[2][0];
        ret[2][0] = matrix[2][1];
        ret[2][1] = matrix[2][2];
        return ret;
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
}
