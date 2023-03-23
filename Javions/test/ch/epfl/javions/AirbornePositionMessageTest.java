package ch.epfl.javions;


import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.AirbornePositionMessage;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;


import java.util.HexFormat;


import static org.junit.jupiter.api.Assertions.*;


public class AirbornePositionMessageTest {
    private final static HexFormat hf = HexFormat.of();
    private final static byte [] tab1 = hf.parseHex("8D49529958B302E6E15FA352306B");
    private final static ByteString bytes1 = new ByteString(tab1);
    private final static long timeStamp1 = 75898000;
    private static final RawMessage message = new RawMessage(timeStamp1, bytes1);


    private final static byte [] tab2 = hf.parseHex("8D4241A9601B32DA4367C4C3965E");
    private final static ByteString bytes2 = new ByteString(tab2);
    private final static long timeStamp2 = 116538700;
    private static final RawMessage message2 = new RawMessage(timeStamp2, bytes2);


    private final static byte [] tab3 = hf.parseHex("8D4D222860B985F7F53FAB33CE76");
    private final static ByteString bytes3 = new ByteString(tab3);
    private final static long timeStamp3 = 138560100;
    private static final RawMessage message3 = new RawMessage(timeStamp3, bytes3);
/*
   @Test
   void ExempleDuProfAdsbDemodulator() throws IOException {


       String f = "resources/samples_20230304_1442.bin";
       try (InputStream s = new FileInputStream(f)) {


           AdsbDemodulator d = new AdsbDemodulator(s);
           RawMessage m;
           while ((m = d.nextMessage()) != null) {
               AirbornePositionMessage e = AirbornePositionMessage.of(m);
               System.out.println(e.timeStampNs());
               System.out.println(e.icaoAddress());
               System.out.println(e.altitude());
               System.out.println(e.parity());
               System.out.println(e.x());
               System.out.println(e.y());
           }
       }
   }
*/


    @Test
    void ofWorksOnKnownValues1()
    {
        AirbornePositionMessage PositionMessage = AirbornePositionMessage.of(message);
        int expectedTimstamp = 75898000;
        IcaoAddress expectedICAO = new IcaoAddress("495299");
        double expectedAltitude = 10546.08;
        double expectedParity = 0;
        double expectedX = 0.6867904663085938;
        double expectedY = 0.7254638671875;


        assertEquals(expectedTimstamp, PositionMessage.timeStampNs());
        assertEquals(expectedICAO, PositionMessage.icaoAddress());
        assertEquals(expectedAltitude, PositionMessage.altitude());
        assertEquals(expectedParity, PositionMessage.parity());
        assertEquals(expectedX, PositionMessage.x());
        assertEquals(expectedY, PositionMessage.y());
    }


    @Test
    void ofWorksOnKnownValues2()
    {
        AirbornePositionMessage PositionMessage1 = AirbornePositionMessage.of(message2);
        int expectedTimeStampNs = 116538700;
        IcaoAddress expectedICAO = new IcaoAddress("4241A9");
        double expectedAltitude = 1303.02;
        double expectedParity = 0;
        double expectedX = 0.702667236328125;
        double expectedY = 0.7131423950195312;


        assertEquals(expectedTimeStampNs, PositionMessage1.timeStampNs());
        assertEquals(expectedICAO, PositionMessage1.icaoAddress());
        assertEquals(expectedAltitude, PositionMessage1.altitude());
        assertEquals(expectedParity, PositionMessage1.parity());
        assertEquals(expectedX, PositionMessage1.x());
        assertEquals(expectedY, PositionMessage1.y());
    }


    @Test
    void ofWorksOnKnownValues3()
    {
        AirbornePositionMessage PositionMessage1 = AirbornePositionMessage.of(message3);
        int expectedTimeStampNs = 138560100;
        IcaoAddress expectedICAO = new IcaoAddress("4D2228");
        double expectedAltitude = 10972.800000000001;
        double expectedParity = 1;
        double expectedX = 0.6243515014648438;
        double expectedY = 0.4921417236328125;


        assertEquals(expectedTimeStampNs, PositionMessage1.timeStampNs());
        assertEquals(expectedICAO, PositionMessage1.icaoAddress());
        assertEquals(expectedAltitude, PositionMessage1.altitude());
        assertEquals(expectedParity, PositionMessage1.parity());
        assertEquals(expectedX, PositionMessage1.x());
        assertEquals(expectedY, PositionMessage1.y());
    }


