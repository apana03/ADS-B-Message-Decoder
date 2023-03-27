package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress, double speed, double trackOrHeading)implements Message {
    public AirborneVelocityMessage{
        if(icaoAddress == null){
            throw new NullPointerException();
        }
        Preconditions.checkArgument(timeStampNs >=0);
        Preconditions.checkArgument(speed>=0);
        Preconditions.checkArgument(trackOrHeading>=0);
    }

    AirborneVelocityMessage of(RawMessage rawMessage){
        long payload = rawMessage.payload();
        int st = Bits.extractUInt(payload,48,3);
        if ( st < 1 || st > 4 ){
            return null;
        }
        switch (st){
            case 1,2:
                int stBits = Bits.extractUInt(payload , 21 ,22 );
                int vns = Bits.extractUInt(stBits , 0 ,10 );
                int dns = Bits.extractUInt(payload , 10 ,1 );
                int vew = Bits.extractUInt(payload , 11 ,10 );
                int dew = Bits.extractUInt(payload , 21 ,1 );

                if(vns == 0 || vew == 0){
                    return null;
                }

                double speed = Math.sqrt( Math.pow ( vns - 1 , 2 ) + Math.scalb( vew - 1 , 2 ) );
                double trackOrHeading
        }
    }
}
