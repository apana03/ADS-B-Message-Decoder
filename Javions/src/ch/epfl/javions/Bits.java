package ch.epfl.javions;

public class Bits {
    private Bits(){}
    public static int extractUInt(long value, int start, int size)
    {
        if( size <= 0 || size >= 32 )
            throw new IllegalArgumentException();
        if( start < 0 || start >= 64 || (start + size - 1) >= 64)
            throw new IllegalArgumentException();
        return (int) (( (1 << size) - 1 ) & ( value >> (start - 1)));
    }
    public static boolean testBit(long value, int index){
        if( (value & (1 << index)) != 0 )
            return true;
        return false;
    }
}
