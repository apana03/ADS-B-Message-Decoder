package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

import java.io.IOException;

public final class BaseMapController {
    private final TileManager tileManager;
    private final MapParameters mapParameters;
    private  final Canvas canvas;
    private final Pane pane;
    private boolean redrawNeeded;
    private Point2D dragInitial;


    private static final int PIXELS_IN_TILE = 256;

    public BaseMapController(TileManager tileManager, MapParameters mapParameters) {
        this.tileManager = tileManager;
        this.mapParameters = mapParameters;

        canvas = new Canvas();
        pane = new Pane(canvas);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        addAllListeners(mapParameters.getZoom(), mapParameters.getMinX(), mapParameters.getMinY());
        addAllMouseActions();
    }

    public Pane pane() {
        return pane;
    }

    public void centerOn(GeoPos point) {
        int zoomLvl = mapParameters.getZoomValue();
        double x = mapParameters.getMinXValue() - WebMercator.x(zoomLvl, point.longitude()) - ((0.5 * canvas.getWidth()) - mapParameters.getMinXValue());
        double y = mapParameters.getMinYValue() - WebMercator.y(zoomLvl, point.latitude()) - ((0.5 * canvas.getWidth()) - mapParameters.getMinYValue());
        mapParameters.scroll(x, y);
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
        double minX = mapParameters.getMinXValue();
        double minY = mapParameters.getMinYValue();
        int tileX = (int) minX / PIXELS_IN_TILE;
        int tileY = (int) minY / PIXELS_IN_TILE;

        for (int i = tileX; i <= tileX + Math.ceil(pane.getWidth() / PIXELS_IN_TILE); i++) {
            for (int j = tileY; j <= tileY + Math.ceil(pane.getHeight() / PIXELS_IN_TILE); j++) {
                try {
                    canvas.getGraphicsContext2D().drawImage(
                            tileManager.imageForTileAt(new TileManager.TileId(mapParameters.getZoomValue(), i, j))
                            , i * PIXELS_IN_TILE - minX
                            , j * PIXELS_IN_TILE - minY);
                } catch (IOException e) {

                }
            }
        }
    }

    private void addAllListeners(ReadOnlyIntegerProperty zoom, ReadOnlyDoubleProperty minX, ReadOnlyDoubleProperty minY) {
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        zoom.addListener((o, oV, nV) -> redrawOnNextPulse());
        minX.addListener((o, oV, nV) -> redrawOnNextPulse());
        minY.addListener((o, oV, nV) -> redrawOnNextPulse());
        canvas.widthProperty().addListener((o, oV, nV) -> redrawOnNextPulse());
        canvas.heightProperty().addListener((o, oV, nV) -> redrawOnNextPulse());
    }

    private void addAllMouseActions() {
        dragInitial = new Point2D(0,0);
        pane.setOnMousePressed(e -> dragInitial = new Point2D(e.getX(),e.getY()));


        pane.setOnMouseDragged(e -> {
            double presentX = e.getX();
            double presentY = e.getY();
            mapParameters.scroll( dragInitial.getX() - presentX, dragInitial.getY() - presentY);
            dragInitial = new Point2D(presentX, presentY);
        });

        pane.setOnMouseReleased(e -> dragInitial = null);

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
