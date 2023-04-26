package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;

public final class MapParameters {
    private IntegerProperty zoom;
    private DoubleProperty minX,minY;
    private final static int MIN_ZOOM = 6, MAX_ZOOM =19;
    public MapParameters(IntegerProperty zoom , DoubleProperty minX, DoubleProperty minY){
        Preconditions.checkArgument(zoom.get() >= MIN_ZOOM || zoom.get()<= MAX_ZOOM);
        this.zoom = zoom;
        this.minX =minX;
        this.minY = minY;
    }

    public ReadOnlyIntegerProperty getZoom(){
        return zoom;
    }

    public int getZoomValue(){
        return zoom.get();
    }

    public ReadOnlyDoubleProperty getMinX(){
        return minX;
    }
    public double getMinXValue(){
        return minX.get();
    }

    public ReadOnlyDoubleProperty getMinY(){
        return minY;
    }
    public double getMinYValue(){
        return minY.get();
    }

    public void scroll(double x, double y){
        minX.set(minX.get() + x);
        minY.set(minY.get() + y);
    }

    public void changeZoomLevel(int zoomDifference){
        zoom.set(Math2.clamp(MIN_ZOOM,zoom.get()+zoomDifference,MAX_ZOOM));
    }
}
