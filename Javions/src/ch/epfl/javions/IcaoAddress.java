package ch.epfl.javions;

import java.util.regex.Pattern;

/**
 *Defines the regular expression for Icao adress
 *
 * @author Andrei Pana
 * @author David Fota
 */

public record IcaoAddress(String string) {

    static Pattern er = Pattern.compile("[0-9A-F]{6}");

    public IcaoAddress{
        if(!er.matcher(string).matches()){
            throw new IllegalArgumentException();
        }
    }
}