    @Test
    void testConstructorThrowsNullPointerException() {
        IcaoAddress icaoAddress = null;
        assertThrows(
                NullPointerException.class,
                () -> new AirbornePositionMessage(System.nanoTime(), icaoAddress, 1000.0, 0, 0, 0.5)
        );
    }


    @Test
    void testConstructorThrowsIllegalArgumentExceptionForNegativeTimestamp() {
        IcaoAddress icaoAddress = new IcaoAddress("123456");
        assertThrows(
                IllegalArgumentException.class,
                () -> new AirbornePositionMessage(-1L, icaoAddress, 1000.0, 0, 0.5, 0.5)
        );
    }


    @Test
    void testConstructorThrowsIllegalArgumentExceptionForInvalidParity() {
        IcaoAddress icaoAddress = new IcaoAddress("123456");
        assertThrows(
                IllegalArgumentException.class,
                () -> new AirbornePositionMessage(System.nanoTime(), icaoAddress, 1000.0, 2, 0.5, 0.5)
        );
    }


    @Test
    void testConstructorThrowsIllegalArgumentExceptionForInvalidXValue() {
        IcaoAddress icaoAddress = new IcaoAddress("123456");
        assertThrows(
                IllegalArgumentException.class,
                () -> new AirbornePositionMessage(System.nanoTime(), icaoAddress, 1000.0, 0, -0.5, 0.5)
        );
    }


    @Test
    void testConstructorThrowsIllegalArgumentExceptionForInvalidYValue() {
        IcaoAddress icaoAddress = new IcaoAddress("123456");
        assertThrows(
                IllegalArgumentException.class,
                () -> new AirbornePositionMessage(System.nanoTime(), icaoAddress, 1000.0, 0, 0.5, 2.0)
        );
    }


    @Test
    void testConstructorWithValidArguments() {
        IcaoAddress icaoAddress = new IcaoAddress("123456");
        AirbornePositionMessage message = new AirbornePositionMessage(System.nanoTime(), icaoAddress, 1000.0, 0, 0.5, 0.5);
        assertNotNull(message);
        assertEquals(icaoAddress, message.icaoAddress());
        assertEquals(1000.0, message.altitude());
        assertEquals(0, message.parity());
        assertEquals(0.5, message.x());
        assertEquals(0.5, message.y());
    }


    @Test
    void testGettersAndSetters() {
        IcaoAddress icaoAddress = new IcaoAddress("123456");
        AirbornePositionMessage message = new AirbornePositionMessage(System.nanoTime(), icaoAddress, 1000.0, 0, 0.5, 0.5);


        assertEquals(1000.0, message.altitude());
        assertEquals(0, message.parity());
        assertEquals(0.5, message.x());
        assertEquals(0.5, message.y());
    }




    @Test
    void testConstructorWithInvalidArguments() {
        IcaoAddress icaoAddress = new IcaoAddress("123456");


        assertThrows(NullPointerException.class, () -> {
            new AirbornePositionMessage(System.nanoTime(), null, 1000.0, 0, 0.5, 0.5);
        });


        assertThrows(IllegalArgumentException.class, () -> {
            new AirbornePositionMessage(-1, icaoAddress, 1000.0, 0, 0.5, 0.5);
        });


        assertThrows(IllegalArgumentException.class, () -> {
            new AirbornePositionMessage(System.nanoTime(), icaoAddress, 1000.0, 2, 0.5, 0.5);
        });


        assertThrows(IllegalArgumentException.class, () -> {
            new AirbornePositionMessage(System.nanoTime(), icaoAddress, 1000.0, 0, -0.5, 0.5);
        });


        assertThrows(IllegalArgumentException.class, () -> {
            new AirbornePositionMessage(System.nanoTime(), icaoAddress, 1000.0, 0, 1.5, 0.5);
        });


        assertThrows(IllegalArgumentException.class, () -> {
            new AirbornePositionMessage(System.nanoTime(), icaoAddress, 1000.0, 0, 0.5, -0.5);
        });


        assertThrows(IllegalArgumentException.class, () -> {
            new AirbornePositionMessage(System.nanoTime(), icaoAddress, 1000.0, 0, 0.5, 1.5);
        });
    }
}
