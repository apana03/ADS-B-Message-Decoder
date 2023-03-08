package ch.epfl.javions;

import ch.epfl.javions.aircraft.AircraftRegistration;
import ch.epfl.javions.demodulation.SamplesDecoder;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SamplesDecoderTest
{
    FileInputStream stream;
    public SamplesDecoderTest() throws FileNotFoundException
    {
        FileInputStream stream = new FileInputStream("Javions/resources/samples.bin");
        this.stream = stream;
    }
    @Test
    void CheckSamplesDecoderReturnsWell() throws IOException {
        SamplesDecoder decoder = new SamplesDecoder(stream, 10);
        short[] batch = new short[10];
        short[] expected = {-3, 8, -9, -8, -5, -8, -12, -16, -23, -9};
        decoder.readBatch(batch);
        for( int i = 0; i < 10; i++)
            assertEquals(expected[i], batch[i]);
    }
    @Test
    public void testNullStream() {
        assertThrows(NullPointerException.class, () -> {
            SamplesDecoder test = new SamplesDecoder(null, 10);
        });
    }


    @Test
    public void testInvalidBatchSize() {


        assertThrows(IllegalArgumentException.class, () -> {
            SamplesDecoder test = new SamplesDecoder(new ByteArrayInputStream(new byte[0]), 0);
        });
    }


    @Test
    void constructorThrowsIllegalArgumentExceptionWhenBatchSizeIsZero() {
        assertThrows(IllegalArgumentException.class, () -> new SamplesDecoder(new ByteArrayInputStream(new byte[0]), 0));
    }


    @Test
    void constructorThrowsIllegalArgumentExceptionWhenBatchSizeIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new SamplesDecoder(new ByteArrayInputStream(new byte[0]), -1));
    }


    @Test
    void constructorThrowsNullPointerExceptionWhenInputStreamIsNull() {
        assertThrows(NullPointerException.class, () -> new SamplesDecoder(null, 10));
    }


    @Test
    void readBatchThrowsIllegalArgumentExceptionWhenBatchSizeIsNotEqualToLengthOfBatchArray() throws IOException {
        SamplesDecoder decoder = new SamplesDecoder(new ByteArrayInputStream(new byte[24]), 4);
        assertThrows(IllegalArgumentException.class, () -> decoder.readBatch(new short[3]));
    }
}
