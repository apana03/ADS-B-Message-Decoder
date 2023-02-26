package ch.epfl.javions.adsb;

import java.util.regex.Pattern;

/**
 *Defines the regular expression for Call Sign
 *
 * @author Andrei Pana
 * @author David Fota
 */

public record CallSign(String string) {

    static Pattern er = Pattern.compile("[A-Z0-9 ]{0,8}");

    public CallSign {
        if(!er.matcher(string).matches()){
            if(!string.isEmpty()){
                throw new IllegalArgumentException();
            }
        }
    }
}
