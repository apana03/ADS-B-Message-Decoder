package ch.epfl.javions;

/**
 * @author Andrei Pana 361249
 * @author David Fota 355816
 * Date: 01/03/2023
 * represents a calculator of Crc of 24 bits
 */
public final class Crc24 {
    /**
     * the least significant 24 bits of the generator used to calculate the CRC24 of messages ADS-B
     */
    public static int GENERATOR = 16774153;
    private final int generator;
    private final int[] table;
    private static final int GENERATOR_MASK = 0xFFFFFF;
    private static final int BYTE_MASK = 0xFF;
    private static final int SEVENTH_BIT_MASK = 0x1000000;
    private static final int TABLE_SIZE = 256;

    /**
     * Public constructor for the Crc24 class
     * Assigns the table to a local variable and
     *
     * @param generator generator used for this Crc24
     */

    public Crc24(int generator) {
        this.generator = generator & GENERATOR_MASK;
        table = buildTable(this.generator);
    }

    /**
     * Calculates the CRC24 of the given table
     *
     * @param bytes the table of bytes
     * @return the CRC24
     */
    public int crc(byte[] bytes) {
        int crc = 0;
        for (byte b : bytes) {
            int a = b & BYTE_MASK;
            crc = ((crc << 8) | a) ^ table[getMostSignificantByte(crc)];
        }
        for (int i = 0; i < 3; i++) {
            crc = ((crc << 8)) ^ table[getMostSignificantByte(crc)];
        }
        return crc_bitwise(generator, bytes);
    }

    /**
     * Calculates the crc using the basic bitwise method
     *
     * @param generator the generator used for this crc24
     * @param data      table of bytes representing the received message
     * @return the CRC24
     */
    private static int crc_bitwise(int generator, byte[] data) {
        int crc = 0;
        for (byte b : data) {
            crc ^= (b & BYTE_MASK) << 16;
            for (int i = 0; i < 8; i++) {
                crc <<= 1;
                if ((crc & SEVENTH_BIT_MASK) != 0) {
                    crc ^= generator;
                }
            }
        }
        return crc & GENERATOR_MASK;
    }

    private static int[] buildTable(int generator) {
        int[] table = new int[TABLE_SIZE];
        for (int i = 0; i < TABLE_SIZE; i++) {
            table[i] = crc_bitwise(generator, new byte[]{(byte) i});
        }
        return table;
    }

    private static int getMostSignificantByte(int value) {
        return ((value >> 16) & BYTE_MASK);
    }
}
