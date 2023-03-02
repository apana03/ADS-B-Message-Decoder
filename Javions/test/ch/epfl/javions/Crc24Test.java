package ch.epfl.javions;

import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * Author: Andrei Pana
 * Date:
 */
public class Crc24Test {
    @Test
    void crcbitwiseCalculatesCRC24Correctly(){
        Crc24 crc24 = new Crc24(Crc24.GENERATOR);
        String mS = "8D392AE499107FB5C00439";
        String cS = "035DB8";
        int c = Integer.parseInt(cS, 16); // == 0xEE2EC6
        byte[] mOnly = HexFormat.of().parseHex(mS);
        System.out.println(HexFormat.of().toHexDigits(c));
        System.out.println(HexFormat.of().toHexDigits(crc24.crc(mOnly)));
        assertEquals(c, crc24.crc(mOnly));
    }
}
