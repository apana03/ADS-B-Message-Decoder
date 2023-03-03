package ch.epfl.javions;

/**
 *Offers static methods to make various calculations
 *
 * @author Andrei Pana
 * @author David Fota 355816
 */
public final class Math2 {
    private Math2(){}

    /**
     * limits the value of v to the range from min to max,
     * returning min if v is less than min, max if v is greater than max,
     * and v otherwise
     * @param v
     *            the value
     * @param max
     *            maximum
     * @param min
     *            minimum
     * @throws IllegalArgumentException
     */
    public static int clamp(int min, int v, int max){
        if( min > max )
            throw new IllegalArgumentException();
        if( v > max )
            return max;
        else if( v < min )
            return min;
        else return v;
    }

    /**
     * returns the reciprocal hyperbolic sine of its argument x
     * @param x
     *      the argument
     */
    public static double asinh(double x)
    {
        return Math.log( x + Math.sqrt( 1 + ( x * x ) ) );
    }
}
