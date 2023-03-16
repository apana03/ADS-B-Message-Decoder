package ch.epfl.javions;

import ch.epfl.javions.adsb.AdsbDemodulator;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.demodulation.PowerComputer;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import java.io.*;
import java.net.URLDecoder;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;


public final class PrintRawMessages {

    @Test
    void test() throws IOException {
        //String f = URLDecoder.decode(getClass().getResource("Javions/resources/samples_20230304_1442.bin").getFile(), UTF_8);
        InputStream stream = new FileInputStream("Javions/resources/samples_20230304_1442.bin");
        AdsbDemodulator d = new AdsbDemodulator(stream);
        RawMessage m;
        int mesCreated = 0;
        while ((m = d.nextMessage()) != null) {
            System.out.println(m);
            mesCreated++;
        }
        System.out.println(mesCreated);
    }
}