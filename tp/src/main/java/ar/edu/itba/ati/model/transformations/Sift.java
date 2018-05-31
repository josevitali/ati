package ar.edu.itba.ati.model.transformations;

import ar.edu.itba.ati.io.Pictures;
import ar.edu.itba.ati.model.pictures.ColorPicture;
import ar.edu.itba.ati.model.pictures.GreyPicture;
import ar.edu.itba.ati.model.pictures.Picture;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;

import java.awt.image.BufferedImage;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static ar.edu.itba.ati.io.Pictures.matToBufferedImage;

public class Sift implements PictureTransformer {

    private final Picture otherPicture;
    private final float nndrRatio;
    private final double matchesPercentage;

    public Sift(Picture otherPicture, float nndrRatio, double matchesPercentage) {
        this.otherPicture = otherPicture;
        this.nndrRatio = nndrRatio;
        this.matchesPercentage = matchesPercentage;
    }

    @Override
    public <T, R> Picture transform(Picture<T> picture) throws InvalidParameterException {
        Mat objectImage = Pictures.pictureToMat(picture);
        Mat sceneImage = Pictures.pictureToMat(otherPicture);

        MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
        FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SIFT);
        featureDetector.detect(objectImage, objectKeyPoints);
//        KeyPoint[] keypoints = objectKeyPoints.toArray();
//        System.out.println(keypoints);

        MatOfKeyPoint objectDescriptors = new MatOfKeyPoint();
        DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
        descriptorExtractor.compute(objectImage, objectKeyPoints, objectDescriptors);

        Mat outputImage = new Mat(objectImage.rows(), objectImage.cols(), Highgui.CV_LOAD_IMAGE_COLOR);
        Scalar newKeypointColor = new Scalar(255, 0, 0);

        Features2d.drawKeypoints(objectImage, objectKeyPoints, outputImage, newKeypointColor, 0);

        // Match object image with the scene image
        MatOfKeyPoint sceneKeyPoints = new MatOfKeyPoint();
        MatOfKeyPoint sceneDescriptors = new MatOfKeyPoint();
        featureDetector.detect(sceneImage, sceneKeyPoints);
        descriptorExtractor.compute(sceneImage, sceneKeyPoints, sceneDescriptors);

        Mat matchoutput = new Mat(Math.max(sceneImage.rows(), objectImage.rows()) * 2,
                Math.max(sceneImage.cols(), objectImage.cols()) * 2, Highgui.CV_LOAD_IMAGE_COLOR);
        Scalar matchesColor = new Scalar(0, 255, 0);

        List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();
        DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
        descriptorMatcher.knnMatch(objectDescriptors, sceneDescriptors, matches, 2);

        LinkedList<DMatch> goodMatchesList = new LinkedList<DMatch>();

//        float nndrRatio = 0.7f;

        for (int i = 0; i < matches.size(); i++) {
            MatOfDMatch matofDMatch = matches.get(i);
            DMatch[] dmatcharray = matofDMatch.toArray();
            DMatch m1 = dmatcharray[0];
            DMatch m2 = dmatcharray[1];

            if (m1.distance <= m2.distance * nndrRatio) {
                goodMatchesList.addLast(m1);

            }
        }

        int descriptorsMinSize = Math.min(objectDescriptors.toArray().length, sceneDescriptors.toArray().length);

        if (goodMatchesList.size() >= matchesPercentage * descriptorsMinSize) {
            System.out.println("Object Found");

            List<KeyPoint> objKeypointlist = objectKeyPoints.toList();
            List<KeyPoint> scnKeypointlist = sceneKeyPoints.toList();

            LinkedList<Point> objectPoints = new LinkedList<>();
            LinkedList<Point> scenePoints = new LinkedList<>();

            for (int i = 0; i < goodMatchesList.size(); i++) {
                objectPoints.addLast(objKeypointlist.get(goodMatchesList.get(i).queryIdx).pt);
                scenePoints.addLast(scnKeypointlist.get(goodMatchesList.get(i).trainIdx).pt);
            }

            MatOfPoint2f objMatOfPoint2f = new MatOfPoint2f();
            objMatOfPoint2f.fromList(objectPoints);
            MatOfPoint2f scnMatOfPoint2f = new MatOfPoint2f();
            scnMatOfPoint2f.fromList(scenePoints);

            Mat homography = Calib3d.findHomography(objMatOfPoint2f, scnMatOfPoint2f, Calib3d.RANSAC, 3);

            Mat obj_corners = new Mat(4, 1, CvType.CV_32FC2);
            Mat scene_corners = new Mat(4, 1, CvType.CV_32FC2);

            obj_corners.put(0, 0, new double[]{0, 0});
            obj_corners.put(1, 0, new double[]{objectImage.cols(), 0});
            obj_corners.put(2, 0, new double[]{objectImage.cols(), objectImage.rows()});
            obj_corners.put(3, 0, new double[]{0, objectImage.rows()});

            Core.perspectiveTransform(obj_corners, scene_corners, homography);

            Mat img = Pictures.pictureToMat(otherPicture);

            Core.line(img, new Point(scene_corners.get(0, 0)), new Point(scene_corners.get(1, 0)), new Scalar(0, 255, 0), 4);
            Core.line(img, new Point(scene_corners.get(1, 0)), new Point(scene_corners.get(2, 0)), new Scalar(0, 255, 0), 4);
            Core.line(img, new Point(scene_corners.get(2, 0)), new Point(scene_corners.get(3, 0)), new Scalar(0, 255, 0), 4);
            Core.line(img, new Point(scene_corners.get(3, 0)), new Point(scene_corners.get(0, 0)), new Scalar(0, 255, 0), 4);

            MatOfDMatch goodMatches = new MatOfDMatch();
            goodMatches.fromList(goodMatchesList);

            Features2d.drawMatches(objectImage, objectKeyPoints, sceneImage, sceneKeyPoints, goodMatches, matchoutput, matchesColor, newKeypointColor, new MatOfByte(), 2);

        } else {
            System.out.println("Object Not Found");
            return picture;
        }

        BufferedImage bufferedImage = matToBufferedImage(matchoutput);

        final int type = bufferedImage.getType();
        if(type == BufferedImage.TYPE_BYTE_GRAY){
            return new GreyPicture(bufferedImage);
        } else if(type == BufferedImage.TYPE_3BYTE_BGR){
            return new ColorPicture(bufferedImage);
        }
        throw new InvalidParameterException("Wrong image type :(");
    }

}
