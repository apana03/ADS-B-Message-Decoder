package ch.epfl.javions.aircraft;

import java.util.Objects;

/**
 *Defines the regular expression for the Aircraft Data
 *
 * @author Andrei Pana
 * @author David Fota 355816
 */

    public record AircraftData(AircraftRegistration registration, AircraftTypeDesignator typeDesignator,
                           String model,AircraftDescription description,
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
