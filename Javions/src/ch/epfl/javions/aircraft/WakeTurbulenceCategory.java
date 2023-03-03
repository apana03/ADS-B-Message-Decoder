package ch.epfl.javions.aircraft;

/**
 *Represents the wake turbulence category of an aircraft
 *
 * @author Andrei Pana 361249
 * @author David Fota 355816
 */

public enum WakeTurbulenceCategory {
    LIGHT,
    MEDIUM,
    HEAVY,
    UNKNOWN;

    /**
     * @returns the wake turbulence category corresponding to the given string
     * @param s the string
     */
    public static WakeTurbulenceCategory of(String s){
        switch (s){
            case "L" -> {
                return LIGHT;
            }
            case "M" -> {
                return MEDIUM;
            }
            case "H" -> {
                return HEAVY;
            }

        }
        return UNKNOWN;
    }
}
