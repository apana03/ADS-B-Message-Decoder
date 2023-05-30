package ch.epfl.javions;

import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.gui.AircraftStateManager;

import java.io.*;

public class ObservableTest
{
   /* public static void main(String[] args) throws IOException{
        MessageParser parser = new MessageParser();
        try(DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream("Javions/resources/messages_20230318_0915.bin")))){
            AircraftStateManager manager = new AircraftStateManager(new AircraftDatabase("Javions/resources/messages_20230318_0915.bin"));
            byte[] bytes = new byte[RawMessage.LENGTH];
            while (true) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);
                RawMessage mess = new RawMessage(timeStampNs, message);
                manager.updateWithMessage(parser.parse(mess));
                System.out.printf("%13d: %s\n", timeStampNs, message);
            }

        } catch (EOFException e) { /* nothing to do  }
    */
    }
