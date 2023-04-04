package ch.epfl.javions;

/**
 * Represents geographical positions
 *
 * @author Andrei Pana 361249
 * @author David Fota 355816
 */

public record GeoPos(int longitudeT32, int latitudeT32) {

    public GeoPos {
        if (!isValidLatitudeT32(latitudeT32))
            throw new IllegalArgumentException();
    }

    /**
     * returns true iff the passed value,
     * interpreted as a latitude expressed in t32, is valid
     *
     * @param latitudeT32 the given latitude
     */
    public static boolean isValidLatitudeT32(int latitudeT32) {
        if (latitudeT32 < -1073741824 || latitudeT32 > 1073741824) {
            return false;
        }
        return true;
    }

    /**
     * @return longitude in radians
     */
    public double longitude() {
        return Units.convertFrom(longitudeT32, Units.Angle.T32);
    }

    /**
     * @return latitude in radians
     */
    public double latitude() {
        return Units.convertFrom(latitudeT32, Units.Angle.T32);
    }

    @Override
    public String toString() {
        return "(" + Units.convert(longitudeT32, Units.Angle.T32, Units.Angle.DEGREE) + "°, " +
                Units.convert(latitudeT32, Units.Angle.T32, Units.Angle.DEGREE) + "°)";
    }
}
