package ch.epfl.javions;

import ch.epfl.javions.adsb.AircraftIdentificationMessage;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AirCraftIdMessageTest {
    @Test
    void testFirst5Values() throws IOException {
        String f = URLDecoder.decode(getClass().getResource("/samples_20230304_1442.bin").getFile(), UTF_8);
        InputStream stream = new FileInputStream(f);
        AdsbDemodulator d = new AdsbDemodulator(stream);
        RawMessage m;
        int mesCreated = 0;
        while ((m = d.nextMessage()) != null) {
            if (m.typeCode() >= 1 && m.typeCode() <= 4) {
                var aux = AircraftIdentificationMessage.of(m);
                if (aux != null)
                    System.out.println(aux);
                mesCreated++;
            }
        }
        System.out.println(mesCreated);
    }
}