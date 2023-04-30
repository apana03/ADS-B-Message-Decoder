package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import javafx.beans.property.*;

public final class MapParameters {
    private IntegerProperty zoom;
    private DoubleProperty minX, minY;
    private final static int MIN_ZOOM = 6, MAX_ZOOM = 19;

    public MapParameters(int zoom, double minX, double minY) {
        Preconditions.checkArgument(zoom >= MIN_ZOOM && zoom <= MAX_ZOOM);
        this.zoom = new SimpleIntegerProperty(zoom);
        this.minX = new SimpleDoubleProperty(minX);
        this.minY = new SimpleDoubleProperty(minY);
    }

    public ReadOnlyIntegerProperty getZoom() {
        return zoom;
    }

    public int getZoomValue() {
        return zoom.get();
    }

    public ReadOnlyDoubleProperty getMinX() {
        return minX;
    }

    public double getMinXValue() {
        return minX.get();
    }

    public ReadOnlyDoubleProperty getMinY() {
        return minY;
    }

    public double getMinYValue() {
        return minY.get();
    }

    public void scroll(double x, double y) {
        minX.set(minX.get() + x);
        minY.set(minY.get() + y);
    }

    public void changeZoomLevel(int zoomDifference) {
        int previousZoom = zoom.get();
        zoom.set(Math2.clamp(MIN_ZOOM, previousZoom + zoomDifference, MAX_ZOOM));
        zoomDifference = previousZoom - zoom.get();
        System.out.println(zoomDifference);
        adaptTopLeftCorner(zoomDifference);
    }

    private void adaptTopLeftCorner(int zoomDifference) {
        double var = 1 / Math.pow(2, zoomDifference);
        minX.set(minX.get() * var);
        minY.set(minY.get() * var);
    }
}
