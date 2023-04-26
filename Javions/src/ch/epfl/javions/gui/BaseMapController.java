package ch.epfl.javions.gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

public final class BaseMapController {
    TileManager tileManager;
    MapParameters mapParameters;
    public BaseMapController(TileManager tileManager, MapParameters mapParameters){
        this.tileManager = tileManager;
        this.mapParameters = mapParameters;
    }

    public Pane pane(){
        Pane pane = new Pane();
        Canvas canvas= new Canvas();
        canvas.widthProperty().bind(pane.widthProperty());
        pane.setCenter
    }
}
