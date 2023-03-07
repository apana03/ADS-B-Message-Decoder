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
        signedBatchPrevious = new short[batchSize*2];
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
        decoder.readBatch(signedBatchPrevious);
        short[] signedBatchCurrent = new short[signedBatchPrevious.length + 8];
        System.arraycopy(last8Bytes, 0, signedBatchCurrent, 0, 8);
        for( int i = 0; i < signedBatchPrevious.length; i++ )
            signedBatchCurrent[i + 8] = signedBatchPrevious[i];
        for( int i = 8; i < signedBatchCurrent.length; i += 2 )
        {
            for(int j = 0; j < 8; j++)
                if( i + 1 - j >= 0)
                    samples[j] = signedBatchCurrent[i + 1 - j];
            batch[(i - 8)/2] = (int) (Math.pow((- samples[0] + samples[2] - samples[4] + samples[6]), 2) +
                    Math.pow(- samples[1] + samples[3] - samples[5] + samples[7], 2));
        }
        for(int i = 0; i < 8; i++)
            last8Bytes[i] = signedBatchPrevious[signedBatchPrevious.length - 8 + i];
        return batchSize;
    }
}
