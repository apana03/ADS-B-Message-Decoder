package ch.epfl.javions;

/**
 * @author Andrei Pana
 * @author David Fota
 * Date: 01/03/2023
 * represents a calculator of Crc of 24 bits
 *
 */
public final class Crc24 {
    /**
     * the least significant 24 bits of the generator used to calculate the CRC24 of messages ADS-B
     */
    public static int GENERATOR = 16774153;
    private int generator;
    private int table[] =  new int[256];

    public Crc24(int generator){
        this.generator = generator & 0xFFFFFF;
        table = buildTable(this.generator);
    }

    /**
     * calculates the CRC24 of the given table
     * @param bytes the table of bytes
     * @return the CRC24
     */
   public int crc(byte[] bytes)
   {
       int crc = 0;
       for(byte b : bytes)
       {
           int a = b & 0xFF;
           crc = ((crc << 8) | a) ^ table[getMostSignificantByte(crc)];
       }
       for(int i = 0; i < 3; i++)
       {
           crc = ((crc << 8)) ^ table[getMostSignificantByte(crc)];
       }
       return crc & 0xFFFFFF;
   }
    private static int crc_bitwise(int generator, byte[] data) {
        int crc = 0x000000;
        for (byte b : data) {
            crc ^= ( b & 0xFF ) << 16;
            for (int i = 0; i < 8; i++) {
                crc <<= 1;
                if ((crc & 0x1000000) != 0) {
                    crc ^= generator;
                }
            }
        }
        return crc & 0xFFFFFF;
    }
    private static int[] buildTable(int generator){
        int table[] = new int[256];
        for(int i = 0; i < 256; i++){
            table[i] =  crc_bitwise(generator, new byte[]{(byte)i});
        }
        return table;
    }
    private static int getMostSignificantByte(int value) {
        return ((value >> 16) & 0xFF);
    }
}
