package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
        double x = WebMercator.x(zoomLvl, point.longitude()) - mapParameters.getMinXValue() - (canvas.getWidth() * 0.5);
        double y = WebMercator.y(zoomLvl, point.latitude()) - mapParameters.getMinYValue() - (canvas.getHeight() * 0.5);
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
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        double minX = mapParameters.getMinXValue();
        double minY = mapParameters.getMinYValue();
        int firstTileX = (int) minX / PIXELS_IN_TILE;
        int firstTileY = (int) minY / PIXELS_IN_TILE;


        // added 1 for smoother drag transsition
        for (int i = firstTileX; i <= firstTileX + (pane.getWidth() / PIXELS_IN_TILE) + 1; i++) {
            for (int j = firstTileY; j <= firstTileY + (pane.getHeight() / PIXELS_IN_TILE) + 1; j++) {
                TileManager.TileId tile = new TileManager.TileId(mapParameters.getZoomValue(), i, j);
                if(tile.isValid()){
                    try {
                        graphicsContext.drawImage(
                                tileManager.imageForTileAt(tile)
                                , i * PIXELS_IN_TILE - minX
                                , j * PIXELS_IN_TILE - minY);
                    } catch (IOException ignored) {}
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
