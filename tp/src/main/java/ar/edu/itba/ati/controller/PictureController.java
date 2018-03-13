package ar.edu.itba.ati.controller;

import ar.edu.itba.ati.events.pictures.CropEvent;
import ar.edu.itba.ati.events.pictures.ShowPictureEvent;
import ar.edu.itba.ati.services.PictureService;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

public class PictureController {

    private EventBus eventBus;
    private PictureService pictureService;

    public ImageView imageView;
    public ImageView auxImageView;
    public Group imageLayer;
    private RubberBandSelection rubberBandSelection;

    @Inject
    public PictureController(final EventBus eventBus, final PictureService pictureService){
        this.eventBus = eventBus;
        this.pictureService = pictureService;
    }


    @FXML
    @Subscribe
    protected void showPicture(ShowPictureEvent event){

        Image image = SwingFXUtils.toFXImage(pictureService.getPicture().toBufferedImage(), null);
        Image auxImage = SwingFXUtils.toFXImage(pictureService.getAuxPicture().toBufferedImage(), null);

        imageView.setImage(image);

        auxImageView.setImage(auxImage);

        Group imageLayer = new Group();

        rubberBandSelection = new RubberBandSelection(imageLayer);


//        BorderPane root = new BorderPane();

//        // container for image layers
//        ScrollPane scrollPane = new ScrollPane();

        // image layer: a group of images

//        // use scrollpane for image view in case the image is large
//        scrollPane.setContent(imageLayer);
//
//        // put scrollpane in scene
//        root.setCenter(scrollPane);

        // rubberband selection

        // add button for crop
//        eventBus.post(new AddCropFunctionalityEvent());

    }

    @FXML
    @Subscribe
    public void cropListener(CropEvent cropEvent){


        System.out.println("se llama al crop listener");

        // get bounds for image crop
        Bounds selectionBounds = rubberBandSelection.getBounds();

        // show bounds info
        System.out.println( "Selected area: " + selectionBounds);

        // crop the image
        crop(selectionBounds);

        // remove the blue selected area after cropping
        rubberBandSelection.removeBounds();
    }

    private void crop(Bounds bounds) {

        int width = (int) bounds.getWidth();
        int height = (int) bounds.getHeight();

        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        parameters.setViewport(new Rectangle2D( bounds.getMinX(), bounds.getMinY(), width, height));

        System.out.println("parametros " + parameters);

        WritableImage wi = new WritableImage( width, height);
        imageView.snapshot(parameters, wi);

        //TODO descomentar para que se vea la foto croppeada cuando ande esto
//        newImageView.setImage(wi);

    }



    /**
     * Drag rectangle with mouse cursor in order to get selection bounds
     */
    public static class RubberBandSelection {

        final DragContext dragContext = new DragContext();
        javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle();

        Group group;


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

                if( event.isSecondaryButtonDown())
                    return;

                double offsetX = event.getX() - dragContext.mouseAnchorX;
                double offsetY = event.getY() - dragContext.mouseAnchorY;

                if( offsetX > 0)
                    rect.setWidth( offsetX);
                else {
                    rect.setX(event.getX());
                    rect.setWidth(dragContext.mouseAnchorX - rect.getX());
                }

                if( offsetY > 0) {
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

            group.getChildren().remove( rect);
        }
    }


}
