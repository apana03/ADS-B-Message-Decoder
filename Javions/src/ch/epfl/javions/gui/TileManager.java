package ch.epfl.javions.gui;

import javafx.scene.image.Image;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public final class TileManager {
    public record TileId(int z, int x, int y){
        public boolean isValid(){
            return (x >= 0) && (x < (1 << z)) && (y >= 0) && (y < (1 << z));
        }
    }
    private final Path localCache;
    private final String serverUrl;
    private final static int MAX_CACHE_CAPACITY = 100;
    private final static float LOAD_FACTOR = 0.75f;
    private LinkedHashMap<TileId, Image> cache;
    public TileManager(Path localCache, String serverUrl) {
        this.localCache = localCache;
        this.serverUrl = serverUrl;
        cache = new LinkedHashMap<>(MAX_CACHE_CAPACITY, LOAD_FACTOR, true);
    }
    public Image imageForTileAt(TileId tileId){
        return null;
    }
}
