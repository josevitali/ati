package ar.edu.itba.ati.model.transformations.borderDetection;

import ar.edu.itba.ati.model.pictures.ColorPicture;
import ar.edu.itba.ati.model.pictures.GreyPicture;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.PictureTransformer;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.Mask;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.Masks;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.SlidingWindowWithMask;
import ar.edu.itba.ati.model.transformations.smoothing.GaussianSmoothing;
import ar.edu.itba.ati.model.transformations.threshold.GlobalThreshold;
import ar.edu.itba.ati.model.transformations.threshold.OtsuThreshold;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.BiFunction;

public class CannyDetector implements PictureTransformer {

    private enum Direction {
        HORIZONTAL(1,0), TOP_LEFT(-1,1), VERTICAL(0,1), TOP_RIGHT(1,1);
        private int xStep,yStep;
        Direction(int xStep, int yStep){
            this.xStep = xStep;
            this.yStep = yStep;
        }
        public int getxStep() {
            return xStep;
        }
        public int getyStep() {
            return yStep;
        }
    }

    private final int[][] neighbours = {{1,-1},{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1},{0,-1}};
    private final double sigma;
    private final double[] angleThresholds = {22.5, 67.5, 112.5, 157.5, 180};
    private final Direction[] directions = {Direction.HORIZONTAL, Direction.TOP_RIGHT, Direction.VERTICAL,
                                        Direction.TOP_LEFT, Direction.HORIZONTAL};
    private final BiFunction<Double, Double, Double> moduleFunction = (x, y) -> Math.sqrt(x*x + y*y);


    public CannyDetector(double sigma) {
        this.sigma = sigma;
    }

    @Override
    public <T, R> Picture<R> transform(Picture<T> picture) {

        GreyPicture greyPicture;
        if(picture.getType() == BufferedImage.TYPE_3BYTE_BGR) {
            greyPicture = ((ColorPicture)picture).toGreyPicture();
        } else {
            greyPicture = (GreyPicture)picture;
        }

        PictureTransformer gaussianTransformer = new GaussianSmoothing(this.sigma);
        Picture filteredPicture = gaussianTransformer.transform(greyPicture);


        List<Mask> masks = Masks.rotate(Masks.SOBEL, new int[]{Masks.TOP, Masks.LEFT});


        GreyPicture gx = (GreyPicture) new SlidingWindowWithMask(masks.get(0)).transform(filteredPicture.getClone());
        GreyPicture gy = (GreyPicture) new SlidingWindowWithMask(masks.get(1)).transform(filteredPicture.getClone());

        //Get module of gx and gy
        GreyPicture modulePicture = (GreyPicture) gx.getClone();
        modulePicture.mapPixelByPixel(moduleFunction, gy);

        //Get border directions
        Direction[][] directions = new Direction[filteredPicture.getHeight()][filteredPicture.getWidth()];
        for (int i = 0; i < gx.getHeight(); i++) {
            for (int j = 0; j < gx.getWidth(); j++) {
                directions[i][j] = getDirection(gy.getPixel(i,j), gx.getPixel(i,j));
            }
        }

        GreyPicture suppressedPicture = suppressPixels(modulePicture, directions);

        //calculate thresholds
        suppressedPicture.normalize();
        double otsuThreshold = new OtsuThreshold().getThreshold(suppressedPicture);
        double t1 = otsuThreshold / 3;
        double t2 = otsuThreshold * 2 / 3;

        return (Picture<R>) hysteresisThresholding(suppressedPicture, t1, t2);
    }

    private Direction getDirection(Double x, Double y) {
        Double angle = (Math.toDegrees(Math.atan2(x, y)) + 180) % 180;
        for (int i = 0; i < angleThresholds.length; i++) {
            if(angle < angleThresholds[i]) {
                return directions[i];
            }
        }
        return directions[0];
    }

    private GreyPicture suppressPixels(GreyPicture picture, Direction[][] directions) {

        GreyPicture suppressedPicture = (GreyPicture)picture.getClone();

        Double[][] pictureMatrix = picture.getMatrix();
        Double[][] suppressedMatrix = suppressedPicture.getMatrix();
        int height = suppressedPicture.getHeight();
        int width = suppressedPicture.getWidth();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double current = pictureMatrix[i][j];
                if(current <= 0) {
                    suppressedMatrix[i][j] = 0.0;
                    continue;
                }
                int xStep = directions[i][j].getxStep();
                int yStep = directions[i][j].getyStep();
                if(i + xStep < 0 || i + xStep >= height || j + yStep < 0 || j + yStep >= width) {
                    suppressedMatrix[i][j] = 0.0;
                    continue;
                }
                if(current <= pictureMatrix[i+xStep][j+yStep]) {
                    suppressedMatrix[i][j] = 0.0;
                    continue;
                }
                else {
                    suppressedMatrix[i][j] = current;
                }
                if(i - xStep < 0 || i - xStep >= height || j - yStep < 0 || j - yStep >= width) {
                    suppressedMatrix[i][j] = 0.0;
                    continue;
                }
                if(current < pictureMatrix[i-xStep][j-yStep]) {
                    suppressedMatrix[i][j] = 0.0;
                }
                else {
                    suppressedMatrix[i][j] = current;
                }
            }
        }

        return suppressedPicture;
    }

    private GreyPicture hysteresisThresholding(GreyPicture picture, double t1, double t2) {
        Double[][] pictureMatrix = picture.getMatrix();
        int height = picture.getHeight();
        int width = picture.getWidth();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if(pictureMatrix[i][j] > t2) {
                    pictureMatrix[i][j] = 255.0;
                }
                else if(pictureMatrix[i][j] < t1) {
                    pictureMatrix[i][j] = 0.0;
                }
                else {
                    boolean foundNeighbour = false;
                    for (int k = 0; k < neighbours.length; k++) {
                        if (i + neighbours[k][0] > 0 && i + neighbours[k][0] < height && j + neighbours[k][1] > 0 && j + neighbours[k][1] < width) {
                            if (pictureMatrix[i + neighbours[k][0]][j + neighbours[k][1]] == 255.0) {
                                pictureMatrix[i][j] = 255.0;
                                foundNeighbour = true;
                            }
                        }
                    }
                    if(!foundNeighbour) {
                        pictureMatrix[i][j] = 0.0;
                    }
                }
            }
        }
        return picture;
    }
}
