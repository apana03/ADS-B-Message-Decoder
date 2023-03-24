package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

public class CprDecoder
{
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent )
    {
        Preconditions.checkArgument(mostRecent == 0 || mostRecent == 1);
        int zLat = (int) Math.rint(y0 * 59 - y1 * 60);
        double lat0, lat1;
        lat0 = 1d/60 * (zLat + y0);
        lat1 = 1d/60 * (zLat + y1);
        double a = Math.acos(1 - ((1 - Math.cos(2 * Math.PI * 1d/60)) /
                (Math.cos(Units.convertFrom(lat0, Units.Angle.TURN)) * Math.cos(Units.convertFrom(lat0, Units.Angle.TURN)))));
        int ZLong0 = (int) Math.floor( 2*Math.PI / a);
        int ZLong1 = ZLong0 - 1;
        int zLong = (int) Math.rint(x0 * ZLong1 - x1 * ZLong0);
        double long0 = 1d/ZLong0 * (zLong + x0);
        double long1 = 1d/ZLong1 * (zLong + x1);
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
                return new GeoPos( (int) Units.convert(long1, Units.Angle.TURN, Units.Angle.T32),
                        (int) Units.convert(lat1, Units.Angle.TURN, Units.Angle.T32) );
            }else{ return null; }
        }
    }
    public static boolean isLatValid( double lat )
    {
        return Units.convert(lat, Units.Angle.TURN, Units.Angle.DEGREE) >= -90 &&
                Units.convert(lat, Units.Angle.TURN, Units.Angle.DEGREE) <= 90;
    }
}