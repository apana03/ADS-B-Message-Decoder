package ch.epfl.javions;

/**
 *Definition of various units
 *
 * @author Andrei Pana
 * @author David Fota 355816
 */
public final class Units {
    private Units(){}

    /**
     * SI units
     */
    public static final double CENTI = 1e-2;
    public static final double KILO = 1e3;

    /**
     * Angle related units
     */
    public static class Angle{
        private Angle(){}
        public static final double RADIAN = 1;
        public static final double TURN = 2 * Math.PI * RADIAN;
        public static final double DEGREE = TURN/360;
        public static final double T32 = TURN/(1L<<32);

    }

    /**
     * Time related units
     */
    public static class Time{
        private Time(){}
        public static final double SECOND = 1;
        public static final double MINUTE = 60 * SECOND;
        public static final double HOUR = 60 * MINUTE;
    }
    /**
     * Length related units
     */
    public static class Length{
        private Length(){}
        public static final double METER = 1;
        public static final double CENTIMETER = CENTI * METER;
        public static final double KILOMETER =KILO * METER;
        public static final double INCH = 2.54*CENTIMETER;
        public static final double FOOT = 12 * INCH;
        public static final double NAUTICAL_MILE  = 1852*METER;
    }
    /**
     * Speed related units
     */
     public static class Speed{
        private Speed(){}
         public static final double METERS_PER_SECOND = Length.METER/ Time.SECOND;
        public static final double KNOT = Length.NAUTICAL_MILE/ Time.HOUR;
        public static final double KILOMETER_PER_HOUR = Length.KILOMETER / Time.HOUR;
    }

    /**
     * Converts given value, from fromUnit to toUnit
     * @param value
     *            the value
     * @param fromUnit
     *            departure unit
     * @param toUnit
     *          end unit
     */
    public static double convert(double value, double fromUnit, double toUnit)
    {
        double convertedValue = value * (fromUnit / toUnit);
        return convertedValue;
    }

    /**
     * Converts given value, from fromUnit to the base unit
     * @param value
     *            the value
     * @param fromUnit
     *            departure unit
     */
    public static double convertFrom(double value, double fromUnit){
        double convertedValue = value * fromUnit;
        return convertedValue;
    }

    /**
     * Converts given value, from base unit to the fromUnit
     * @param value
     *            the value
     * @param toUnit
     *            end unit
     */
    public static double convertTo(double value, double toUnit)
    {
        double convertedValue = value *  (1.00 / toUnit);
        return convertedValue;
    }
}
