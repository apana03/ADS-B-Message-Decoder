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
        this.table = buildTable(this.generator);
    }

    /**
     * calculates the CRC24 of the given table
     * @param bytes the table of bytes
     * @return the CRC24
     */
   public int crc(byte[] bytes) {
       int crc = 0;
       for(byte b:bytes){
           crc = ((crc << 8) | (b & 0xFF)) ^ table[getMostSignificantByte(crc)];
       }
       return crc & 0xFFFFFF;
   }

    /*private  static int crc_bitwise(int generator, byte[]bytes) {
        int table[] = new int[]{0, generator};
        int crc = 0;
        for (byte b : bytes) {
            for (int i = 1; i <8; i <<= 1) {
                int bit = b & i;
                crc = ((crc << 1) | bit) ^ table[(crc & 4194304) >> 22];
            }
        }
        for(int j = 0; j< 3;j++){
            for(int i = 1 ; i < 8 ; i<<=1) {
                int bit = 0;
                crc = ((crc << 1) | bit) ^ table[(crc & 4194304) >> 22];
            }
        }
        return crc & 0xFFFFFF;
    }

     */
    private static int crc_bitwise(int generator, byte[] data) {
        int crc = 0x000000;
        for (byte b : data) {
            crc ^= (b & 0xFF) << 16;
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
        for(int i=0; i<256;i++){
            table[i] =  crc_bitwise(generator, new byte[]{(byte)(i&0xFF)});
        }
        return table;
    }
    private static char getMostSignificantByte(int value) {
        return (char) ((value >> 24) & 0xFF);
    }







}
