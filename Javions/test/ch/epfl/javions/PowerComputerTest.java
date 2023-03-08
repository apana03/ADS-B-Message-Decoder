package ch.epfl.javions;

import ch.epfl.javions.demodulation.PowerComputer;
import ch.epfl.javions.demodulation.SamplesDecoder;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    @Test
    public void testConstructorInvalidBatchSize() {
        assertThrows(IllegalArgumentException.class, () -> new PowerComputer(null, 5));
    }


    @Test
    public void testReadBatchInvalidBatchSize() throws IOException {
        // Initialise un PowerComputer avec un lot de taille 16
        ByteArrayInputStream input = new ByteArrayInputStream(new byte[0]);
        PowerComputer computer = new PowerComputer(input, 16);


        // Vérifie que readBatch lève une exception si le tableau passé en argument n'a pas la bonne taille
        assertThrows(IllegalArgumentException.class, () -> computer.readBatch(new int[10]));
    }
    @Test
    public void testConstructorIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new PowerComputer(new ByteArrayInputStream(new byte[0]), 7));
    }


    @Test
    public void testReadBatchIllegalArgumentException() throws IOException {
        PowerComputer pc = new PowerComputer(new ByteArrayInputStream(new byte[8]), 8);
        assertThrows(IllegalArgumentException.class, () -> pc.readBatch(new int[7]));
    }


    @Test
    public void testConstructorInvalidBatchSize1() {
        InputStream stream = new ByteArrayInputStream(new byte[100]);
        assertThrows(IllegalArgumentException.class, () -> new PowerComputer(stream, 5));
    }


    @Test
    public void testConstructorIllegalArgumentException1() {
        assertThrows(IllegalArgumentException.class, () -> new PowerComputer(new ByteArrayInputStream(new byte[0]), 7));
    }


    @Test
    public void testReadBatchIllegalArgumentException1() throws IOException {
        PowerComputer pc = new PowerComputer(new ByteArrayInputStream(new byte[8]), 8);
        assertThrows(IllegalArgumentException.class, () -> pc.readBatch(new int[7]));
    }

}
