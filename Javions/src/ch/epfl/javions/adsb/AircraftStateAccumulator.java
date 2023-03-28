package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

public class AircraftStateAccumulator<T extends AircraftStateSetter>
{
    final T stateSetter;
    final static long NANO = (long) Math.pow(10, 9);
    AirbornePositionMessage lastEvenMessage, lastOddMessage;
    public AircraftStateAccumulator(T stateSetter)
    {
        this.stateSetter = stateSetter;
        if(stateSetter == null)
            throw new NullPointerException();
    }
    public T stateSetter(){return stateSetter;}
    void update(Message message)
    {
        stateSetter.setLastMessageTimeStampNs(message.timeStampNs());
        switch(message){
            case AircraftIdentificationMessage aim -> {
                stateSetter.setCategory(aim.category());
                stateSetter.setCallSign(aim.callSign());
            }
            case AirbornePositionMessage aim -> {
                stateSetter.setAltitude(aim.altitude());
                switch(aim.parity())
                {
                    case 0 :
                    {
                        if( aim.timeStampNs() - lastOddMessage.timeStampNs() <= 10 * NANO)
                            stateSetter.setPosition(CprDecoder.decodePosition(aim.x(), aim.y(),
                                    lastOddMessage.x(), lastOddMessage.y(), 0));
                        lastEvenMessage = aim;
                    }
                    case 1 :
                    {
                        if( aim.timeStampNs() - lastEvenMessage.timeStampNs() <= 10 * NANO)
                            stateSetter.setPosition(CprDecoder.decodePosition(lastEvenMessage.x(),
                                    lastEvenMessage.y(), aim.x(), aim.y(), 1));
                        lastOddMessage = aim;
                    }
                }
            }
            case AirborneVelocityMessage aim -> {
                stateSetter.setVelocity(aim.speed());
                stateSetter.setTrackOrHeading(aim.trackOrHeading());
            }
            default -> System.out.println("Unexpected value!");
        }
    }
}
