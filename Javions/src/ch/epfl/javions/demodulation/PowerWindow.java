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

    private static final int WINDOW_MAX_SIZE = 1<<16;

    private int windowSize;
    private long position = 0;
    private long samplesDecoded =0;
    private int[] batch1;
    private int[] batch2;

    private PowerComputer powerComputer;


    public PowerWindow(InputStream stream,int windowSize) throws IOException{
        Preconditions.checkArgument(windowSize>0 && windowSize<=WINDOW_MAX_SIZE);
        this.windowSize = windowSize;
        batch1 = new int[WINDOW_MAX_SIZE];
        batch2 = new int[WINDOW_MAX_SIZE];
        powerComputer = new PowerComputer(stream,WINDOW_MAX_SIZE);
        samplesDecoded+=powerComputer.readBatch(batch1);
    }

    /**
     * @return the window size
     */
    public int size(){
        return windowSize;
    }

    public long position(){
        return position;
    }

    public boolean isFull() {

        return (samplesDecoded >= position+windowSize);
    }

    public int get(int i){
        if(i < 0 || i >= windowSize){
            throw new IndexOutOfBoundsException();
        }
        int positionStart = (int) position % WINDOW_MAX_SIZE ;
            if(positionStart + i < WINDOW_MAX_SIZE){
                return batch1[positionStart + i];
            }else{
                return batch2[(positionStart + i)%WINDOW_MAX_SIZE];
            }
    }

    public void advance() throws IOException{
        if(position == 0){
            samplesDecoded+=powerComputer.readBatch(batch2);
            position++;
        }else{
            position++;
            if(position % WINDOW_MAX_SIZE == 0){
                batch1 = batch2;
                samplesDecoded+=powerComputer.readBatch(batch2);
            }
        }
    }

    public void advanceBy(int offset) throws IOException{
        Preconditions.checkArgument(offset>=0);
        for(int i = 0 ;i < offset;i++){
            advance();
        }
    }
}
