package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.HexFormat;

/**
 *Represents a Raw Message
 *
 * @author Andrei Pana 361249
 * @author David Fota 355816
 */
public record RawMessage(long timeStampNs, ByteString bytes) {

    public static final int LENGTH = 14;
    static Crc24 crc24 = new Crc24(Crc24.GENERATOR);
    public RawMessage{
        Preconditions.checkArgument(timeStampNs>=0);
        Preconditions.checkArgument(bytes.size() == LENGTH);
    }
    /**
     *Generates the message
     * @param timeStampNs
     * @param bytes
     * @return the generated Raw Message
     */
    public static RawMessage of(long timeStampNs, byte[] bytes){
        if(crc24.crc(bytes)!=0){
            return null;
        }else{
            return new RawMessage(timeStampNs, new ByteString(bytes));
        }
    }
    /**
     * Calculates the size of the message
     * @param byte0
     * @return size of the message
     */
    public static int size(byte byte0){
        byte0 =(byte) ((byte0 & 0b11111000)>>3);
        return (byte0 == 17) ? LENGTH : 0;
    }
    /**
     *Calculates the type code
     * @param payload
     * @return the extracted type code
     */
    public static int typeCode(long payload){
        return Bits.extractUInt(payload,51,5);
    }

    public int downLinkFormat(){
        int df = bytes.byteAt(0);
        df = (df & 0b11111000)>>3;
        return df;
    }
    /**
     *Defines the Icao Adress in the message
     * @return Icao Adress
     */
    public IcaoAddress icaoAddress(){
        int icao = (int) bytes.bytesInRange(1,4);
        return new IcaoAddress(Integer.toHexString(icao).toUpperCase());
    }
    /**
     *Defines the payload of the message
     * @return payload
     */
    public long payload(){
        return bytes.bytesInRange(4,11);
    }
    /**
     *Defines the typeCode of the message
     * @return typeCode
     */
    public int typeCode(){
        return typeCode(payload());
    }
}
