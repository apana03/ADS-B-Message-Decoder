package ch.epfl.javions;

import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;

public class AircraftState implements AircraftStateSetter {

    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {}

    @Override
    public void setCategory(int category) {}

    @Override
    public void setCallSign(CallSign callSign) {}

    @Override
    public void setPosition(GeoPos position) {}

    @Override
    public void setAltitude(double altitude) {}

    @Override
    public void setVelocity(double velocity) {}

    @Override
    public void setTrackOrHeading(double trackOrHeading) {}
}
