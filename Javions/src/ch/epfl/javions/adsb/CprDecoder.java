package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

/**
 * represents a decoder of the CPR position
 *
 * @author Andrei Pana 361249
 * @author David Fota 355816
 */


public class CprDecoder {

    /**
     * which returns the geographical position corresponding to the given normalized local positions —
     *
     * @param x0 and y0 being the local longitude and latitude of an even message,
     * @param x1 and y1 those of an odd message — knowing that the most recent positions are those of
     * mostRecent index (0 or 1);
     * @returns null if the latitude of the decoded position is invalid (i.e. between ±90°)
     * or if the position cannot be determined due to a change in latitude band or the decoded positions
     * @throws IllegalArgumentException if mostRecent is not 0 or 1.
     */
    private static final double DELTA0 = 1d / 60;
    private static final double DELTA1 = 1d / 59;

    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent) {
        Preconditions.checkArgument(mostRecent == 0 || mostRecent == 1);
        int zLat0, zLat1, zLong0, zLong1;
        int zLat = (int) Math.rint(y0 * 59 - y1 * 60);
        if (zLat < 0) {
            zLat0 = zLat + 60;
            zLat1 = zLat + 59;
        } else {
            zLat0 = zLat;
            zLat1 = zLat;
        }
        double lat0, lat1;
        lat0 = DELTA0 * (zLat0 + y0);
        lat0 = checkLongOrLat(lat0);
        lat1 = DELTA1 * (zLat1 + y1);
        lat1 = checkLongOrLat(lat1);
        double a = computeA(lat0);
        if (checkIfBandChanged(a, computeA(lat1)) && !Double.isNaN(a))
            return null;
        if (Double.isNaN(a)) {
            zLong0 = 1;
            zLong1 = 1;
        } else {
            zLong0 = (int) Math.floor(2 * Math.PI / a);
            zLong1 = zLong0 - 1;
        }
        int zLong = (int) Math.rint(x0 * zLong1 - x1 * zLong0);
        double long0 = 1d / zLong0 * (zLong + x0);
        long0 = checkLongOrLat(long0);
        double long1 = 1d / zLong1 * (zLong + x1);
        long1 = checkLongOrLat(long1);
        if (mostRecent == 0) {
            if (isLatValid(lat0)) {
                return createGeoPos(long0, lat0);
            } else {
                return null;
            }
        } else {
            if (isLatValid(lat1)) {
                return createGeoPos(long1, lat1);
            } else {
                return null;
            }
        }
    }

    private static boolean isLatValid(double lat) {
        return GeoPos.isValidLatitudeT32((int) Math.rint(Units.convert(lat, Units.Angle.TURN, Units.Angle.T32)));
    }

    private static double checkLongOrLat(double a) {
        if (a >= 0.5)
            a--;
        return a;
    }

    private static GeoPos createGeoPos(double a, double b) {
        return new GeoPos((int) Math.rint(Units.convert(a, Units.Angle.TURN, Units.Angle.T32)),
                (int) Math.rint(Units.convert(b, Units.Angle.TURN, Units.Angle.T32)));
    }

    private static double computeA(double lat0) {
        return Math.acos(1 - ((1 - Math.cos(2 * Math.PI * DELTA0)) /
                (Math.cos(Units.convertFrom(lat0, Units.Angle.TURN)) *
                        Math.cos(Units.convertFrom(lat0, Units.Angle.TURN)))));
    }

    private static boolean checkIfBandChanged(double a1, double a2) {
        if (Math.floor((2 * Math.PI) / a1) != Math.floor((2 * Math.PI) / a2))
            return true;
        else return false;
    }
}
