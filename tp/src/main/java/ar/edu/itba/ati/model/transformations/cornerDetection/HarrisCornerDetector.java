package ar.edu.itba.ati.model.transformations.cornerDetection;

import ar.edu.itba.ati.model.pictures.ColorPicture;
import ar.edu.itba.ati.model.pictures.GreyPicture;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.PictureTransformer;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.Mask;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.Masks;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.SlidingWindowWithMask;
import ar.edu.itba.ati.model.transformations.smoothing.GaussianSmoothing;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class HarrisCornerDetector implements PictureTransformer {

    private final Function<Double,Double> square = (p -> p * p);
    private final BiFunction<Double,Double,Double> mult = (p1,p2) -> p1 * p2;
    private final double k = 0.04;

    private final double threshold;

    public HarrisCornerDetector(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public <T, R> Picture transform(Picture<T> picture) {
        final GreyPicture greyPicture = picture.getType() == BufferedImage.TYPE_BYTE_GRAY ?
                (GreyPicture) picture : ((ColorPicture) picture).toGreyPicture();

        final List<Mask> masks = Masks.rotate(Masks.SOBEL, new int[]{Masks.TOP, Masks.LEFT});
        Picture ix = new SlidingWindowWithMask(masks.get(0)).transform(greyPicture.getClone());
        Picture iy = new SlidingWindowWithMask(masks.get(1)).transform(greyPicture.getClone());

        Picture ix2 = ix.getClone();
        Picture iy2 = iy.getClone();
        ix2.mapPixelByPixel(square);
        iy2.mapPixelByPixel(square);

        final PictureTransformer gaussianTransformer = new GaussianSmoothing(2, 7);
        ix2 = gaussianTransformer.transform(ix2);
        iy2 = gaussianTransformer.transform(iy2);

        Picture ixy = ix.getClone();
        ixy.mapPixelByPixel(mult, iy);
        ixy = gaussianTransformer.transform(ixy);

        Double[][] rMatrix = new Double[picture.getHeight()][picture.getWidth()];

        GreyPicture cim = (GreyPicture) ix.getClone();

        for(int row = 0; row < picture.getHeight(); row++){
            for(int col = 0; col < picture.getWidth(); col++){
                Double ix2Px = (Double) ix2.getPixel(row,col);
                Double iy2Px = (Double) iy2.getPixel(row,col);
                Double ixyPx = (Double) ixy.getPixel(row,col);

                cim.mapPixel(row, col, aDouble -> (ix2Px * iy2Px - ixyPx * ixyPx) - k * (ix2Px + iy2Px) * (ix2Px + iy2Px));
            }
        }

        double max = cim.getMaxPixel();

        ColorPicture colorPicture = picture.getType() == BufferedImage.TYPE_3BYTE_BGR ?
                (ColorPicture) picture : ((GreyPicture) picture).toColorPicture();

        for(int row = 0; row < picture.getHeight(); row++){
            for(int col = 0; col < picture.getWidth(); col++){
                if(cim.getPixel(row,col) > max * threshold){
                    colorPicture.putPixel(new Double[]{0.0,255.0,255.0}, row, col);
                }
            }
        }

        return colorPicture;
    }
}
