package ch.epfl.javions.adsb;

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
        String callString = "";
        for( int i = 42; i >= 0; i -= 6 )
        {
            int a = (int) me & (0x111111 << i);
            if( a >= 1 && a <= 26 )
                callString += (char) a + 64;
            else if( (48 <= a && a <= 57) || a == 32 )
                callString += (char) a;
        }
        return new AircraftIdentificationMessage( rawMessage.timeStampNs(), rawMessage.icaoAddress(),
                (int) (me >> 48) & 0x111, new CallSign(callString));
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
