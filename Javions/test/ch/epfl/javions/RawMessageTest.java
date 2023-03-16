package ch.epfl.javions;

import ch.epfl.javions.adsb.RawMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RawMessageTest {
    @Test
    public void SizeMethodWorks(){
        byte byte0 = (byte) 0b10001000;
        byte byte1 = 12;
        assertEquals(RawMessage.size(byte0),RawMessage.LENGTH);
        assertEquals(RawMessage.size(byte1),0);
    }

    @Test
    public void TypeCodeMethpodWorks(){
        long payload1 = 1<<63;
        long payload2 = 1<<59;
        assertEquals(RawMessage.typeCode(payload1),16);
        assertEquals(RawMessage.typeCode(payload2),1);

    }
}
