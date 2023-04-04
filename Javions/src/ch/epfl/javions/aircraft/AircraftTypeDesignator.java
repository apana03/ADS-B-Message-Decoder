package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

/**
 * Defines the regular expression for the Aircraft Type Designator
 *
 * @author Andrei Pana 361249
 * @author David Fota 355816
 */

public record AircraftTypeDesignator(String string) {

    static Pattern er = Pattern.compile("[A-Z0-9]{2,4}");

    public AircraftTypeDesignator {
        if (!er.matcher(string).matches()) {
            if (!string.isEmpty()) {
                throw new IllegalArgumentException();
            }
        }
    }
}
