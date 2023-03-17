package ch.epfl.javions.adsb;/*
 * Author: Andrei Pana
 * Date:
 */

import ch.epfl.javions.GeoPos;
/**
 *Defines the regular expression for AircraftStateSetter
 *
 * @author Andrei Pana 361249
 * @author David Fota 355816
 */
public interface AircraftStateSetter {
    void setLastMessageTimeStampNs(long timeStampNs);
    void setCategory(int category);
    void setCallSign(CallSign callSign);
    void setPosition(GeoPos position);
    void setAltitude(double altitude);
    void setVelocity(double velocity);
    void setTrackOrHeading(double trackOrHeading);
}
