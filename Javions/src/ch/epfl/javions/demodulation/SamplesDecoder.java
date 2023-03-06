package ch.epfl.javions.demodulation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

public final class SamplesDecoder
{
    InputStream stream;
    int batchSize;
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
        Reader r = new InputStreamReader(stream, StandardCharsets.UTF_8);
        BufferedReader b = new BufferedReader(r);
        String l = "";
        for( int i = 0; i < batchSize; i+=2)
        {
            l = "";
            for (int j = 0; j < 16; j++)
            {
                l += (char)b.read();
            }
            byte[] sample = HexFormat.of().parseHex(l);
            batch[i] = (short) (0 | (sample[3]<<8));
            batch[i] |= (sample[1]<<4 | sample[0]);
        }
        return batchSize;
    }
}
