package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

public class AircraftStateAccumulator<T extends AircraftStateSetter> {
    final T stateSetter;
    final static long NANO = (long) Math.pow(10, 9);
    AirbornePositionMessage lastEvenMessage, lastOddMessage;

    public AircraftStateAccumulator(T stateSetter) {
        this.stateSetter = stateSetter;
        if (stateSetter == null)
            throw new NullPointerException();
    }

    public T stateSetter() {
        return stateSetter;
    }

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
                if (apm.parity() == 0) {
                    if (lastOddMessage != null && apm.timeStampNs() - lastOddMessage.timeStampNs() <= 10 * NANO) {
                        position = CprDecoder.decodePosition(apm.x(), apm.y(),
                                lastOddMessage.x(), lastOddMessage.y(), 0);
                        if (position != null)
                            stateSetter.setPosition(position);
                    }
                    lastEvenMessage = apm;
                } else {
                    if (lastEvenMessage != null && apm.timeStampNs() - lastEvenMessage.timeStampNs() <= 10 * NANO) {
                        position = CprDecoder.decodePosition(lastEvenMessage.x(),
                                lastEvenMessage.y(), apm.x(), apm.y(), 1);
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
