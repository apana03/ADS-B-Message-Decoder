package ch.epfl.javions.gui;

import javafx.scene.image.Image;

import javax.print.DocFlavor;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public final class TileManager {
    public record TileId(int zoom, int x, int y){
        public boolean isValid(){
            return (x >= 0) && (x < (1 << zoom)) && (y >= 0) && (y < (1 << zoom));
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
    public Image imageForTileAt(TileId tileId) throws IOException {
        if(cache.containsKey(tileId)){
            return cache.get(tileId);
        }
        Path path = Path.of(tileId.zoom() + "/" + tileId.x() + "/" + tileId.y() + ".png");
        if(cache.size() == MAX_CACHE_CAPACITY){
            cache.remove(cache.keySet().iterator().next());
        }
        if(Files.exists(path)){
            try(FileInputStream is = new FileInputStream(path.toFile())){
                Image image = new Image(is);
                cache.put(tileId,image);
                return image;
        }
        }else{
            URL u = new URL(serverUrl + "/" + tileId.zoom() + "/" + tileId.x() + "/" + tileId.y() + ".png");
            URLConnection c = u.openConnection();
            Files.createDirectories(path.getParent());
            c.setRequestProperty("User-Agent", "Javions");
            try(InputStream is = c.getInputStream()){
                byte[] buffer = is.readAllBytes();
                FileOutputStream o = new FileOutputStream(path.toFile());
                o.write(buffer);
                Image image = new Image(new ByteArrayInputStream(buffer));
                cache.put(tileId , image);
                return image;
            }
        }
    }
}
