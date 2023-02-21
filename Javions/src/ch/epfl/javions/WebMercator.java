package ch.epfl.javions;

public class WebMercator {
    private WebMercator(){}
    public static double x(int zoomLevel, double longitude){
        return ( 2 ^ ( 8 + zoomLevel ) ) * ( Units.convertTo( longitude, Units.Angle.TURN ) + 0.5 );
    }
    public static double y(int zoomLevel, double latitude){
        return( 2 ^ ( 8 + zoomLevel ) ) * ( ( - ( ( Math2.asinh( Math.tan( latitude ) ) ) / ( 2 * Math.PI ) ) ) + 0.5);
    }
}
