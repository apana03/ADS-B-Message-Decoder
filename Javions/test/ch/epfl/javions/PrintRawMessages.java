package ch.epfl.javions;

import ch.epfl.javions.demodulation.AdsbDemodulator;
import ch.epfl.javions.adsb.RawMessage;
import org.junit.jupiter.api.Test;

import java.io.*;


public final class PrintRawMessages {
    @Test
    void newtest() throws IOException {
        int count = 0;
        String f = "Javions/resources/samples_20230304_1442.bin";
        try (InputStream s = new FileInputStream(f)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            while ((m = d.nextMessage()) != null) {
                System.out.println(m);
                count++;

            }
        }
    }
}