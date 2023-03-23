package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAdress,
                                            int category, CallSign callsign) implements Message
{
    public AircraftIdentificationMessage {
        Preconditions.checkArgument(timeStampNs >= 0);
        if( icaoAdress == null || callsign == null )
            throw new NullPointerException();
    }
    public static AircraftIdentificationMessage of(RawMessage rawMessage){
        long me = rawMessage.payload();
        StringBuilder callString = new StringBuilder();
        int count = 0;
        for( int i = 42; i >= 0; i -= 6 )
        {
            int a = Bits.extractUInt(me, i, 6);
            if( a >= 1 && a <= 26 )
                callString.append((char) (a + 'A' - 1));
            else if( (48 <= a && a <= 57) || a == 32)
                callString.append((char) a);
            else return null;
        }
        int category = ( 14 - rawMessage.typeCode() << 4) | Bits.extractUInt(me, 48, 3);
        return new AircraftIdentificationMessage( rawMessage.timeStampNs(), rawMessage.icaoAddress(),
                category, new CallSign(callString.toString().trim()));
    }
    @Override
    public long timeStampNs() {
        return timeStampNs;
    }
    @Override
    public IcaoAddress icaoAddress() {
        return icaoAdress;
    }

}
