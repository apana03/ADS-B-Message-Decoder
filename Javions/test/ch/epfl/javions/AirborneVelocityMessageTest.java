package ch.epfl.javions;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.*;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class AirborneVelocityMessageTest
{
    @Test
    void AirborneVelocityMessageReturnsNullWithInvalidMessages()
    {
        RawMessage rm1 = new RawMessage(0, ByteString.ofHexadecimalString("8D485020994409800838175B284F"));
        RawMessage rm2 = new RawMessage(0, ByteString.ofHexadecimalString("8D485020994400140838175B284F"));
        RawMessage rm3 = new RawMessage(0, ByteString.ofHexadecimalString("8D4850209C4609800838175B284F"));
        RawMessage rm4 = new RawMessage(0, ByteString.ofHexadecimalString("8bfffffffffffffffbffffffffff"));

        assertNull(AirborneVelocityMessage.of(rm1));
        assertNull(AirborneVelocityMessage.of(rm2));
        assertNull(AirborneVelocityMessage.of(rm3));
        assertNull(AirborneVelocityMessage.of(rm4));
    }
    @Test
    void AirborneVelocityMessageWorksOnGivenValues()
    {
        RawMessage rm = new RawMessage(0, ByteString.ofHexadecimalString("8D485020994409940838175B284F"));
        AirborneVelocityMessage message1 = AirborneVelocityMessage.of(rm);

        double velocity = 81.90013721178154;
        double track = 3.1918647255875205;
        assertEquals(velocity, message1.speed());
        assertEquals(track, message1.trackOrHeading());
    }

    @Test
    void AirborneVelocityMessageWorksOnGivenValues2()
    {
        RawMessage rm = new RawMessage(0, ByteString.ofHexadecimalString("8DA05F219B06B6AF189400CBC33F"));
        AirborneVelocityMessage message = AirborneVelocityMessage.of(rm);

        double velocity = 192.91666666666669;
        double track = 4.25833066717054;
        assertEquals(velocity, message.speed());
        assertEquals(track, message.trackOrHeading());

    }

    @Test
    void AirborneVelocityMessageWorksOnGivenValues3()
    {
        RawMessage rm = new RawMessage(0, ByteString.ofHexadecimalString("8DA05F219C06B6AF189400CBC33F"));
        AirborneVelocityMessage message = AirborneVelocityMessage.of(rm);

        double velocity = 4*192.91666666666669;
        double track = 4.25833066717054;
        assertEquals(velocity, message.speed());
        assertEquals(track, message.trackOrHeading());

    }

    @Test
    void AirborneVelocityMessageWorksOnGivenValues4()
    {
        RawMessage rm = new RawMessage(0, ByteString.ofHexadecimalString("8D4B1A00EA0DC89E8F7C0857D5F5"));
        AirborneVelocityMessage message = AirborneVelocityMessage.of(rm);

        double velocity = 1061.4503686262444;
        double track = 4.221861463749146;
        assertEquals(velocity, message.speed());
        assertEquals(track, message.trackOrHeading());
    }

    @Test
    void SousType3Or4Works(){
        String message = "8DA05F219B06B6AF189400CBC33F";
        ByteString byteString = ByteString.ofHexadecimalString(message);
        long timestamps = 0;
        RawMessage test = new RawMessage(timestamps, byteString);
        AirborneVelocityMessage avm = AirborneVelocityMessage.of(test);
        assertEquals(192.91666666666669 , avm.speed());
        assertEquals(4.25833066717054 , avm.trackOrHeading());
    }

    @Test
    void PrintAirborneVelocityMessage() throws IOException
    {
        int count = 0;
        String f = "javions/resources/samples_20230304_1442.bin";
        try (InputStream s = new FileInputStream(f)) {

            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            while ((m = d.nextMessage()) != null)
            {
                if ((m.typeCode() == 19))
                {
                    System.out.println(AirborneVelocityMessage.of(m));
                    count++;
                }
            }
        }
        System.out.println(count);
    }
}