package ch.epfl.javions.aircraft;

import java.util.Objects;

/**
 *Defines the regular expression for the Aircraft Data
 *
 * @author Andrei Pana
 * @author David Fota
 */

public record AircraftData(AircraftRegistration registration, AircraftDescription description,
                           AircraftTypeDesignator typeDesignator, String model,
                           WakeTurbulenceCategory wakeTurbulenceCategory)
{
    public AircraftData
    {
       Objects.requireNonNull(registration);
       Objects.requireNonNull(description);
       Objects.requireNonNull(typeDesignator);
       Objects.requireNonNull(model);
       Objects.requireNonNull(wakeTurbulenceCategory);
    }
}
