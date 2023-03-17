package ch.epfl.javions.adsb;/*
 * Author: Andrei Pana
 * Date:
 */

import ch.epfl.javions.aircraft.IcaoAddress;
/**
 *Defines the regular expression for Message
 *
 * @author Andrei Pana 361249
 * @author David Fota 355816
 */
public interface Message {
    public long timeStampNs();
    public IcaoAddress icaoAddress();
}
