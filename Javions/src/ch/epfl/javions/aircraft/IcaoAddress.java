package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

/**
 * Defines the regular expression for Icao adress
 *
 * @author Andrei Pana 361249
 * @author David Fota 355816
 */

public record IcaoAddress(String string) {

    static Pattern er = Pattern.compile("[0-9A-F]{6}");

    /**
     * the compact constructor of the class
     *
     * @throws IllegalArgumentException if the string does not respect the regular expression
     */
    public IcaoAddress {
        if (!er.matcher(string).matches()) {
            throw new IllegalArgumentException();
        }
    }

}
