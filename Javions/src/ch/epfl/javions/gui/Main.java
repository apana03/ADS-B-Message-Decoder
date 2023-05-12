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
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.Thread.sleep;

public class Main extends Application
{
    public static void main(String[] args) { launch(args); }
    @Override
    public void start(Stage primaryStage) throws Exception {

        URL u = getClass().getResource("/aircraft.zip");
        assert u != null;
        Path p = Path.of(u.toURI());
        AircraftDatabase database = new AircraftDatabase(p.toString());

        ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<>();

        AircraftStateManager asm = new AircraftStateManager(database);

        MapParameters mp = new MapParameters(8, 33530, 23070);
        ObjectProperty<ObservableAircraftState> sap = new SimpleObjectProperty<>();

        AircraftController ac = new AircraftController(mp, asm.states(), sap);

        Path tileCache = Path.of("tile-cache");
        TileManager tm = new TileManager(tileCache, "tile.openstreetmap.org");

        BaseMapController bmc = new BaseMapController(tm, mp);

        AircraftTableController atc = new AircraftTableController(asm.states(), sap);
        atc.setOnDoubleClick(s -> bmc.centerOn(s.getPosition()));

        StatusLineController slc = new StatusLineController();
        slc.getAircraftCountProperty().bind(Bindings.size(asm.states()));

        StackPane stp = new StackPane(bmc.pane(), ac.pane());
        primaryStage.setScene(new Scene(stp));
        primaryStage.show();
        BorderPane bp = new BorderPane(atc.pane());
        bp.setTop(slc.pane());
        SplitPane sp = new SplitPane(stp, bp);
        sp.setOrientation(javafx.geometry.Orientation.VERTICAL);
        sp.getItems().addAll(stp, bp);
        BorderPane root = new BorderPane(sp);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Javions");
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);
        primaryStage.setScene(scene);
        primaryStage.show();
        Thread thread;

        if(getParameters().getRaw().isEmpty()) {
            thread = new Thread(() -> {
                getParameters().getRaw().get(0);
                try{
                    AdsbDemodulator adsb = new AdsbDemodulator(System.in);
                    RawMessage rmsg = adsb.nextMessage();
                    while(rmsg != null) {
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
                        if(System.nanoTime() < rmsg.timeStampNs())
                            sleep((rmsg.timeStampNs() - System.nanoTime()));
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

        thread.start();
        thread.setDaemon(true);
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
                    if(now - lastPurge > 1_000_000_000L){
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
        List<RawMessage> messages = List.of();
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
