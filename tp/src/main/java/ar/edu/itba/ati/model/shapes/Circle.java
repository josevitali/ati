package ar.edu.itba.ati.model.shapes;

public class Circle implements Shape{

    private double delta, radius, centerX, centerY;

    public Circle(double delta, double radius, double centerX, double centerY) {
        this.delta = delta;
        this.radius = radius;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    @Override
    public boolean belongs(int x, int y) {
        return Math.abs(Math.pow(radius,2) - Math.pow(x - centerX,2) - Math.pow(y - centerY,2)) <= delta;
    }

//    @Override
//    public ColorPicture drawShape(ColorPicture picture) {
//        double i, angle, x1, y1;
//
//        Double[] colorPixel = new Double[3];
//        colorPixel[0]= 100.0;
//        colorPixel[1] = 100.0;
//        colorPixel[2] = 100.0;
//
//        for(i = 0; i < 360; i += 0.1) {
//            angle = i;
//            x1 = radius * Math.cos(angle * Math.PI / 180);
//            y1 = radius * Math.sin(angle * Math.PI / 180);
//            if((int)(centerX + x1) < picture.getWidth() && (int)(centerY + y1) < picture.getHeight() && (int)(centerX + x1) >= 0 && (int)(centerY + y1) >= 0)
//                picture.putPixel(colorPixel, (int)(centerY + y1), (int)(centerX + x1));
//        }
//
//        return picture;
//
//    }

}
