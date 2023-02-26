package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

/**
 *Defines the regular expression for the Aircraft Registration
 *
 * @author Andrei Pana
 * @author David Fota
 */

public record AircraftRegistration(String string) {

    static Pattern er = Pattern.compile("[A-Z0-9 .?/_+-]+");

    public AircraftRegistration {
        if(!er.matcher(string).matches()){
            throw new IllegalArgumentException();
        }
    }
}
