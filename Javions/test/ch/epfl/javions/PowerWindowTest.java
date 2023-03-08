package ch.epfl.javions;

import ch.epfl.javions.demodulation.PowerWindow;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PowerWindowTest
{
    FileInputStream inputStream = new FileInputStream("Javions/resources/samples.bin");
    PowerWindowTest() throws FileNotFoundException{}
    @Test
    void testInvalidWindowSize() throws IOException {
        // Test with invalid window size (less than 0)
        InputStream stream = new ByteArrayInputStream(new byte[1000]);
        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(stream, -1));
    }


    @Test
    void testWindowSizeExceedsLimit() throws IOException {
        // Test with window size exceeding limit (greater than 2^16)
        InputStream stream = new ByteArrayInputStream(new byte[1000]);
        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(stream, 65537));
    }


    @Test
    void testAdvanceByNegativeOffset() throws IOException {
        // Test with negative offset in advanceBy method
        InputStream stream = new ByteArrayInputStream(new byte[1000]);
        PowerWindow window = new PowerWindow(stream, 10);
        assertThrows(IllegalArgumentException.class, () -> window.advanceBy(-1));
    }


    @Test
    void testGetInvalidIndex() throws IOException {
        // Test with invalid index in get method
        InputStream stream = new ByteArrayInputStream(new byte[1000]);
        PowerWindow window = new PowerWindow(stream, 10);
        assertThrows(IndexOutOfBoundsException.class, () -> window.get(10));
    }


    // Test que le constructeur lève IllegalArgumentException lorsque la taille de la fenêtre est inférieure à 0
    @Test
    public void testConstructorThrowsIllegalArgumentExceptionWhenWindowSizeIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(inputStream, -1));
    }


    // Test que le constructeur lève IllegalArgumentException lorsque la taille de la fenêtre est supérieure à 2^16
    @Test
    public void testConstructorThrowsIllegalArgumentExceptionWhenWindowSizeIsGreaterThanMaxWindowSize() {
        assertThrows(IllegalArgumentException.class, () -> new PowerWindow(inputStream, 65537));
    }


    // Test que la méthode advanceBy lève IllegalArgumentException lorsque l'offset est négatif
    @Test
    public void testAdvanceByThrowsIllegalArgumentExceptionWhenOffsetIsNegative() throws IOException {
        PowerWindow window = new PowerWindow(inputStream, 10);
        assertThrows(IllegalArgumentException.class, () -> window.advanceBy(-1));
    }


    // Test que la méthode get lève IndexOutOfBoundsException lorsque l'index est inférieur à 0
    @Test
    public void testGetThrowsIndexOutOfBoundsExceptionWhenIndexIsNegative() throws IOException {
        PowerWindow window = new PowerWindow(inputStream, 10);
        assertThrows(IndexOutOfBoundsException.class, () -> window.get(-1));
    }


    // Test que la méthode get lève IndexOutOfBoundsException lorsque l'index est supérieur ou égal à la taille de la fenêtre
    @Test
    public void testGetThrowsIndexOutOfBoundsExceptionWhenIndexIsGreaterThanWindowSize() throws IOException {
        PowerWindow window = new PowerWindow(inputStream, 10);
        assertThrows(IndexOutOfBoundsException.class, () -> window.get(11));
    }

}
