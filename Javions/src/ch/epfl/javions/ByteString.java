package ch.epfl.javions;

import java.util.HexFormat;

public final class ByteString {
    private byte[] byteString;
    public ByteString(byte[] bytes){
        byteString = bytes.clone();
    }

    static ByteString ofHexadecimalString(String hexString){
        HexFormat hf = HexFormat.of().withUpperCase();
        if(hexString.length()%2!=0){
            throw new IllegalArgumentException();
        }
        for(int i=0;i<hexString.length();i++){
            if(!HexFormat.isHexDigit(hexString.charAt(i))){
                throw new NumberFormatException();
            }
        }
        byte[] bytes = hf.parseHex(hexString);
        ByteString byteString  = new ByteString(bytes);
        return byteString;
    }

    public int size(){
        return byteString.length;
    }

    public int byteAt(int index){
        int wantedByte = byteString[index] & 0xFF;
        return wantedByte;
    }



}
