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

    public Crc24(int generator){

    }

    /**
     * calculates the CRC24 of the given table
     * @param bytes the table of bytes
     * @return the CRC24
     */
   // public static int crc(byte[] bytes){
     //   crc_bitwise(GENERATOR,bytes);
    ///

    private static int[] crc_bitwise(int generator, byte[]bytes) {
        int leastSignificantBits = generator & 0xFFFFFF;
        int table[] = new int[]{0, leastSignificantBits};
        int crc24[] = new int[bytes.length];
        for(int j = 0; j< bytes.length;j++){
            int crc = 0;
            byte b = bytes[j];
            for(int i = 1 ; i <= b ; i<<=1) {
                int bit = b&i;
                crc = ((crc << 1) | bit) ^ table[(crc & 4194304) >> 22];
            }
            crc = crc & 0xFFFFFF;
            crc24[j] = crc;
        }
        return crc24;
    }

    private static int[] buildTable(int generator){
        byte bytes[] = new byte[256];
        for(int i =0; i<256;i++){
            bytes[i] = (byte)i;
        }
        return crc_bitwise(generator,bytes);
    }






}
