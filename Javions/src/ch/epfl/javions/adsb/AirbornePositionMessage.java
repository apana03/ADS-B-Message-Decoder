package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AirbornePositionMessage(long timeStampNs, IcaoAddress icaoAddress, double altitude, int parity, double x, double y) implements Message {

    public AirbornePositionMessage{
        if(icaoAddress == null){
            throw new NullPointerException();
        }
        Preconditions.checkArgument(timeStampNs>=0);
        Preconditions.checkArgument(parity==0 || parity ==1);
        Preconditions.checkArgument(x>=0 && x<1);
        Preconditions.checkArgument(y>=0 && y<1);
    }

    public static AirbornePositionMessage of(RawMessage rawMessage){
        long payload = rawMessage.payload();
        int lon_cpr = (int) payload & 0b11111111111111111;
        int lat_cpr = (int) ((payload>>17)& 0b11111111111111111);
        int format =  (int) ((payload>>34) & 1);
        int altitude =  (int) ((payload>>36) & 0b111111111111);
        double x = Math.scalb(lon_cpr, -17);
        double y = Math.scalb(lat_cpr, -17);
        double convertedAltitude;
        if(((altitude>>4)&1) == 1){
            altitude = ((altitude & 0b111111100000)>>1) + (altitude & 0b000000001111);
            altitude = -1000 + altitude*25;
            convertedAltitude = Units.convert(altitude, Units.Length.FOOT, Units.Length.METER);
        }else{
            int untangledAlt = 0;
            int var = 0;
            for(int i = 0; i <= 4; i += 2){
                untangledAlt += ((altitude & (1<<i))>>i)<<(9+var);
                untangledAlt += ((altitude & (1<<i+1))>>i+1)<<(3+var);
                var++;
            }
            var = 0;
            for(int i = 6; i <= 10; i += 2){
                untangledAlt += ((altitude & (1<<i))>>i)<<(6+var);
                untangledAlt += ((altitude & (1<<i+1))>>i+1)<<(var);
                var++;
            }
            int grayLeastSigBits = untangledAlt & 0b111;
            int grayMostSigBits = (untangledAlt & 0b111111111000) >> 3;
            int leastSig = 0;
            int mostSig = 0;
            for(int i = 0; i<3; i++){
               leastSig = leastSig ^ (grayLeastSigBits>>i);
            }
            for(int i = 0; i<9; i++){
                mostSig = mostSig ^ (grayMostSigBits>>i);
            }
            if(leastSig == 0 || leastSig == 5 || leastSig == 6){
                return null;
            }
            if(leastSig == 7){
                leastSig = 5;
            }
            if(mostSig%2 == 1){
                leastSig = 6 - leastSig;
            }
            altitude = -1300 + (mostSig*500) + (leastSig*100);
            convertedAltitude = Units.convert(altitude, Units.Length.FOOT, Units.Length.METER);
        }
        return new AirbornePositionMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), convertedAltitude, format, x, y);
    }
}
