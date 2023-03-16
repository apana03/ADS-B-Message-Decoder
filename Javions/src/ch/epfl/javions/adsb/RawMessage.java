package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.HexFormat;

public record RawMessage(long timeStampNs, ByteString bytes) {

    public static final int LENGTH = 14;

    public RawMessage{
        Preconditions.checkArgument(timeStampNs>=0);
        Preconditions.checkArgument(bytes.size() == LENGTH);
    }

    static RawMessage of(long timeStampNs, byte[] bytes){
        Crc24 crc24 = new Crc24(Crc24.GENERATOR);
        byte[] messageCrc = new byte[]{bytes[11],bytes[12],bytes[13]};
        if(crc24.crc(messageCrc)!=0){
            return null;
        }else{
            return new RawMessage(timeStampNs, new ByteString(bytes));
        }
    }

    public static int size(byte byte0){
        byte0 =(byte) ((byte0 & 0b11111000)>>3);
        return (byte0 == 17) ? LENGTH : 0;
    }

    public static int typeCode(long payload){
        return Bits.extractUInt(payload,51,5);
    }

    public int downLinkFormat(){
        int df = bytes.byteAt(0);
        df = (df & 0b11111000)>>3;
        return df;
    }

    public IcaoAddress icaoAddress(){
        int icao = (int) bytes.bytesInRange(1,3);
        return new IcaoAddress(Integer.toHexString(icao));
    }

    public long payload(){
        return bytes.bytesInRange(4,10);
    }

    public int typeCode(){
        return typeCode(payload());
    }
}
