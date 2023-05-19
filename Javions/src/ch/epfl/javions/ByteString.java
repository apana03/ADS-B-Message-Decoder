package ch.epfl.javions;

import java.util.Arrays;
import java.util.Objects;
import java.util.HexFormat;

/**
 * Represents a sequence of Bytes
 *
 * @author Andrei Pana 361249
 * @author David Fota 355816
 */
public final class ByteString {

    private static final HexFormat hf = HexFormat.of().withUpperCase();
    private final byte[] byteString;

    /**
     * the constructor of the class
     *
     * @param bytes
     *      the bytes array which will help build the byteString
     */
    public ByteString(byte[] bytes) {
        byteString = bytes.clone();
    }

    /**
     * @param hexString the string containing the hexadecimal number
     * @throws IllegalArgumentException if the given string  is not of even length
     * @throws NumberFormatException    if it contains a character that is not a hexadecimal digit
     * @return the byte string
     * whose string passed as argument is the hexadecimal
     * representation
     */
    public static ByteString ofHexadecimalString(String hexString) {
        Preconditions.checkArgument(hexString.length() % 2 == 0);
        byte[] bytes = hf.parseHex(hexString);
        return new ByteString(bytes);
    }

    /**
     * @return the size of the ByteString
     */
    public int size() {
        return byteString.length;
    }


    /**
     * @param index the index
     * @return the unsigned byte at the given index
     */
    public int byteAt(int index) {
        return Byte.toUnsignedInt(byteString[index]);
    }

    /**
     * @param fromIndex start index
     * @param toIndex   end index
     * @throws IndexOutOfBoundsException if the range described by fromIndex and toIndex
     *                                   is not entirely between 0 and the size of the string
     * @throws IllegalArgumentException  if the difference between toIndex and fromIndex is not strictly less
     *                                   than the number of bytes contained in a long type value
     * @return the bytes between the indexes fromIndex (inclusive)
     * and toIndex (excluded) as a value of type long
     */
    public long bytesInRange(int fromIndex, int toIndex) {
        Objects.checkFromToIndex(fromIndex, toIndex, byteString.length);
        int numBytes = toIndex - fromIndex;
        Preconditions.checkArgument(numBytes <= Long.BYTES);
        long result = 0;
        for (int i = fromIndex; i < toIndex; i++) {
            result <<= Byte.SIZE;
            result |= (byteAt(i));
        }
        return result;
    }

    @Override
    public boolean equals(Object thatO) {
        if (thatO instanceof ByteString that) {
            return Arrays.equals(this.getBytes(), that.getBytes());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(byteString);
    }

    @Override
    public String toString() {
        return hf.formatHex(byteString);
    }

    private byte[] getBytes() {
        return byteString;
    }
}
