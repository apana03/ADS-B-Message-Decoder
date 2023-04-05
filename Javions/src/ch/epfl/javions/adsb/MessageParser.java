package ch.epfl.javions.adsb;

/**
 * Transforms the raw ADS-B messages into messages of one of the three types described below:
 * — AircraftIdentificationMessage, AirbornePositionMessage or AirborneVelocityMessage
 *
 * @author Andrei Pana 361249
 * @author David Fota 355816
 */

public class MessageParser {

    /**
     *Transforms a raw message into the corresponding message
     *
     * @param rawMessage
     *          the raw message
     * @return the instance of AircraftIdentificationMessage, AirbornePositionMessage or AirborneVelocityMessage
     *          corresponding to the given raw message, or null if the type code of the latter does not correspond to
     *          any of these three types of messages, or if it is invalid
     */
    public static Message parse(RawMessage rawMessage) {
        int typecode = rawMessage.typeCode();
        if (typecode <= 4 && typecode >= 1) {
            return AircraftIdentificationMessage.of(rawMessage);
        } else if ((typecode >= 9 && typecode <= 18) || (typecode >= 20 && typecode <= 22)) {
            return AirbornePositionMessage.of(rawMessage);
        } else if (typecode == 19) {
            return AirborneVelocityMessage.of(rawMessage);
        }
        return null;
    }
}
