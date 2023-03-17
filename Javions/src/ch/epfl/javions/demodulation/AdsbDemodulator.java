package ch.epfl.javions.demodulation;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.demodulation.PowerWindow;

import java.io.IOException;
import java.io.InputStream;
/**
 *Represents an ADSB Demodulator
 *
 * @author Andrei Pana 361249
 * @author David Fota 355816
 */
public class AdsbDemodulator
{
    PowerWindow window;
    int sumPCurrent, sumPPrevious;
    final int WINDOW_SIZE = 1200;
    public AdsbDemodulator(InputStream samplesStream) throws IOException
    {
        window = new PowerWindow(samplesStream, WINDOW_SIZE);
        sumPCurrent = messageCurrent();
        sumPPrevious = 0;
    }
    /**
     *Calculates and demodulates the next message that is found
     * @return message, which represents the calculated Raw Message
     */
    public RawMessage nextMessage() throws IOException
    {
        long horodatage;
        int sumPAfter, sumV;
        byte[] byteArray = new byte[14];
        while(window.isFull())
        {
            sumPAfter = messageAfter();
            if( sumPCurrent > sumPPrevious && sumPCurrent > sumPAfter )
            {
                sumV = messageValley();
                if(sumPCurrent >= 2 * sumV)
                {
                    for (int i = 0; i < 112; i += 8)
                    {
                        byte b = 0;
                        for (int j = 0; j < 8; j++)
                            if (window.get(80 + 10 * (i + j)) >= window.get(85 + 10 * (i + j)))
                                b |= (1 << (7 - j));
                        byteArray[i >>> 3] = b;
                    }
                    horodatage = window.position() * 100;
                    var message = RawMessage.of(horodatage, byteArray);
                    if (message != null && message.downLinkFormat() == 17)
                    {
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
    private int messageValley(){return window.get(5) + window.get(15) + window.get(20) + window.get(25) + window.get(30)
            + window.get(40);}
    private int messageAfter(){return window.get(1) + window.get(11) + window.get(36) + window.get(46);}
    private int messageCurrent(){
        return window.get(0) + window.get(10) + window.get(35) + window.get(45);
    }
}