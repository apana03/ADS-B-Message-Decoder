package ch.epfl.javions;

import ch.epfl.javions.demodulation.PowerComputer;
import ch.epfl.javions.demodulation.SamplesDecoder;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PowerComputerTest
{
    private InputStream stream;
    public PowerComputerTest() throws FileNotFoundException
    {
        FileInputStream stream = new FileInputStream("Javions/resources/samples.bin");
        this.stream = stream;
    }
    @Test
    void CheckPowerComputerCalculatesWell() throws IOException {
        PowerComputer computer = new PowerComputer(stream, 10);
        int[] batch = new int[10];
        int[] expected = {73, 292, 65, 745, 98, 4226, 12244, 25722, 36818, 23825};
        computer.readBatch(batch);
        for( int i = 0; i < 10; i++)
            assertEquals(expected[i], batch[i]);
    }
}
