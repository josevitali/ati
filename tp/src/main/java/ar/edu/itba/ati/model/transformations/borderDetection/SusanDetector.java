package ar.edu.itba.ati.model.transformations.borderDetection;

import ar.edu.itba.ati.model.pictures.ColorPicture;
import ar.edu.itba.ati.model.pictures.GreyPicture;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.transformations.PictureTransformer;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.Masks;
import ar.edu.itba.ati.model.transformations.slidingWindows.withMask.SlidingWindowWithMask;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

//public class SusanDetector extends SlidingWindowWithMask<Integer> {
public class SusanDetector implements PictureTransformer {

    private final SlidingWindowWithMask<Integer> slidingWindow;
    //TODO: remove
    public Picture transformedPicture = null;

    public SusanDetector(double t){
        this.slidingWindow = new SlidingWindowWithMask<>(Masks.CIRCULAR, new BiFunction<Integer[][], Double[][], Double>() {
            @Override
            public Double apply(Integer[][] circularMask, Double[][] doubles) {
                int n = 0;
                int size = circularMask.length * circularMask[0].length;

                for(int row = 0; row < circularMask.length; row++){
                    for(int col = 0; col < circularMask[0].length; col++){
                        if(circularMask[row][col] == 1){
                            n += Math.abs(doubles[row][col] - doubles[3][3]) < t ? 1.0 : 0.0;
                        }
                    }
                }
                return 1 - ((double)n / size);
            }
        });
    }

    @Override
    public <T,R> Picture transform(Picture<T> picture) {
        // TODO: solo para imagenes grises
        picture.normalize();
        GreyPicture greyPicture = (GreyPicture) picture;
        ColorPicture colorPicture = ((GreyPicture) picture).toColorPicture();
        List<int[]> borderPixels = new ArrayList();
        List<int[]> cornerPixels = new ArrayList();

        slidingWindow.transform(greyPicture);
        double px;

        for(int row = 3; row < greyPicture.getHeight() - 3; row++){
            for(int col = 3; col < greyPicture.getWidth() - 3; col++){
                px = greyPicture.getPixel(row,col);
                if(px >= 0.65 && px <= 0.8){
                    cornerPixels.add(new int[]{row,col});
                } else if(px >= 0.45 && px <= 0.60){
                    borderPixels.add(new int[]{row,col});
                }
            }
        }

        borderPixels.forEach(border -> colorPicture.putPixel(new Double[]{0.0,0.0,254.0},border[0],border[1]));
        cornerPixels.forEach(corner -> colorPicture.putPixel(new Double[]{0.0,254.0,0.0},corner[0],corner[1]));
        return colorPicture;
    }
}
