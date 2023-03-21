package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AirbornePositionMessage(long timeStampNs, IcaoAddress icaoAddress, double altitude, int parity, double x, double y) implements Message {

    private static int z0 =60;
    private static int z1 = 59;

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
        long timeStampNs = rawMessage.timeStampNs();
        int lon_cpr = (int) payload & 0b11111111111111111;
        int lat_cpr = (int) ((payload>>17)& 0b11111111111111111);
        int format =  (int) ((payload>>34) & 1);
        int altitude =  (int) ((payload>>36) & 0b111111111111);
        double x = Math.scalb(lon_cpr, -17);
        double y = Math.scalb(lat_cpr, -17);
        if(((altitude>>4)&1) == 1){
            altitude = ((altitude & 0b111111100000)>>1) + (altitude & 0b000000001111);
            double convertedAltitude = Units.convert(altitude, Units.Length.FOOT, Units.Length.METER);
        }else{

        }
    }
}
