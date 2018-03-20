package ar.edu.itba.ati.controller;

import ar.edu.itba.ati.events.pictures.CropEvent;
import ar.edu.itba.ati.events.pictures.ShowPictureEvent;
import ar.edu.itba.ati.model.pictures.Picture;
import ar.edu.itba.ati.services.PictureService;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

public class PictureController {

    private EventBus eventBus;
    private PictureService pictureService;

    public ImageView imageView;
    public Group imageLayer;
    private RubberBandSelection rubberBandSelection;

    @Inject
    public PictureController(final EventBus eventBus, final PictureService pictureService){
        this.eventBus = eventBus;
        this.pictureService = pictureService;
    }

    @FXML
    @Subscribe
    protected void showPicture(ShowPictureEvent event) {
        Picture picture = event.getPicture();
        if(picture == null){
            picture = pictureService.getPicture();
        }

        Image image = SwingFXUtils.toFXImage(picture.getNormalizedClone().toBufferedImage(), null);

        imageView.setImage(image);

        if(rubberBandSelection != null){
            rubberBandSelection.removeBounds();
        }
        rubberBandSelection = new RubberBandSelection(imageLayer);
    }

    @FXML
    @Subscribe
    public void cropListener(CropEvent cropEvent){

        // get bounds for image crop
        Bounds selectionBounds = rubberBandSelection.getBounds();

        // crop the image
        crop(selectionBounds);

        // remove the blue selected area after cropping
        rubberBandSelection.removeBounds();
    }

    private void crop(Bounds bounds){

        if((int) bounds.getWidth() <= 0 || (int) bounds.getHeight() <= 0)
            return;

        pictureService.cropPicture((int)bounds.getMinY(), (int)bounds.getMaxY(), (int)bounds.getMinX(), (int)bounds.getMaxX());
        imageView.setImage(SwingFXUtils.toFXImage(pictureService.getPicture().toBufferedImage(), null));

    }

    /**
     * Drag rectangle with mouse cursor in order to get selection bounds
     */
    public static class RubberBandSelection {

        final DragContext dragContext = new DragContext();
        Rectangle rect = new Rectangle();

        Group group;


        /**
         * @return bounds, x refers to columns and y to rows
         */
        public Bounds getBounds() {
            return rect.getBoundsInParent();
        }

        public RubberBandSelection(Group group) {

            this.group = group;

            rect = new Rectangle( 0,0,0,0);
            rect.setStroke(Color.BLUE);
            rect.setStrokeWidth(1);
            rect.setStrokeLineCap(StrokeLineCap.ROUND);
            rect.setFill(Color.LIGHTBLUE.deriveColor(0, 1.2, 1, 0.6));

            group.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
            group.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
//            group.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler);

        }

        EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                if(event.isSecondaryButtonDown())
                    return;

                // remove old rect
                rect.setX(0);
                rect.setY(0);
                rect.setWidth(0);
                rect.setHeight(0);

                group.getChildren().remove(rect);

                // prepare new drag operation
                dragContext.mouseAnchorX = event.getX();
                dragContext.mouseAnchorY = event.getY();

                rect.setX(dragContext.mouseAnchorX);
                rect.setY(dragContext.mouseAnchorY);
                rect.setWidth(0);
                rect.setHeight(0);

                group.getChildren().add(rect);

            }
        };

        EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                if(event.isSecondaryButtonDown())
                    return;

                double offsetX = event.getX() - dragContext.mouseAnchorX;
                double offsetY = event.getY() - dragContext.mouseAnchorY;

                if(offsetX > 0)
                    rect.setWidth(offsetX);
                else {
                    rect.setX(event.getX());
                    rect.setWidth(dragContext.mouseAnchorX - rect.getX());
                }

                if(offsetY > 0) {
                    rect.setHeight( offsetY);
                } else {
                    rect.setY(event.getY());
                    rect.setHeight(dragContext.mouseAnchorY - rect.getY());
                }
            }
        };

        private static final class DragContext {

            public double mouseAnchorX;
            public double mouseAnchorY;

        }

        public void removeBounds(){
            rect.setX(0);
            rect.setY(0);
            rect.setWidth(0);
            rect.setHeight(0);

            group.getChildren().remove(rect);
        }
    }


}
