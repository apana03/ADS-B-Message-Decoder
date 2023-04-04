package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.HexFormat;

/**
 * Represents a Raw Message
 *
 * @author Andrei Pana 361249
 * @author David Fota 355816
 */
public record RawMessage(long timeStampNs, ByteString bytes) {

    public static final int LENGTH = 14;
    private static final int MOST_SIG_5BITS_MASK = 0b11111000;

    private static final int ICAO_START = 1, ICAO_END = 4;
    private static final int ME_START = 4, ME_END = 11;
    private static final int TYPECODE_START = 51, TYPECODE_LENGTH = 5;
    private static Crc24 crc24 = new Crc24(Crc24.GENERATOR);

    private static HexFormat hf = HexFormat.of().withUpperCase();

    public RawMessage {
        Preconditions.checkArgument(timeStampNs >= 0);
        Preconditions.checkArgument(bytes.size() == LENGTH);
    }

    /**
     * Generates the message
     *
     * @param timeStampNs
     * @param bytes
     * @return the generated Raw Message
     */
    public static RawMessage of(long timeStampNs, byte[] bytes) {
        if (crc24.crc(bytes) != 0) {
            return null;
        } else {
            return new RawMessage(timeStampNs, new ByteString(bytes));
        }
    }

    /**
     * Calculates the size of the message
     *
     * @param byte0
     * @return size of the message
     */
    public static int size(byte byte0) {
        byte0 = (byte) ((byte0 & MOST_SIG_5BITS_MASK) >> 3);
        return (byte0 == 17) ? LENGTH : 0;
    }

    /**
     * Calculates the type code
     *
     * @param payload
     * @return the extracted type code
     */
    public static int typeCode(long payload) {
        return Bits.extractUInt(payload, TYPECODE_START, TYPECODE_LENGTH);
    }

    public int downLinkFormat() {
        int df = bytes.byteAt(0);
        df = (df & MOST_SIG_5BITS_MASK) >> 3;
        return df;
    }

    /**
     * Defines the Icao Adress in the message
     *
     * @return Icao Adress
     */
    public IcaoAddress icaoAddress() {
        long icao = bytes.bytesInRange(ICAO_START, ICAO_END);
        return new IcaoAddress(hf.toHexDigits(icao, 6));
    }

    /**
     * Defines the payload of the message
     *
     * @return payload
     */
    public long payload() {
        return bytes.bytesInRange(ME_START, ME_END);
    }

    /**
     * Defines the typeCode of the message
     *
     * @return typeCode
     */
    public int typeCode() {
        return typeCode(payload());
    }
}
