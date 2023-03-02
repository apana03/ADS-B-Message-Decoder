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

    public Crc24(int generator){
        this.generator = generator & 0xFFFFFF;
    }

    /**
     * calculates the CRC24 of the given table
     * @param bytes the table of bytes
     * @return the CRC24
     */
   public int crc(byte[] bytes) {
       return crc_bitwise(generator, bytes);
   }

    private  int crc_bitwise(int generator, byte[]bytes) {
        int leastSignificantBits = generator & 0xFFFFFF;
        int table[] = new int[]{0, leastSignificantBits};
        int crc = 0;
        for (byte b : bytes) {
            for (int i = 1; i <8; i <<= 1) {
                int bit = b & i;
                crc = ((crc << 1) | bit) ^ table[(crc & 4194304) >> 22];
            }
        }
        for(int j = 0; j< 3;j++){
            byte b = 0;
            for(int i = 1 ; i < 8 ; i<<=1) {
                int bit = b&i;
                crc = ((crc << 1) | bit) ^ table[(crc & 4194304) >> 22];
            }
        }
        return crc & 0xFFFFFF;
    }



private static int[] buildTable(int generator){
        byte bytes[] = new byte[256];
        for(int i =0; i<256;i++){
            bytes[i] = (byte)i;
        }
        return new int[2];
    }






}
