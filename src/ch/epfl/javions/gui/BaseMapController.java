package ch.epfl.javions.gui;

import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.IOException;

public final class BaseMapController {


    public static final int SIZE = 256;
    private final TileManager tileManager ;
    private final MapParameters mapParameters;
    private Pane pane;
    private Canvas canvas ;
    private GraphicsContext graphicsContext;
    private boolean redrawNeeded = true ;


    private double lastMouseX, lastMouseY;
    private boolean isDragging = false;


    public BaseMapController(TileManager tileManager , MapParameters parameters ){
        canvas = new Canvas();
        pane = new Pane(canvas);

        this.mapParameters =parameters;
        this.tileManager=tileManager;

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
        canvas.sceneProperty().addListener((p, oldS, newS) -> {

            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        canvas.widthProperty().addListener((observable, oldValue, newValue) -> redrawOnNextPulse());
        canvas.heightProperty().addListener((observable, oldValue, newValue)-> redrawOnNextPulse());


        LongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(e -> {
            int zoomDelta = (int) Math.signum(e.getDeltaY());
            if (zoomDelta == 0) return;
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);
            mapParameters.scroll(-e.getX(),-e.getY());
            mapParameters.changeZoomLevel(zoomDelta);
            mapParameters.scroll(e.getX(), e.getY());
            redrawOnNextPulse();
            e.consume();
        });


        canvas.setOnMousePressed(e -> {
            lastMouseX = e.getX();
            lastMouseY = e.getY();
            isDragging = true;
        });


        canvas.setOnMouseDragged(e -> {
            if (isDragging) {
                double deltaX = e.getX() - lastMouseX;
                double deltaY = e.getY() - lastMouseY;
                mapParameters.scroll(deltaX, deltaY);
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                redrawOnNextPulse();
            }
        });
        canvas.setOnMouseReleased(e -> isDragging = false);
    }



    public Pane pane(){
        return pane;
    }

        private void redrawIfNeeded() {
            if (!redrawNeeded) return;
            graphicsContext = canvas.getGraphicsContext2D();
            redrawNeeded = false;
            graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            for (int x = (int) Math.floor(mapParameters.getMinX()/SIZE); x < (canvas.getWidth()+ mapParameters.getMinX())/SIZE; x++) {
                for (int y = (int) Math.floor(mapParameters.getMinY()/SIZE); y <  (canvas.getHeight()+ mapParameters.getMinY())/SIZE; y++) {
                    TileManager.TileId tileId = new TileManager.TileId(mapParameters.getZoom(), x,  y);
                    try {
                        Image tileImage = tileManager.imageForTileAt(tileId);
                        double drawX = x* SIZE - mapParameters.getMinX();
                        double drawY = (y* SIZE - mapParameters.getMinY()) ;
                        graphicsContext.drawImage(tileImage, drawX, drawY);
                    } catch (IOException e) {
                    }
                }
                }
            }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}


