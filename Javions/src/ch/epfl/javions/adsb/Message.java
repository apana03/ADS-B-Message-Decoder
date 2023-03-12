package ch.epfl.javions.adsb;/*
 * Author: Andrei Pana
 * Date:
 */

import ch.epfl.javions.aircraft.IcaoAddress;

public interface Message {
    public long timeStampNs();
    public IcaoAddress icaoAddress();
}
