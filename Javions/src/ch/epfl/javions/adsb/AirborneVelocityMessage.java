package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

public record AirborneVelocityMessage(long timeStampNs,
                                      IcaoAddress icaoAddress,
                                      double speed, double trackOrHeading) implements Message {
    public AirborneVelocityMessage {
        if (icaoAddress == null) {
            throw new NullPointerException();
        }
        Preconditions.checkArgument(timeStampNs >= 0);
        Preconditions.checkArgument(speed >= 0);
        Preconditions.checkArgument(trackOrHeading >= 0);
    }

    public static AirborneVelocityMessage of(RawMessage rawMessage) {
        long payload = rawMessage.payload();
        int st = Bits.extractUInt(payload, 48, 3);
        int stBits = Bits.extractUInt(payload, 21, 22);
        double speed;
        double trackOrHeading;

        if (st < 1 || st > 4) {
            return null;
        }

        switch (st) {
            case 1, 2:

                int vns = Bits.extractUInt(stBits, 0, 10);
                int dns = Bits.extractUInt(stBits, 10, 1);
                int vew = Bits.extractUInt(stBits, 11, 10);
                int dew = Bits.extractUInt(stBits, 21, 1);

                if (vns == 0 || vew == 0) {
                    return null;
                }

                vns = (dns == 0) ? (--vns) : (--vns) * (-1);
                vew = (dew == 0) ? (--vew) : (--vew) * (-1);

                speed = Math.sqrt(Math.pow(vns, 2) + Math.pow(vew, 2));
                speed = (st == 1) ? Units.convertFrom(speed, Units.Speed.KNOT) :
                        Units.convertFrom(4 * speed, Units.Speed.KNOT);

                trackOrHeading = Math.atan2(vew, vns);
                trackOrHeading = (trackOrHeading < 0) ? trackOrHeading + Math.PI * 2 : trackOrHeading;

                return new AirborneVelocityMessage(rawMessage.timeStampNs(),
                        rawMessage.icaoAddress(),
                        speed,
                        trackOrHeading);

            case 3, 4:

                int as = Bits.extractUInt(stBits, 0, 10);
                int hdg = Bits.extractUInt(stBits, 11, 10);
                int sh = Bits.extractUInt(stBits, 21, 1);

                if (sh == 0 || as == 0) {
                    return null;
                }
                as--;
                speed = (st == 3) ? Units.convertFrom(as, Units.Speed.KNOT) :
                        Units.convertFrom(4 * as, Units.Speed.KNOT);

                // converting cap to TURN
                trackOrHeading = hdg / Math.scalb(1, 10);
                trackOrHeading = Units.convertFrom(trackOrHeading, Units.Angle.TURN);

                return new AirborneVelocityMessage(rawMessage.timeStampNs(),
                        rawMessage.icaoAddress(),
                        speed,
                        trackOrHeading);
        }
        return null;
    }
}
