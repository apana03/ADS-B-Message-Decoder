package ch.epfl.javions;

import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftRegistration;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/*
 * Author: Andrei Pana
 * Date:
 */
public class RegexConstraintsTests {
    @Test
    void IcaoAddressConstraints() {
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress(""));
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress("4B1814J"));
        assertDoesNotThrow(() -> new IcaoAddress("4B1814"));
    }
    @Test
    void AircraftRegistrationConstraints() {
        assertThrows(IllegalArgumentException.class, () -> new AircraftRegistration(""));
        assertDoesNotThrow(() -> new AircraftRegistration("HB-JDC"));
    }

    @Test
    void AircraftTypeDesignatorConstraints() {
        assertDoesNotThrow(() -> new AircraftTypeDesignator(""));
        assertThrows(IllegalArgumentException.class, () -> new AircraftTypeDesignator("abh"));
    }

    @Test
    void AircraftDescriptionConstraints() {
        assertDoesNotThrow(() -> new AircraftDescription(""));
        assertThrows(IllegalArgumentException.class, () -> new AircraftDescription("ABJ"));
    }

    @Test
    void CallSignConstraints(){
        assertDoesNotThrow(() -> new CallSign(""));
        assertDoesNotThrow(() -> new CallSign("AZA90"));
        assertThrows(IllegalArgumentException.class, ()->new CallSign("AAAAAAAAAAAAAAAAAAAAAA"));
        assertThrows(IllegalArgumentException.class, ()->new CallSign("assdas"));
    }
}
