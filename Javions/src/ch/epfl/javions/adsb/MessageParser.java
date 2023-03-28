package ch.epfl.javions.adsb;

public class MessageParser {


    public static Message parse(RawMessage rawMessage){
        
        int typecode = rawMessage.typeCode();
        if(typecode<=4 && typecode>=1){
            return AircraftIdentificationMessage.of(rawMessage);
        } else if ( (typecode>=9 && typecode<=18) || (typecode>=20 && typecode<=22)) {
            return AirbornePositionMessage.of(rawMessage);
        } else if (typecode == 19) {
            return AirborneVelocityMessage.of(rawMessage);
        }
        return null;
    }
}
