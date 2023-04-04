package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

/**
 *represents an ADS-B in-flight positioning message
 *
 * @author Andrei Pana 361249
 * @author David Fota 355816
 */

public record AirbornePositionMessage(long timeStampNs, IcaoAddress icaoAddress,
                                      double altitude, int parity, double x, double y) implements Message {

    private static final int LAT_AND_LONG_MASK = 0b11111111111111111;
    private static final int ALTITUDE_MASK = 0b111111111111;
    private static final int ALTITUDE_MOST_SIG_7BITS = 0b111111100000;
    private static final int ALTITUDE_LEAST_SIG_4BITS = 0b000000001111;
    private static final int GRAY_LEAST_SIG_BITS_MASK = ((1<<4)-1);
    private static final int GRAY_MOST_SIG_BITS_MASK = 0b111111111000;


    /**
     * the constructor of the class
     * @throws NullPointerException if the IcaoAdress is null
     * @throws IllegalArgumentException if timeStamp is strictly less than 0,
     * or parity is different from 0 or 1, or x or y are not between 0 (included) and 1 (excluded)
     * */
    public AirbornePositionMessage{
        if(icaoAddress == null){
            throw new NullPointerException();
        }
        Preconditions.checkArgument(timeStampNs>=0);
        Preconditions.checkArgument(parity==0 || parity ==1);
        Preconditions.checkArgument(x>=0 && x<1);
        Preconditions.checkArgument(y>=0 && y<1);
    }

    /**
     * method that creates an AircraftPositionMessage from a RawMessage
     * @param rawMessage the raw message
     * @returns the in-flight positioning message corresponding to the given raw message, or null if the altitude it contains is invalid
     */
    public static AirbornePositionMessage of(RawMessage rawMessage){
        long payload = rawMessage.payload();
        int lon_cpr = (int) payload & LAT_AND_LONG_MASK;
        int lat_cpr = (int) ((payload>>17)& LAT_AND_LONG_MASK);
        int format =  (int) ((payload>>34) & 1);
        int altitude =  (int) ((payload>>36) & ALTITUDE_MASK);
        double x = Math.scalb(lon_cpr, -17);
        double y = Math.scalb(lat_cpr, -17);
        double convertedAltitude;
        if(((altitude>>4)&1) == 1){
            altitude = ((altitude & ALTITUDE_MOST_SIG_7BITS)>>1) + (altitude & ALTITUDE_LEAST_SIG_4BITS);
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
            int grayLeastSigBits = untangledAlt & GRAY_LEAST_SIG_BITS_MASK;
            int grayMostSigBits = (untangledAlt & GRAY_MOST_SIG_BITS_MASK) >> 3;
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

        return new AirbornePositionMessage(rawMessage.timeStampNs(),
                rawMessage.icaoAddress(),
                convertedAltitude,
                format,
                x,
                y);
    }
}
