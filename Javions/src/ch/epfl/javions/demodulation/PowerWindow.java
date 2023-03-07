package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 *Represents a fixed-size window on a sequence of power samples produced by a power computer
 *
 * @author Andrei Pana 361249
 * @author David Fota 355816
 */
public final class PowerWindow {

    private int windowSize;
    private long position = 0;
    private int[] batch1;
    private int[] batch2;

    private PowerComputer powerComputer;


    public PowerWindow(InputStream stream,int windowSize) throws IOException{
        Preconditions.checkArgument(windowSize>0 && windowSize<=65536);
        this.windowSize = windowSize;
        batch1 = new int[windowSize];
        batch2 = new int[windowSize];
        powerComputer = new PowerComputer(stream,windowSize);
        powerComputer.readBatch(batch1);
    }

    /**
     * @return the window size
     */
    public int size(){
        return windowSize;
    }

    long position(){
        return position;
    }

    public boolean isFull() {
        return true;
    }

    int get(int i){
        if(i<0 && i>=windowSize){
            throw new IndexOutOfBoundsException();
        }

    }

    void advance() throws IOException{
        if(position == 0){
            powerComputer.readBatch(batch2);
        }
        position++;
        if(position%windowSize == 0){
            batch1 = batch2;
            powerComputer.readBatch(batch2);
        }
    }

    void advanceBy(int offset) throws IOException{
        Preconditions.checkArgument(offset>=0);

    }


}
