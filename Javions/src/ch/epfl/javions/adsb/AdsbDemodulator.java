package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.demodulation.PowerWindow;

import java.io.IOException;
import java.io.InputStream;

public class AdsbDemodulator
{
    PowerWindow window;
    int sumPCurrent, sumPPrevious;
    public AdsbDemodulator(InputStream samplesStream) throws IOException
    {
        window = new PowerWindow(samplesStream, 1200);
        sumPCurrent = window.get(0) + window.get(10) + window.get(35) + window.get(45);
        sumPPrevious = 0;
    }
    public RawMessage nextMessage() throws IOException
    {
        long horodatage;
        int sumPAfter, sumV;
        byte[] byteArray;
        while(window.isFull())
        {
            sumPAfter = window.get(1) + window.get(11) + window.get(36) + window.get(46);
            byteArray = new byte[14];
            if( sumPCurrent > sumPPrevious && sumPCurrent > sumPAfter )
            {
                sumV = window.get(5) + window.get(15) + window.get(20) + window.get(25)
                        + window.get(30) + window.get(40);
                if(sumPCurrent >= 2 * sumV)
                {
                    for (int i = 0; i < 112; i += 8)
                    {
                        for (int j = 0; j < 8; j++)
                            if (window.get(80 + 10 * (i + j)) >= window.get(85 + 10 * (i + j)))
                                byteArray[i >>> 3] |= (1 << (7 - j));
                    }
                    horodatage = window.position() * 100;
                    var message = RawMessage.of(horodatage, byteArray);
                    if (message != null && message.downLinkFormat() == 17)
                    {
                        window.advanceBy(1199);
                        sumPPrevious = window.get(0) + window.get(10) + window.get(35) + window.get(45);
                        sumPCurrent = window.get(1) + window.get(11) + window.get(36) + window.get(46);
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
}