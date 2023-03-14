package ch.epfl.javions.adsb;

import ch.epfl.javions.demodulation.PowerWindow;

import java.io.IOException;
import java.io.InputStream;

public class AdsbDemodulator
{
    PowerWindow window;
    public AdsbDemodulator(InputStream samplesStream) throws IOException
    {
        window = new PowerWindow(samplesStream, 1200);
    }
    RawMessage newtMessage() throws IOException{

    }
}
