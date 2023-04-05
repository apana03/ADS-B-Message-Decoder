package ch.epfl.javions.demodulation;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.demodulation.PowerWindow;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Represents an ADSB Demodulator
 *
 * @author Andrei Pana 361249
 * @author David Fota 355816
 */
public class AdsbDemodulator {
    PowerWindow window;
    private int sumPCurrent, sumPPrevious, sumPAfter, sumV;
    byte[] byteArray = new byte[14];
    private final int NANO_PER_POS = 100;
    private final int WINDOW_SIZE = 1200;
    private final int PREAMBULE_SIZE = 80;
    private final int[] messageValleyIndexes = {5, 15, 20, 25, 30, 40};
    private final int[] messageAfterIndexes = {1, 11, 36, 46};
    private final int[] messageCurrentIndexes = {0, 10, 35, 45};

    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        window = new PowerWindow(samplesStream, WINDOW_SIZE);
        sumPCurrent = messageCurrent();
        sumPPrevious = 0;
    }

    /**
     * Calculates and demodulates the next message that is found
     *
     * @return message, which represents the calculated Raw Message
     */
    public RawMessage nextMessage() throws IOException {
        long horodatage;
        while (window.isFull()) {
            sumPAfter = messageAfter();
            if (isEligibleForSumValleyCalculation()) {
                sumV = messageValley();
                if (isValid()) {
                    Arrays.fill(byteArray, (byte) 0);
                    for (int i = 0; i < 112; i += Long.BYTES) {
                        for (int j = 0; j < Long.BYTES; j++)
                            if (window.get(PREAMBULE_SIZE + 10 * (i + j))
                                    >= window.get(PREAMBULE_SIZE + 5 + 10 * (i + j)))
                                byteArray[i >>> 3] |= (1 << (7 - j));
                    }
                    horodatage = window.position() * NANO_PER_POS;
                    var message = RawMessage.of(horodatage, byteArray);
                    if (message != null && message.downLinkFormat() == 17) {
                        window.advanceBy(WINDOW_SIZE - 1);
                        sumPPrevious = messageCurrent();
                        sumPCurrent = messageAfter();
                        window.advance();
                        return message;
                    }
                }
            }
            sumPPrevious = sumPCurrent;
            sumPCurrent = sumPAfter;
            window.advance();
        }
        return null;
    }

    private int messageValley() {
        int s = 0;
        for( int i : messageValleyIndexes)
            s += window.get(i);
        return s;
    }

    private int messageAfter() {
        int s = 0;
        for( int i : messageAfterIndexes)
            s += window.get(i);
        return s;
    }

    private int messageCurrent() {
        int s = 0;
        for( int i : messageCurrentIndexes)
            s += window.get(i);
        return s;
    }
    private boolean isEligibleForSumValleyCalculation(){
        return sumPCurrent > sumPPrevious && sumPCurrent > sumPAfter;
    }
    private boolean isValid(){
        return sumPCurrent >= 2 * sumV;
    }
}