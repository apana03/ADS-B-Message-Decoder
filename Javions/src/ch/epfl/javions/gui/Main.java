package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.Thread.sleep;

public class Main extends Application
{
    private static final long PURGE_INTERVAL = 1_000_000_000L;
    private static final int ZOOM_LEVEL = 8;
    private static final int MIN_X_VALUE = 33530;
    private static final int MIN_Y_VALUE = 23070;
    private static final long NANO_TO_MILI = 1_000_000L;
    private static final int MIN_HEIGHT = 600;
    private static final int MIN_WIDTH = 800;
    private static final String SERVER_URL = "tile.openstreetmap.org";
    private static final String TILE_CACHE = "tile-cache";
    private static final String AIRCRAFT_FOLDER_ZIPPED = "/aircraft.zip";
    private static final String TITLE = "Javions";
    public static void main(String[] args) { launch(args); }
    @Override
    public void start(Stage primaryStage) throws Exception {
        long startTime = System.nanoTime();
        URL u = getClass().getResource(AIRCRAFT_FOLDER_ZIPPED);
        assert u != null;
        Path p = Path.of(u.toURI());
        AircraftDatabase database = new AircraftDatabase(p.toString());
        ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<>();
        AircraftStateManager asm = new AircraftStateManager(database);
        MapParameters mp = new MapParameters(ZOOM_LEVEL, MIN_X_VALUE, MIN_Y_VALUE);
        ObjectProperty<ObservableAircraftState> sap = new SimpleObjectProperty<>();
        AircraftController ac = new AircraftController(mp, asm.states(), sap);
        Path tileCache = Path.of(TILE_CACHE);
        TileManager tm = new TileManager(tileCache, SERVER_URL);
        BaseMapController bmc = new BaseMapController(tm, mp);
        AircraftTableController atc = new AircraftTableController(asm.states(), sap);
        atc.setOnDoubleClick(s -> bmc.centerOn(s.getPosition()));
        StatusLineController slc = new StatusLineController();
        slc.getAircraftCountProperty().bind(Bindings.size(asm.states()));
        StackPane stp = new StackPane(bmc.pane(), ac.pane());
        BorderPane bp = new BorderPane(atc.pane());
        bp.setTop(slc.pane());
        Thread thread;
        if(getParameters().getRaw().isEmpty()) {
            thread = new Thread(() -> {
                getParameters().getRaw();
                try{
                    AdsbDemodulator adsb = new AdsbDemodulator(System.in);
                    RawMessage rmsg = adsb.nextMessage();
                    while(true) {
                        Message msg = MessageParser.parse(rmsg);
                        if(msg != null)
                            messageQueue.add(msg);
                        rmsg = adsb.nextMessage();
                    }
                }catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } else {
            thread = new Thread(() -> {
                try {
                    for(RawMessage rmsg : readMessages(getParameters().getRaw().get(0))){
                        long currentTime = System.nanoTime() - startTime;
                        if(currentTime < rmsg.timeStampNs())
                            sleep((rmsg.timeStampNs() - currentTime) / NANO_TO_MILI);
                        Message msg = MessageParser.parse(rmsg);
                        if(msg != null)
                            messageQueue.add(msg);
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }catch(InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        thread.setDaemon(true);
        thread.start();
        SplitPane sp = new SplitPane(stp, bp);
        sp.setOrientation(javafx.geometry.Orientation.VERTICAL);
        Scene scene = new Scene(sp);
        primaryStage.setTitle(TITLE);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setScene(scene);
        primaryStage.show();
        new AnimationTimer() {
            private long lastPurge = 0L;
            @Override
            public void handle(long now) {
                if(messageQueue.isEmpty()) return;
                try{
                    while(!messageQueue.isEmpty()){
                        Message msg = messageQueue.remove();
                        asm.updateWithMessage(msg);
                        slc.getMessageCountProperty().set(slc.getMessageCountProperty().get() + 1);
                    }
                    if(now - lastPurge > PURGE_INTERVAL){
                        lastPurge = now;
                        asm.purge();
                    }
            } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();

    }
    private static List<RawMessage> readMessages(String name) throws IOException {
        List<RawMessage> messages = new ArrayList<>();
        try(DataInputStream dis = new DataInputStream(
                new BufferedInputStream(
                    new FileInputStream(name)))) {
            byte[] buffer = new byte[RawMessage.LENGTH];
            while(true){
                long tstp = dis.readLong();
                int byteCount = dis.readNBytes(buffer, 0, buffer.length);
                assert byteCount == RawMessage.LENGTH;
                ByteString bs = new ByteString(buffer);
                RawMessage rmsg = new RawMessage(tstp, bs);
                messages.add(rmsg);
            }
        }catch(EOFException e) {
            return messages;
        }
    }
}
