package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * represents an ADS-B in-flight positioning message
 *
 * @author Andrei Pana 361249
 * @author David Fota 355816
 */

public record AirbornePositionMessage(long timeStampNs, IcaoAddress icaoAddress,
                                      double altitude, int parity, double x, double y) implements Message {

    private static final int ALTITUDE_MOST_SIG_7BITS = 0b111111100000;
    private static final int ALTITUDE_LEAST_SIG_4BITS = 0b000000001111;
    private static final int GRAY_LEAST_SIG_BITS_MASK = ((1 << 4) - 1);
    private static final int GRAY_MOST_SIG_BITS_MASK = 0b111111111000;
    private static final int ALT_START = 36, ALT_LENGTH = 12;
    private static final int FORMAT_START = 34, FORMAT_LENGTH = 1;
    private static final int LAT_CPR_START = 17, LAT_CPR_LENGTH = 17;
    private static final int LONG_CPR_START = 0, LONG_CPR_LENGTH = 17;


    /**
     * the compact constructor of the class
     *
     * @throws NullPointerException     if the IcaoAdress is null
     * @throws IllegalArgumentException if timeStamp is strictly less than 0,
     *                                  or parity is different from 0 or 1, or x or y are not between 0 (included) and 1 (excluded)
     */
    public AirbornePositionMessage {
        if (icaoAddress == null) {
            throw new NullPointerException();
        }
        Preconditions.checkArgument(timeStampNs >= 0);
        Preconditions.checkArgument(parity == 0 || parity == 1);
        Preconditions.checkArgument(x >= 0 && x < 1);
        Preconditions.checkArgument(y >= 0 && y < 1);
    }

    /**
     * method that creates an AircraftPositionMessage from a RawMessage
     *
     * @param rawMessage the raw message
     * @returns the in-flight positioning message corresponding to the given raw message, or null if the altitude it contains is invalid
     */
    public static AirbornePositionMessage of(RawMessage rawMessage) {
        long payload = rawMessage.payload();
        int lon_cpr = Bits.extractUInt(payload, LONG_CPR_START, LONG_CPR_LENGTH);
        int lat_cpr = Bits.extractUInt(payload, LAT_CPR_START, LAT_CPR_LENGTH);
        int format = Bits.extractUInt(payload, FORMAT_START, FORMAT_LENGTH);
        int altitude = Bits.extractUInt(payload, ALT_START, ALT_LENGTH);
        double x = Math.scalb(lon_cpr, -17);
        double y = Math.scalb(lat_cpr, -17);
        double convertedAltitude;

        if (((altitude >> 4) & 1) == 1) {
            convertedAltitude = convertAltitude(altitude);
        } else {

            int untangledAlt = untangleAltitude(altitude);

            int grayLeastSigBits = untangledAlt & GRAY_LEAST_SIG_BITS_MASK;
            int grayMostSigBits = (untangledAlt & GRAY_MOST_SIG_BITS_MASK) >> 3;

            int mostSig = decodeGrayMostSigBits(grayMostSigBits);
            int leastSig = decodeGrayLeastSigBits(grayLeastSigBits);

            if (leastSig == 0 || leastSig == 5 || leastSig == 6) return null;

            convertedAltitude = computeAltitude(leastSig, mostSig);


        }

        return new AirbornePositionMessage(rawMessage.timeStampNs(),
                rawMessage.icaoAddress(),
                convertedAltitude,
                format,
                x,
                y);
    }

    private static double convertAltitude(int altitude) {
        altitude = ((altitude & ALTITUDE_MOST_SIG_7BITS) >> 1) + (altitude & ALTITUDE_LEAST_SIG_4BITS);
        altitude = -1000 + altitude * 25;
        return Units.convert(altitude, Units.Length.FOOT, Units.Length.METER);
    }

    private static int untangleAltitude(int altitude) {
        int untangledAlt = 0;
        int var = 0;
        for (int i = 0; i <= 4; i += 2) {
            untangledAlt += ((altitude & (1 << i)) >> i) << (9 + var);
            untangledAlt += ((altitude & (1 << i + 1)) >> i + 1) << (3 + var);
            var++;
        }
        var = 0;
        for (int i = 6; i <= 10; i += 2) {
            untangledAlt += ((altitude & (1 << i)) >> i) << (6 + var);
            untangledAlt += ((altitude & (1 << i + 1)) >> i + 1) << (var);
            var++;
        }
        return untangledAlt;
    }

    private static int decodeGrayMostSigBits(int grayMostSigBits) {
        int mostSig = 0;
        for (int i = 0; i < 9; i++) mostSig = mostSig ^ (grayMostSigBits >> i);
        return mostSig;
    }

    private static int decodeGrayLeastSigBits(int grayLeastSigBits) {
        int leastSig = 0;
        for (int i = 0; i < 3; i++) leastSig = leastSig ^ (grayLeastSigBits >> i);
        return leastSig;
    }

    private static double computeAltitude(int leastSig, int mostSig) {
        if (leastSig == 7) leastSig = 5;
        if (mostSig % 2 != 0) leastSig = 6 - leastSig;

        int altitude = -1300 + (mostSig * 500) + (leastSig * 100);
        return Units.convert(altitude, Units.Length.FOOT, Units.Length.METER);
    }

}
