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
    private long samplesDecoded =0;
    private int[] batch1;
    private int[] batch2;

    private PowerComputer powerComputer;


    public PowerWindow(InputStream stream,int windowSize) throws IOException{
        Preconditions.checkArgument(windowSize>0 && windowSize<=65536);
        this.windowSize = windowSize;
        batch1 = new int[windowSize];
        batch2 = new int[windowSize];
        powerComputer = new PowerComputer(stream,windowSize);
        samplesDecoded+=powerComputer.readBatch(batch1);
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
        return (samplesDecoded>=position);
    }

    public int get(int i){
        if(i<0 && i>=windowSize){
            throw new IndexOutOfBoundsException();
        }
        int positionStart = (int) position%windowSize;
        if(positionStart == 0){
            return batch1[i];
        }else{
            if(positionStart+i<=windowSize){
                return batch1[i];
            }else{
                return batch2[(positionStart+i)%windowSize];
            }
        }
    }

    public void advance() throws IOException{
        if(position == 0){
            samplesDecoded+=powerComputer.readBatch(batch2);
        }
        position++;
        if(position%windowSize == 0){
            batch1 = batch2;
            samplesDecoded+=powerComputer.readBatch(batch2);
        }
    }

    public void advanceBy(int offset) throws IOException{
        Preconditions.checkArgument(offset>=0);
        if(position == 0){
            samplesDecoded+=powerComputer.readBatch(batch2);
        }
        for(int i = 0 ;i<offset;i++){
            advance();
        }
    }


}
