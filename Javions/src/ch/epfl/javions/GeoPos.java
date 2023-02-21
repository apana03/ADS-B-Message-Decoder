package ch.epfl.javions;

public record GeoPos(int longitudeT32, int latitudeT32)
{
    public GeoPos{
        if(!isValidLatitudeT32(latitudeT32))
            throw new IllegalArgumentException();
    }
    public static boolean isValidLatitudeT32(int latitudeT32)
    {
        if( latitudeT32 < -(2^30) || latitudeT32 > (2^30) )
        {
            return false;
        }
        return true;
    }
    double longitude(){
        return Units.convertFrom(longitudeT32, Units.Angle.T32);
    }
    double latitude(){
        return Units.convertFrom(latitudeT32, Units.Angle.T32);
    }
    public String toString(){
        return "(" + Units.convert(longitudeT32, Units.Angle.T32, Units.Angle.DEGREE) + "°, " +
                Units.convert(latitudeT32, Units.Angle.T32, Units.Angle.DEGREE) + "°)";
    }
}
