package ch.epfl.javions;

import java.util.Arrays;
import java.util.Objects;
import java.util.HexFormat;

public final class ByteString {
    private byte[] byteString;
    public ByteString(byte[] bytes){
        byteString = bytes.clone();
    }

    public static ByteString ofHexadecimalString(String hexString){
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
        return byteString[index] & 0xFF;
    }

    public long bytesInRange(int fromIndex, int toIndex){
        Objects.checkFromToIndex(fromIndex, toIndex , byteString.length);
        int numBytes = toIndex - fromIndex;
        if (numBytes > Long.BYTES) {
            throw new IllegalArgumentException();
        }
        long result = 0;
        for (int i = 0; i < toIndex; i++) {
            result <<= 8;
            result |= (byteString[i] & 0xFF);
        }
        return result;
    }

    @Override
    public boolean equals(Object thatO) {
        if (thatO instanceof ByteString that) {
            if(this.size() == that.size()){
                for(int i = 0; i<this.size();i++){
                    if(this.byteAt(i)!=that.byteAt(i)){
                        return false;
                    }
                }
                return true;
            }else{
                return false;
            }
        } else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(byteString);
    }

    @Override
    public String toString() {
        HexFormat hf = HexFormat.of().withUpperCase();
        return hf.formatHex(byteString);
    }

}
