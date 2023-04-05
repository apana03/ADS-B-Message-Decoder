package ch.epfl.javions.regextests;

import ch.epfl.javions.aircraft.AircraftRegistration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AircraftRegistrationTest {
    @Test
    void aircraftRegistrationConstructorThrowsWithInvalidRegistration() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AircraftRegistration("abc");
        });
    }

    @Test
    void aircraftRegistrationConstructorThrowsWithEmptyRegistration() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AircraftRegistration("");
        });
    }

    @Test
    void aircraftRegistrationConstructorAcceptsValidRegistration() {
        assertDoesNotThrow(() -> {
            new AircraftRegistration("F-HZUK");
        });
    }
}