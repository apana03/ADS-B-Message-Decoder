package ch.epfl.javions;

import ch.epfl.javions.aircraft.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
public class AircraftDataBaseTest {
    @Test
    void checkDoesNotThrow(){
        AircraftDatabase database=new AircraftDatabase("/aircraft.zip");
        assertDoesNotThrow(()->database.get(new IcaoAddress("0087BB")));
        assertDoesNotThrow(()->database.get(new IcaoAddress("A4A8FF")));
    }

    @Test
    void checkThrowsOnWrongFile(){
        assertThrows(NullPointerException.class,()->new AircraftDatabase("/aircraft1.zip"));
    }

    @Test
    void checkReturnValues(){
        AircraftDatabase database=new AircraftDatabase("/aircraft.zip");
        assertTrue(()->{
            try {
                return Objects.equals(database.get(new IcaoAddress("A4A8FF")),
                        new AircraftData(new AircraftRegistration("N4LX"), new AircraftDescription("L1P")
                                                        , new AircraftTypeDesignator("PTS2"), "PITTS S-2 Special", WakeTurbulenceCategory.LIGHT));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertTrue(()->{
            try {
                return Objects.equals(database.get(new IcaoAddress("A4AAFF")),
                        new AircraftData(new AircraftRegistration("N40HH"), new AircraftDescription("")
                                                        , new AircraftTypeDesignator(""), "", WakeTurbulenceCategory.UNKNOWN));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        assertTrue(()->{
            try {
                return Objects.equals(database.get(new IcaoAddress("94AAFF")),null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
