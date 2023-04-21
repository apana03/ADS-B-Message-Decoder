package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;

/**
 * Represents an "aircraft state accumulator",
 * i.e. an object accumulating ADS-B messages originating from a single aircraft
 * in order to determine its state over time
 *
 * @author Andrei Pana 361249
 * @author David Fota 355816
 */

public class AircraftStateAccumulator<T extends AircraftStateSetter> {
    final T stateSetter;
    private final static long NANO_IN_NORMAL= (long) Math.pow(10, 9);
    private static final int EVEN = 0, ODD = 1;
    AirbornePositionMessage lastEvenMessage, lastOddMessage;

    /**
     * Public constructor
     * @param stateSetter
     *
     * @return an aircraft state accumulator associated with the given modifiable state
     * @throws NullPointerException if it is null.
     */
    public AircraftStateAccumulator(T stateSetter) {
        this.stateSetter = stateSetter;
        if (stateSetter == null)
            throw new NullPointerException();
    }

    /**
     * @return the modifiable state of the aircraft passed to its constructor
     */

    public T stateSetter() {
        return stateSetter;
    }

    /**
     * updates the editable status according to the given message
     * @param message
     *      the message
     */

    public void update(Message message) {
        GeoPos position;
        stateSetter.setLastMessageTimeStampNs(message.timeStampNs());
        switch (message) {
            case AircraftIdentificationMessage aim -> {
                stateSetter.setCategory(aim.category());
                stateSetter.setCallSign(aim.callSign());
            }
            case AirbornePositionMessage apm -> {
                stateSetter.setAltitude(apm.altitude());
                if (apm.parity() == EVEN) {
                    if (lastOddMessage != null && apm.timeStampNs() - lastOddMessage.timeStampNs() <= 10 * NANO_IN_NORMAL) {
                        position = CprDecoder.decodePosition(apm.x(), apm.y(),
                                lastOddMessage.x(), lastOddMessage.y(), EVEN);
                        if (position != null)
                            stateSetter.setPosition(position);
                    }
                    lastEvenMessage = apm;
                } else {
                    if (lastEvenMessage != null && apm.timeStampNs() - lastEvenMessage.timeStampNs() <= 10 * NANO_IN_NORMAL) {
                        position = CprDecoder.decodePosition(lastEvenMessage.x(),
                                lastEvenMessage.y(), apm.x(), apm.y(), ODD);
                        if (position != null)
                            stateSetter.setPosition(position);
                    }
                    lastOddMessage = apm;
                }
            }
            case AirborneVelocityMessage avm -> {
                stateSetter.setVelocity(avm.speed());
                stateSetter.setTrackOrHeading(avm.trackOrHeading());
            }
            default -> throw new Error();
        }
    }
}
