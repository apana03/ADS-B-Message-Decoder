package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

import java.io.IOException;

public final class BaseMapController {
    TileManager tileManager;
    MapParameters mapParameters;
    private Canvas canvas;
    private Pane pane;
    private boolean redrawNeeded;

    private double dragInitialX;
    private double dragInitialY;

    private static final int PIXELS_IN_TILE = 256;

    public BaseMapController(TileManager tileManager, MapParameters mapParameters) {
        this.tileManager = tileManager;
        this.mapParameters = mapParameters;

        canvas = new Canvas();
        pane = new Pane(canvas);
        canvas.widthProperty().bind(pane.widthProperty());

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        addAllListeners(mapParameters.getZoom(),mapParameters.getMinX(),mapParameters.getMinY());
        addAllMouseActions();
    }

    public Pane pane() {
        drawMap(pane);
        return pane;
    }

    public void centerOn(GeoPos point) {
        int zoomLvl = mapParameters.getZoomValue();
        double x = WebMercator.x(zoomLvl, point.longitude());
        double y = WebMercator.y(zoomLvl, point.latitude());
        mapParameters = new MapParameters(zoomLvl, x - (canvas.getWidth() / 2), y - (canvas.getHeight() / 2));
        redrawOnNextPulse();
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        drawMap(pane);
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    private void drawMap(Pane pane) {
        int minX = (int) Math.floor(mapParameters.getMinXValue() / PIXELS_IN_TILE);
        int minY = (int) Math.floor(mapParameters.getMinYValue() / PIXELS_IN_TILE);
        double firstTileXPixel = -(mapParameters.getMinXValue() % PIXELS_IN_TILE);
        double firstTileYPixel = -(mapParameters.getMinYValue() % PIXELS_IN_TILE);
        for (int i = minX; i < minX + Math.floor(pane.getWidth() / PIXELS_IN_TILE); i++) {
            for (int j = minY; j < minY + Math.floor(pane.getHeight() / PIXELS_IN_TILE); j++) {
                try {
                    canvas.getGraphicsContext2D().drawImage(
                            tileManager.imageForTileAt(new TileManager.TileId(mapParameters.getZoomValue(), i, j))
                            , firstTileXPixel
                            , firstTileYPixel);
                } catch (IOException e) {

                }
                firstTileXPixel += PIXELS_IN_TILE;
            }
            firstTileYPixel += PIXELS_IN_TILE;
        }
    }

    private void addAllListeners(ReadOnlyIntegerProperty zoom, ReadOnlyDoubleProperty minX, ReadOnlyDoubleProperty minY){
        zoom.addListener((o, oV, nV) -> redrawOnNextPulse());
        minX.addListener((o, oV, nV) -> redrawOnNextPulse());
        minY.addListener((o, oV, nV) -> redrawOnNextPulse());
    }

    private void addAllMouseActions(){
        pane.setOnMousePressed(e -> {
            dragInitialX = e.getX();
            dragInitialY = e.getY();
        });


        pane.setOnMouseDragged(e -> {
            mapParameters.scroll(dragInitialX - e.getX(), dragInitialY - e.getY());
        });

        LongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(e -> {
            int zoomDelta = (int) Math.signum(e.getDeltaY());
            if (zoomDelta == 0) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);

            double xTranslation = e.getX();
            double yTranslation = e.getY();
            mapParameters.scroll(xTranslation, yTranslation);
            mapParameters.changeZoomLevel(zoomDelta);
            mapParameters.scroll(-xTranslation, -yTranslation);
        });

    }
}
