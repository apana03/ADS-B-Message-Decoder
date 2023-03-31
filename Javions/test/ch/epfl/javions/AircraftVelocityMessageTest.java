package ch.epfl.javions;

import ch.epfl.javions.adsb.AirborneVelocityMessage;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class AircraftVelocityMessageTest {
    @Test
    void testSubType34() throws IOException {
        var message1 = RawMessage.of(100, ByteString.ofHexadecimalString("8D485020994409940838175B284F").getBytes());
        System.out.println(AirborneVelocityMessage.of(message1));
        var message2  = RawMessage.of(100, ByteString.ofHexadecimalString("8DA05F219B06B6AF189400CBC33F").getBytes());
        System.out.println(AirborneVelocityMessage.of(message2));
    }
    @Test
    void testSubType345w() throws IOException {
        var message1 = RawMessage.of(100, ByteString.ofHexadecimalString("8D485020994409940838175B284F").getBytes());
        System.out.println(AirborneVelocityMessage.of(message1));
        var message2  = RawMessage.of(100, ByteString.ofHexadecimalString("8DA05F219B06B6AF189400CBC33F").getBytes());
        System.out.println(AirborneVelocityMessage.of(message2));
        var message3  = RawMessage.of(10000, ByteString.ofHexadecimalString("8D485020994409940838175B284F").getBytes());
        System.out.println(AirborneVelocityMessage.of(message3));
    }
    @Test
    void printAllValues() throws IOException {
        String f = URLDecoder.decode(getClass().getResource("/samples_20230304_1442.bin").getFile(), StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(f);
        AdsbDemodulator d = new AdsbDemodulator(stream);
        RawMessage m;
        int mesCreated = 0;
        while ((m = d.nextMessage()) != null) {
            if (m.typeCode() == 19) {
                var aux = AirborneVelocityMessage.of(m);
                if (aux != null) {
                    //System.out.println((long) (m.payload() >> 48) & ((1 << 3) - 1));
                    System.out.println(aux);
                }
                mesCreated++;
            }
        }
        System.out.println(mesCreated);
    }
}
