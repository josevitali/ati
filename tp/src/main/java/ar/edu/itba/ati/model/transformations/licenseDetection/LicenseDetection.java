package ar.edu.itba.ati.model.transformations.licenseDetection;

import ar.edu.itba.ati.model.pictures.ColorPicture;
import ar.edu.itba.ati.model.pictures.GreyPicture;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.shapes.generators.RectangleSpaceGenerator;
import ar.edu.itba.ati.model.transformations.PictureTransformer;
import ar.edu.itba.ati.model.transformations.borderDetection.HoughDetector;

import java.awt.image.BufferedImage;

public class LicenseDetection implements PictureTransformer {

  final HoughDetector houghDetector;

  public LicenseDetection(int houghThreshold, double houghDelta) {
    this.houghDetector = new HoughDetector(houghThreshold, houghDelta, new RectangleSpaceGenerator());
  }


  @Override
  public <T, R> Picture<R> transform(Picture<T> picture) {
    return houghDetector.transform(picture);
  }
}
