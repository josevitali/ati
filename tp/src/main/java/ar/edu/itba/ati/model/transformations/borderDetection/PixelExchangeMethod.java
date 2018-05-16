package ar.edu.itba.ati.model.transformations.borderDetection;

import ar.edu.itba.ati.model.pictures.ColorPicture;
import ar.edu.itba.ati.model.pictures.GreyPicture;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.PictureTransformer;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.Mask;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.Masks;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class PixelExchangeMethod implements PictureTransformer {

    private final int firstRow;
    private final int lastRow;
    private final int firstCol;
    private final int lastCol;
    private final int iterations;
    private final double restriction;
    private static final int OUT = 3;
    private static final int LOUT = 1;
    private static final int LIN = -1;
    private static final int IN = -3;

    public PixelExchangeMethod(int firstRow, int lastRow, int firstCol, int lastCol, int iterations, double restriction) {
        this.firstRow = firstRow;
        this.lastRow = lastRow;
        this.firstCol = firstCol;
        this.lastCol = lastCol;
        this.iterations = iterations;
        this.restriction = restriction;
    }

    @Override
    public <T, R> Picture transform(Picture<T> picture) {
        picture.normalize();
        final Set<Point> lin = new HashSet();
        final Set<Point> lout = new HashSet();
        final int[][] phi = new int[picture.getHeight()][picture.getWidth()];
        final ColorPicture colorPicture = picture.getType() == BufferedImage.TYPE_BYTE_GRAY ?
                ((GreyPicture) picture).toColorPicture() : (ColorPicture) picture;
        int i = 0;
        boolean insideLoutIf = true;
        boolean insideLinIf = true;

        // #1
        // Define initial lin, lout & phi matrix
        Double[] tetha = initialParameters(lin, lout, phi, colorPicture);

        while(i < iterations && (insideLinIf || insideLoutIf)){
            i++;
            insideLoutIf = false;
            insideLinIf = false;

            Set<Point> toRemoveLin = new HashSet();
            Set<Point> toAddLin = new HashSet();
            Set<Point> toRemoveLout = new HashSet();
            Set<Point> toAddLout = new HashSet();

            // #2
            for(Point point : lout){

                if(fd(colorPicture.getPixel(point.x,point.y), tetha) > 0){
                    insideLoutIf = true;
                    toRemoveLout.add(point);
                    toAddLin.add(point);

                    // up
                    if(point.x > 0 && phi[point.x - 1][point.y] == OUT){
                        toAddLout.add(new Point(point.x - 1,point.y));
                    }

                    // down
                    if(point.x < phi.length - 1 && phi[point.x + 1][point.y] == OUT){
                        toAddLout.add(new Point(point.x + 1,point.y));
                    }

                    // left
                    if(point.y > 0 && phi[point.x][point.y - 1] == OUT){
                        toAddLout.add(new Point(point.x,point.y - 1));
                    }

                    // right
                    if(point.y < phi[0].length - 1 && phi[point.x][point.y + 1] == OUT){
                        toAddLout.add(new Point(point.x,point.y + 1));
                    }
                }
            }

            for(Point point : toRemoveLout){
                lout.remove(point);
                phi[point.x][point.y] = LIN;
            }
            for(Point point : toAddLout){
                lout.add(point);
                phi[point.x][point.y] = LOUT;
            }
            for(Point point : toAddLin){
                lin.add(point);
                phi[point.x][point.y] = LIN;
            }

            // #3
            for(Point point : lin){
                int row = point.x;
                int col = point.y;

                if(row > 0 && row < phi.length - 1 && col > 0 && col < phi[0].length - 1 &&
                        phi[row - 1][col] != LOUT && phi[row + 1][col] != LOUT && phi[row][col - 1] != LOUT &&
                        phi[row][col + 1] != LOUT){
                    phi[row][col] = IN;
                    toRemoveLin.add(point);
                }
            }

            lin.removeAll(toRemoveLin);

            toAddLin.clear();
            toAddLout.clear();
            toRemoveLin.clear();
            toRemoveLout.clear();


            //#4
            for(Point point : lin){

                if(fd(colorPicture.getPixel(point.x,point.y), tetha) < 0){
                    insideLinIf = true;
                    toRemoveLin.add(point);
                    toAddLout.add(point);

                    // up
                    if(point.x > 0 && phi[point.x - 1][point.y] == IN){
                        toAddLin.add(new Point(point.x - 1,point.y));
                    }

                    // down
                    if(point.x < phi.length - 1 && phi[point.x + 1][point.y] == IN){
                        toAddLin.add(new Point(point.x + 1,point.y));
                    }

                    // left
                    if(point.y > 0 && phi[point.x][point.y - 1] == IN){
                        toAddLin.add(new Point(point.x,point.y - 1));
                    }

                    // right
                    if(point.y < phi[0].length - 1 && phi[point.x][point.y + 1] == IN){
                        toAddLin.add(new Point(point.x,point.y + 1));
                    }
                }
            }

            for(Point point : toRemoveLin){
                lin.remove(point);
                phi[point.x][point.y] = LOUT;
            }
            for(Point point : toAddLout){
                lout.add(point);
                phi[point.x][point.y] = LOUT;
            }
            for(Point point : toAddLin){
                lin.add(point);
                phi[point.x][point.y] = LIN;
            }

            // #5
            for(Point point : lout){
                int row = point.x;
                int col = point.y;

                if(row > 0 && row < phi.length - 1 && col > 0 && col < phi[0].length - 1 &&
                        phi[row - 1][col] != LIN && phi[row + 1][col] != LIN && phi[row][col - 1] != LIN &&
                        phi[row][col + 1] != LIN){
                    phi[row][col] = OUT;
                    toRemoveLout.add(point);
                }
            }

            lout.removeAll(toRemoveLout);

            toAddLin.clear();
            toAddLout.clear();
            toRemoveLin.clear();
            toRemoveLout.clear();
        }




        // Second cycle
        Mask gaussian = Masks.gaussianMask(5,1);
        i = 0;

        while(i < 5){
            i++;

            Set<Point> toRemoveLin = new HashSet();
            Set<Point> toAddLin = new HashSet();
            Set<Point> toRemoveLout = new HashSet();
            Set<Point> toAddLout = new HashSet();

            // #2
            for(Point point : lout){

                if(point.x > 2 && point.y >= 2 && point.x <= colorPicture.getHeight() - 3 && point.y <= colorPicture.getWidth() - 3 &&
                gPhiConvolution(point.x, point.y, phi, gaussian) < 0){
                    toRemoveLout.add(point);
                    toAddLin.add(point);

                    // up
                    if(point.x > 0 && phi[point.x - 1][point.y] == OUT){
                        toAddLout.add(new Point(point.x - 1,point.y));
                    }

                    // down
                    if(point.x < phi.length - 1 && phi[point.x + 1][point.y] == OUT){
                        toAddLout.add(new Point(point.x + 1,point.y));
                    }

                    // left
                    if(point.y > 0 && phi[point.x][point.y - 1] == OUT){
                        toAddLout.add(new Point(point.x,point.y - 1));
                    }

                    // right
                    if(point.y < phi[0].length - 1 && phi[point.x][point.y + 1] == OUT){
                        toAddLout.add(new Point(point.x,point.y + 1));
                    }
                }
            }

            for(Point point : toRemoveLout){
                lout.remove(point);
                phi[point.x][point.y] = LIN;
            }
            for(Point point : toAddLout){
                lout.add(point);
                phi[point.x][point.y] = LOUT;
            }
            for(Point point : toAddLin){
                lin.add(point);
                phi[point.x][point.y] = LIN;
            }

            // #3
            for(Point point : lin){
                int row = point.x;
                int col = point.y;

                if(row > 0 && row < phi.length - 1 && col > 0 && col < phi[0].length - 1 &&
                        phi[row - 1][col] != LOUT && phi[row + 1][col] != LOUT && phi[row][col - 1] != LOUT &&
                        phi[row][col + 1] != LOUT){
                    phi[row][col] = IN;
                    toRemoveLin.add(point);
                }
            }

            lin.removeAll(toRemoveLin);

            toAddLin.clear();
            toAddLout.clear();
            toRemoveLin.clear();
            toRemoveLout.clear();


            //#4
            for(Point point : lin){

                if(point.x > 2 && point.y >= 2 && point.x <= colorPicture.getHeight() - 3 && point.y <= colorPicture.getWidth() - 3 &&
                        gPhiConvolution(point.x, point.y, phi, gaussian) > 0){
                    toRemoveLin.add(point);
                    toAddLout.add(point);

                    // up
                    if(point.x > 0 && phi[point.x - 1][point.y] == IN){
                        toAddLin.add(new Point(point.x - 1,point.y));
                    }

                    // down
                    if(point.x < phi.length - 1 && phi[point.x + 1][point.y] == IN){
                        toAddLin.add(new Point(point.x + 1,point.y));
                    }

                    // left
                    if(point.y > 0 && phi[point.x][point.y - 1] == IN){
                        toAddLin.add(new Point(point.x,point.y - 1));
                    }

                    // right
                    if(point.y < phi[0].length - 1 && phi[point.x][point.y + 1] == IN){
                        toAddLin.add(new Point(point.x,point.y + 1));
                    }
                }
            }

            for(Point point : toRemoveLin){
                lin.remove(point);
                phi[point.x][point.y] = LOUT;
            }
            for(Point point : toAddLout){
                lout.add(point);
                phi[point.x][point.y] = LOUT;
            }
            for(Point point : toAddLin){
                lin.add(point);
                phi[point.x][point.y] = LIN;
            }

            // #5
            for(Point point : lout){
                int row = point.x;
                int col = point.y;

                if(row > 0 && row < phi.length - 1 && col > 0 && col < phi[0].length - 1 &&
                        phi[row - 1][col] != LIN && phi[row + 1][col] != LIN && phi[row][col - 1] != LIN &&
                        phi[row][col + 1] != LIN){
                    phi[row][col] = OUT;
                    toRemoveLout.add(point);
                }
            }

            lout.removeAll(toRemoveLout);

            toAddLin.clear();
            toAddLout.clear();
            toRemoveLin.clear();
            toRemoveLout.clear();
        }

        for(Point out : lout){
            colorPicture.putPixel(new Double[]{255.0,255.0,0.0}, out.x, out.y);
        }
        for(Point in : lin){
            colorPicture.putPixel(new Double[]{0.0,0.0,255.0}, in.x, in.y);
        }
        return colorPicture;
    }

    private Double[] initialParameters(Set<Point> lin, Set<Point> lout, int[][] phi, ColorPicture colorPicture){
        final int height = colorPicture.getHeight();
        final int width = colorPicture.getWidth();
        Double[] tetha = new Double[]{new Double(0.0),new Double(0.0),new Double(0.0)};

        for(int row = 0; row < height; row++){
            for(int col = 0; col < width; col++){
                if(row > firstRow && row < lastRow && col > firstCol && col < lastCol){
                    // Inside
                    phi[row][col] = IN;
                    addVectors(tetha, colorPicture.getPixel(row,col));
                } else if (((row == firstRow || row == lastRow) && (col >= firstCol && col <= lastCol)) ||
                        ((col == firstCol || col == lastCol) && (row >= firstRow && row <= lastRow))){
                    // lin
                    phi[row][col] = LIN;
                    lin.add(new Point(row,col));
                    addVectors(tetha, colorPicture.getPixel(row,col));
                } else if (((row == firstRow - 1 || row == lastRow + 1) && (col >= firstCol - 1 && col <= lastCol + 1)) ||
                        ((col == firstCol - 1 || col == lastCol + 1) && (row >= firstRow - 1 && row <= lastRow + 1))){
                    // lout
                    phi[row][col] = LOUT;
                    lout.add(new Point(row,col));
                } else {
                    // Outside
                    phi[row][col] = OUT;
                }
            }
        }

        for(int i = 0; i < 3; i++){
            tetha[i] /= ((1 + lastRow - firstRow) * (1 + lastCol - firstCol));
        }

        return tetha;
    }

    private double fd(Double[] pixel, Double[] tetha){
        double[] diff = new double[]{pixel[0] - tetha[0], pixel[1] - tetha[1], pixel[2] - tetha[2]};
        double nom = Math.sqrt(diff[0] * diff[0] + diff[1] * diff[1] + diff[2] * diff[2]);

        double den = 256 * Math.sqrt(3);
        double result = (nom/den);

        return result < restriction ? 1 : -1;
    }

    private double gPhiConvolution(int phiRow, int phiCol, int[][] phi, Mask gaussian){
        double ret = 0;
        Number[][] matrix = gaussian.getMatrix();

        for(int row = 0; row < matrix.length; row++){
            for(int col = 0; col < matrix[0].length; col++){
                ret += phi[row + phiRow - 2][col + phiCol - 2] * (Double) matrix[row][col];
            }
        }

        return ret;
    }

    private void addVectors(Double[] vec1, Double[] vec2){
        int length = vec1.length;

        if(vec2.length != length){
            throw new IllegalArgumentException("Vectors should have same length.");
        }

        for(int i = 0; i < length; i++){
            vec1[i] += vec2[i];
        }
    }
}
