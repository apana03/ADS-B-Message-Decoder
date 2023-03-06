package ch.epfl.javions.demodulation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

public final class SamplesDecoder
{
    InputStream stream;
    int batchSize;
    byte[] octets;
    public SamplesDecoder(InputStream stream, int batchSize)
    {
        if(batchSize <= 0)
            throw new IllegalArgumentException();
        if(stream == null)
            throw new NullPointerException();
        this.stream = stream;
        this.batchSize = batchSize;
    }
    public int readBatch(short[] batch) throws IOException
    {
        if( batch.length != batchSize)
            throw new IllegalArgumentException();
        octets = stream.readNBytes(batchSize*2);
        for( int i = 0; i < batchSize; i++)
        {
            batch[i] = (short) ((short) 0 | ((octets[2*i + 1] & 0x00001111) << 8));
            batch[i] |= octets[2*i];
        }
        if(octets.length < batchSize*2)
           return (int) Math.floor(octets.length/2);
        return batchSize;
    }
}
