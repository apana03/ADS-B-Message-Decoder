package ch.epfl.javions;

import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

public class RawMessageTest {
    private final static HexFormat hf = HexFormat.of();
    private final static byte [] tab = hf.parseHex("8D4B17E5F8210002004BB8B1F1AC");
    private final static ByteString bytes = new ByteString(tab);

    @Test
    public void SizeMethodWorks(){
        byte byte0 = (byte) 0b10001000;
        byte byte1 = 12;
        assertEquals(RawMessage.size(byte0),RawMessage.LENGTH);
        assertEquals(RawMessage.size(byte1),0);
    }

    @Test
    public void TypeCodeMethpodWorks(){
        long payload1 = (long) 1 << 55;
        long payload2 = (long) 1 << 51;
        assertEquals(RawMessage.typeCode(payload1),16);
        assertEquals(RawMessage.typeCode(payload2),1);

    }
    @Test
    void RawMessageThrowsIllegalArgumentException()
    {
        byte [] tab1 = {0, 1, 2};
        byte [] tab2 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
        int timeStamp1 = -2;
        int timeStamp2 =  0;
        ByteString byteString1 = new ByteString(tab1);
        ByteString byteString2 = new ByteString(tab2);

        assertThrows(IllegalArgumentException.class, ()-> new RawMessage(timeStamp2, byteString1));
        assertThrows(IllegalArgumentException.class, ()-> new RawMessage(timeStamp1, byteString2));
        assertDoesNotThrow(()-> new RawMessage(timeStamp2, byteString2));
    }

    @Test
    void SizeWorksOnKnownValues()
    {
        byte byte0 = (byte) 0b010001000;
        int actual = RawMessage.size(byte0);
        int expected = RawMessage.LENGTH;
        assertEquals(expected, actual);

        byte byte1 = 0b01010100;
        actual = RawMessage.size(byte1);
        expected = 0;
        assertEquals(expected, actual);

        byte byte2 = 0b00000000;
        actual = RawMessage.size(byte2);
        assertEquals(expected, actual);
    }

    @Test
    void TypeCodeWorksOnKnownValues()
    {
        RawMessage message = new RawMessage(8096200, bytes);
        int expected = 0b00011111;
        int actual = RawMessage.typeCode(bytes.bytesInRange(4, 11));
        assertEquals(expected, actual);
        actual = message.typeCode();
        assertEquals(expected, actual);
    }

    @Test
    void DownLinkFormatWorksOnKnownValues()
    {
        RawMessage message = new RawMessage(8096200, bytes);

        int expected = 0b10001;
        int actual = message.downLinkFormat();

        assertEquals(expected, actual);
    }

    @Test
    void IcaoAddressWorksOnKnownValues()
    {
        RawMessage message = new RawMessage(8096200, bytes);

        IcaoAddress expected = new IcaoAddress("4B17E5");
        IcaoAddress actual = message.icaoAddress();

        assertEquals(expected, actual);
    }

    @Test
    void PayloadWorksOnKnownValues()
    {
        RawMessage message = new RawMessage(8096200, bytes);

        long actual = message.payload();
        long expected = 0xF8210002004BB8L;

        assertEquals(expected, actual);
    }
}
