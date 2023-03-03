package ch.epfl.javions;
/**
 *Represents the preconditions
 * @author Andrei Pana 361249
 * @author David Fota 355816
 */
public final class Preconditions {
    private Preconditions() {}
    public static void checkArgument(boolean shouldBeTrue){
            if(!shouldBeTrue){
                throw new IllegalArgumentException();
            }
    }
}
