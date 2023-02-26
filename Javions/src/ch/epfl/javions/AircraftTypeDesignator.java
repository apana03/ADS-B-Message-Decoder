package ch.epfl.javions;

import java.util.regex.Pattern;

/**
 *Defines the regular expression for the Aircraft Type Designator
 *
 * @author Andrei Pana
 * @author David Fota
 */

public record AircraftTypeDesignator(String string) {

    static Pattern er = Pattern.compile("[A-Z0-9]{2,4}");

    public AircraftTypeDesignator {
        if(!er.matcher(string).matches()){
            throw new IllegalArgumentException();
        }
    }
}
