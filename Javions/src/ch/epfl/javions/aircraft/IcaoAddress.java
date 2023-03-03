package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

/**
 *Defines the regular expression for Icao adress
 *
 * @author Andrei Pana
 * @author David Fota 355816
 */

public record IcaoAddress(String string) {

    static Pattern er = Pattern.compile("[0-9A-F]{6}");

    public IcaoAddress{
        if(!er.matcher(string).matches()){
            throw new IllegalArgumentException();
        }
    }
}
