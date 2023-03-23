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
    private short[] signedBatchPrevious;
    private short[] samples = new short[8];
    private short[] last8Bytes = new short[8];
    private short[] signedBatchCurrent;
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
        Preconditions.checkArgument(batchSize % 8 == 0 && batchSize > 0);
        SamplesDecoder decoder = new SamplesDecoder(stream, batchSize*2);
        this.decoder = decoder;
        this.batchSize = batchSize;
        signedBatchPrevious = new short[batchSize*2];
        signedBatchCurrent = new short[batchSize*2 + 8];
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
    public int readBatch( int[] batch ) throws IOException
    {
        Preconditions.checkArgument( batchSize == batch.length);
        int decoded = decoder.readBatch(signedBatchPrevious);
        for( int i = 8, k = 1; k <= decoded/2; i += 2, k++)
        {
            for(int j = 0; j < 8; j++)
                if( i + 1 - j - 8>= 0 )
                    samples[j] = signedBatchPrevious[i + 1 - j - 8];
            else samples[j] = last8Bytes[i + 1 - j];
            batch[(i - 8)/2] = (- samples[0] + samples[2] - samples[4] + samples[6]) * (- samples[0] + samples[2] - samples[4] + samples[6]) +
                    (- samples[1] + samples[3] - samples[5] + samples[7]) * (- samples[1] + samples[3] - samples[5] + samples[7]);
        }
        for(int i = 0; i < 8; i++)
            last8Bytes[i] = signedBatchPrevious[signedBatchPrevious.length - 8 + i];
        return decoded/2;
    }
}
