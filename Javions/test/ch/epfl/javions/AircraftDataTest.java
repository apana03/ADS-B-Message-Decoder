package ch.epfl.javions;

import ch.epfl.javions.aircraft.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AircraftDataTest {

    @Test
    public void testAircraftDataConstructor() {
        // Test that the constructor throws a NullPointerException when given null arguments
        Assertions.assertThrows(NullPointerException.class, () -> {
            new AircraftData(null, new AircraftTypeDesignator("B737"), "Boeing 737", new AircraftDescription(""), WakeTurbulenceCategory.MEDIUM);
        });
        Assertions.assertThrows(NullPointerException.class, () -> {
            new AircraftData(new AircraftRegistration("N12345"), null, "B737", new AircraftDescription(""), WakeTurbulenceCategory.MEDIUM);
        });
        Assertions.assertThrows(NullPointerException.class, () -> {
            new AircraftData(new AircraftRegistration("N12345"), new AircraftTypeDesignator("B737"), null, new AircraftDescription(""), WakeTurbulenceCategory.MEDIUM);
        });
        Assertions.assertThrows(NullPointerException.class, () -> {
            new AircraftData(new AircraftRegistration("N12345"), new AircraftTypeDesignator("B737"), "Boeing 737", null, WakeTurbulenceCategory.MEDIUM);
        });
        Assertions.assertThrows(NullPointerException.class, () -> {
            new AircraftData(new AircraftRegistration("N12345"), new AircraftTypeDesignator("B737"), "Boeing 737", new AircraftDescription(""), null);
        });

        // Test that the constructor does not throw a NullPointerException when given non-null arguments
        Assertions.assertDoesNotThrow(() -> {
            new AircraftData(new AircraftRegistration("N12345"), new AircraftTypeDesignator("B737"), "Boeing 737", new AircraftDescription(""), WakeTurbulenceCategory.MEDIUM);
        });
    }
}

