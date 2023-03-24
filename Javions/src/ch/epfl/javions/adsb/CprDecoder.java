package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

public class CprDecoder
{
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent )
    {
        Preconditions.checkArgument(mostRecent == 0 || mostRecent == 1);
        int zLat0, zLat1;
        int zLat = (int) Math.rint(y0 * 59 - y1 * 60);
        if(zLat < 0){
            zLat0 = zLat + 60;
            zLat1 = zLat + 59;
        }else{
            zLat0 = zLat;
            zLat1 = zLat;
        }
        double lat0, lat1;
        lat0 = 1d/60 * (zLat0 + y0);
        if(lat0 >= 0.5)
            lat0--;
        lat1 = 1d/59 * (zLat1 + y1);
        if(lat1 >= 0.5)
            lat1--;
        double a = Math.acos(1 - ((1 - Math.cos(2 * Math.PI * 1d/60)) /
                (Math.cos(Units.convertFrom(lat0, Units.Angle.TURN)) * Math.cos(Units.convertFrom(lat0, Units.Angle.TURN)))));
        int ZLong0 = (int) Math.floor( 2*Math.PI / a);
        int ZLong1 = ZLong0 - 1;
        int zLong = (int) Math.rint(x0 * ZLong1 - x1 * ZLong0);
        double long0 = 1d/ZLong0 * (zLong + x0);
        if(long0 >= 0.5)
            long0--;
        double long1 = 1d/ZLong1 * (zLong + x1);
        if(long1 >= 0.5)
            long1--;
        if(mostRecent == 0)
        {
            if (isLatValid(lat0)) {
                return new GeoPos((int)Math.rint(Units.convert(long0, Units.Angle.TURN, Units.Angle.T32)),
                        (int)Math.rint(Units.convert(lat0, Units.Angle.TURN, Units.Angle.T32)));
            }else{ return null; }
        }
        else
        {
            if (isLatValid(lat1)) {
                return new GeoPos( (int) Math.rint(Units.convert(long1, Units.Angle.TURN, Units.Angle.T32)),
                        (int) Math.rint(Units.convert(lat1, Units.Angle.TURN, Units.Angle.T32) ));
            }else{ return null; }
        }
    }
    public static boolean isLatValid( double lat )
    {
        return GeoPos.isValidLatitudeT32((int)Math.rint(Units.convert(lat, Units.Angle.TURN, Units.Angle.T32)));
    }
}
