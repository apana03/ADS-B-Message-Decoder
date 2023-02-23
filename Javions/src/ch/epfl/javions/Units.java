package ch.epfl.javions;

public final class Units {
    private Units(){}

    public static final double CENTI = 1e-2;
    public static final double KILO = 1e3;

    public static class Angle{
        private Angle(){}
        public static final double RADIAN = 1;
        public static final double TURN = 2 * Math.PI * RADIAN;
        public static final double DEGREE = TURN/360;
        public static final double T32 = TURN/(1L<<32);

    }

    public static class Time{
        private Time(){}
        public static final double SECOND = 1;
        public static final double MINUTE = 60 * SECOND;
        public static final double HOUR = 60 * MINUTE;
    }
    public static class Length{
        private Length(){}
        public static final double METER = 1;
        public static final double CENTIMETER = CENTI * METER;
        public static final double KILOMETER =KILO * METER;
        public static final double INCH = 2.54*CENTIMETER;
        public static final double FOOT = 12 * INCH;
        public static final double NAUTICAL_MILE  = 1852*METER;
    }
     public static class Speed{
        private Speed(){}
         public static final double METERS_PER_SECOND = Length.METER/ Time.SECOND;
        public static final double KNOT = Length.NAUTICAL_MILE/ Time.HOUR;
        public static final double KILOMETER_PER_HOUR = Length.KILOMETER / Time.HOUR;
    }

    public static double convert(double value, double fromUnit, double toUnit)
    {
        double convertedValue = value * (toUnit / fromUnit);
        return convertedValue;
    }
    public static double convertFrom(double value, double fromUnit){
        double convertedValue = value * ( 1.00 / fromUnit);
        return convertedValue;
    }
    public static double convertTo(double value, double toUnit)
    {
        double convertedValue = value * toUnit;
        return convertedValue;
    }
}