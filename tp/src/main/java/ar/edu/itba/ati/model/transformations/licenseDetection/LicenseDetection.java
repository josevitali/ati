package ar.edu.itba.ati.model.transformations.licenseDetection;

import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.model.shapes.generators.RectangleSpaceGenerator;
import ar.edu.itba.ati.model.transformations.PictureTransformer;
import ar.edu.itba.ati.model.transformations.borderDetection.HoughDetector;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class LicenseDetection implements PictureTransformer {

  final HoughDetector houghDetector;

  public LicenseDetection(int houghThreshold, double houghDelta) {
    this.houghDetector = new HoughDetector(houghThreshold, houghDelta, new RectangleSpaceGenerator());
  }


  @Override
  public <T, R> Picture<R> transform(Picture<T> picture) {
    return houghDetector.transform(picture);
  }

  /**
   * Using OCR library read text in license plate
   * @param imageLocation
   * @return
   */
  public static String imageToString(String imageLocation){
    ITesseract instance = new Tesseract();
    try {
      return instance.doOCR(new File(imageLocation));
    }
    catch (TesseractException e) {
      e.getMessage();
      return "Error while reading image";
    }
  }

  public static void main(String[] args) {
    System.out.println(imageToString("/Users/natinavas/Documents/ITBA/ATI/ati/tp/src/main/java/ar/edu/itba/ati/model/transformations/licenseDetection/licensePlate.png"));
  }



}
