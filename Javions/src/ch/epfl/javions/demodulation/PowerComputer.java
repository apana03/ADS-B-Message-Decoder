package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public class PowerComputer
{
    private SamplesDecoder decoder;
    private short[] signedBatch;
    private short[] samples = new short[8];
    private int batchSize;
    public PowerComputer(InputStream stream, int batchSize)
    {
        Preconditions.checkArgument((batchSize * 4) % 8 == 0 && batchSize > 0);
        SamplesDecoder decoder = new SamplesDecoder(stream, batchSize*2);
        this.decoder = decoder;
        this.batchSize = batchSize;
        signedBatch = new short[batchSize*2];
    }
    public int readBatch( int batch[] ) throws IOException
    {
        Preconditions.checkArgument( batchSize == batch.length);
        decoder.readBatch(signedBatch);
        for( int i = 0; i < signedBatch.length; i += 2)
        {
            for(int j = 0; j < 8; j++)
                if( i + 1 - j >= 0)
                    samples[j] = signedBatch[i + 1 - j];
            batch[i/2] = (int) (Math.pow((- samples[0] + samples[2] - samples[4] + samples[6]), 2) +
                    Math.pow(- samples[1] + samples[3] - samples[5] + samples[7], 2));
        }
        return batchSize;
    }
}
