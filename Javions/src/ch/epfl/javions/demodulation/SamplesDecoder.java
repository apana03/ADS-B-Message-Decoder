package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
/**
 * Represents a sample decoder
 * transforming received octets
 * into 12-bit signed samples
 * @author David Fota 355816
 * @author Andrei Pana 361249
 */
public final class SamplesDecoder
{
    private InputStream stream;
    private int batchSize;
    private byte[] octets;
    /**
     * @param stream, batchSize
     *        The input stream received by the decoder
     *        Size of the batch that is to be decoded
     * @throws IllegalArgumentException if batchSize is 0 or negative
     * @throws NullPointerException if the received stream is null
     */
    public SamplesDecoder(InputStream stream, int batchSize)
    {
        Preconditions.checkArgument(batchSize > 0);
        if(stream == null)
            throw new NullPointerException();
        this.stream = stream;
        this.batchSize = batchSize;
    }

    /**
     * @param batch
     * reads from the stream passed to the constructor
     * the number of bytes corresponding to a batch,
     * then converts these bytes into signed samples,
     * which are placed in the array passed as argument
     * @return the size of the batch if it was big enough
     * for all the octets to be read, and the number of octets
     * that have been read divided by 2 and rounded to floor
     * otherwise
     * @throws IOException
     */
    public int readBatch(short[] batch) throws IOException
    {
        Preconditions.checkArgument(batchSize == batch.length);
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
