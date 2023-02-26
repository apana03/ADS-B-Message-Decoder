package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

/**
 *Defines the regular expression for the Aircraft description
 *
 * @author Andrei Pana
 * @author David Fota
 */

public record AircraftDescription(String string) {

    static Pattern er = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");

    public AircraftDescription {
        if(!er.matcher(string).matches()){
            if(!string.isEmpty()){
                throw new IllegalArgumentException();
            }
        }
    }
}
