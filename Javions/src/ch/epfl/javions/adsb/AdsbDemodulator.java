package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.demodulation.PowerWindow;

import java.io.IOException;
import java.io.InputStream;

public class AdsbDemodulator
{
    PowerWindow window, window1;
    public AdsbDemodulator(InputStream samplesStream) throws IOException
    {
        window = new PowerWindow(samplesStream, 1200);
    }
    RawMessage nextMessage() throws IOException
    {
        long horodatage = 0;
        byte[] byteArray = new byte[12];
        window1 = window;
        int[] sumV = new int[1200], sumP = new int[1200];
        for(int i = 0; i < 1200; i++)
        {
            sumP[i] = window1.get(0) + window1.get(10) + window1.get(35) + window1.get(45);
            sumV[i] = window1.get(5) + window1.get(15) + window1.get(20) + window1.get(25)
                    + window1.get(30) + window1.get(40);
            window1.advanceBy(1);
        }
        for(int i = 1; i < 1200; i++)
        {
            if( sumP[i] > sumP[i-1] && sumP[i] > sumP[i+1] && sumP[i] >= sumV[i] )
            {
                window.advanceBy(i);
                horodatage = i/10;
            }
        }
        for( int i = 0; i < 112; i += 8 )
        {
            for( int j = 0; j < 8; j++ )
                if( window.get(80 + 10 * i) >= window.get(85 + 10 * i) )
                    byteArray[i / 8] |= (1 << j);
        }
        ByteString message = new ByteString(byteArray);
        if(horodatage != 0)
            return new RawMessage(horodatage, message);
        else return null;
    }
}
