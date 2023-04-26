package ch.epfl.javions.gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

import java.io.IOException;

public final class BaseMapController {
    TileManager tileManager;
    MapParameters mapParameters;
    public BaseMapController(TileManager tileManager, MapParameters mapParameters){
        this.tileManager = tileManager;
        this.mapParameters = mapParameters;
    }

    public Pane pane() throws IOException {
        Canvas canvas = new Canvas();
        Pane pane = new Pane(canvas);
        canvas.widthProperty().bind(pane.widthProperty());
        int minX = (int) Math.floor(mapParameters.getMinXValue() / 256);
        int minY = (int) Math.floor(mapParameters.getMinYValue() / 256);
        for( int i = minX; i < minX + Math.floor(pane.getWidth() / 256); i++)
            for( int j = minY; j < minY + Math.floor(pane.getHeight() / 256; j++)
                canvas.getGraphicsContext2D().drawImage(tileManager.imageForTileAt(
                        new TileManager.TileId(mapParameters.getZoomValue(), i, j)),
                        (i - minX) * 256, (j - minY) * 256);
        return pane;
    }
}
