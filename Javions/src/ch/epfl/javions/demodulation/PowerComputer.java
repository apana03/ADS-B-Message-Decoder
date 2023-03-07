package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
/**
 * Represents a power computer
 * @author David Fota 355816
 * @author Andrei Pana 361249
 */
public class PowerComputer
{
    private SamplesDecoder decoder;
    private short[] signedBatch;
    private short[] samples = new short[8];
    private int batchSize;
    /**
     * @param stream, batchSize
     *        The input stream received by the decoder
     *        Size of the batch that is to be computed
     * @throws IllegalArgumentException if the size of the batch
     * is not multiple of 8 or if it is negative
     */
    public PowerComputer(InputStream stream, int batchSize)
    {
        Preconditions.checkArgument((batchSize * 4) % 8 == 0 && batchSize > 0);
        SamplesDecoder decoder = new SamplesDecoder(stream, batchSize*2);
        this.decoder = decoder;
        this.batchSize = batchSize;
        signedBatch = new short[batchSize*2];
    }
    /**
     * @param batch
     * reads from the sample decoder the number of
     * samples needed to calculate a batch of power
     * samples, then calculates them using the given
     * formula and places them in the array passed as argument
     * @return the number of power samples that have been placed
     * in the table
     * @throws IOException in case of input/output error
     * @throws IllegalArgumentException if the size of the table
     * passed as argument is not equal to the size of a batch
     */
    public int readBatch(int[] batch) throws IOException
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
