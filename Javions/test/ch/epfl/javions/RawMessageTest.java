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
    @Test
    public void Constructeur() {
        assertThrows(IllegalArgumentException.class, () -> {
            RawMessage test = new RawMessage(-1, new ByteString(new byte[14]));
        });
    }


    // Taille ByteString mauvaise
    @Test
    public void Constructeur1() {
        assertThrows(IllegalArgumentException.class, () -> {
            RawMessage test = new RawMessage(2, new ByteString(new byte[1]));
        });
    }


    @Test
    public void Constructeur2() {
        assertDoesNotThrow(() -> {new RawMessage(2, new ByteString(new byte[14]));
        });
    }

    @Test
    void SizeTest(){
        byte test1 = 70; byte test2 = 0b01000110; byte test3 = 0x46; byte test4 = (byte) 0x8D;
        int Test1 = RawMessage.size(test1);
        int Test2 = RawMessage.size(test2);
        int Test3 = RawMessage.size(test3);
        int Test4 = RawMessage.size(test4);
        assertEquals(0, Test1); assertEquals(0,Test2); assertEquals(0,Test3);
        assertEquals(14,Test4);
    }


    @Test void AllMethodsInOneTest(){
        String message = "8D4B17E5F8210002004BB8B1F1AC";
        ByteString byteString = ByteString.ofHexadecimalString(message); int timestamps = 0;
        RawMessage test = new RawMessage(timestamps , byteString);
        IcaoAddress icao = test.icaoAddress();
        int typecode = test.typeCode();
        int df = test.downLinkFormat();
        long payload = test.payload();
        int typecode2 = RawMessage.typeCode(payload);
        assertEquals( "4B17E5",icao.string());
        assertEquals(17,df);
        assertEquals(31,typecode);
        assertEquals(31,typecode2);
        String message2 = "8D49529958B302E6E15FA352306B";
        ByteString byteString2 = ByteString.ofHexadecimalString(message2);
        int timestamps2 = 0;
        RawMessage test2 = new RawMessage(timestamps2 , byteString2);
        IcaoAddress icao2 = test2.icaoAddress();
        int typecode22 = test2.typeCode();
        int df2 = test2.downLinkFormat();
        long payload2 = test2.payload();
        int typecode222 = RawMessage.typeCode(payload2);
        assertEquals( "495299",icao2.string());
        assertEquals(17,df2);
        assertEquals(11,typecode22);
        assertEquals(11,typecode222);
    }

}
