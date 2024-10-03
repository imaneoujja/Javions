package ch.epfl.javions.aircraft;

import java.util.Objects;

/**
 * Cet enregistrement collecte les données fixes d'un aéronef.
 *
 * @param registration           Registration de l'aéronef
 * @param typeDesignator         TypeDesignator de l'aéronef
 * @param model                  model de l'aéronef
 * @param description            description de l'aéronef
 * @param wakeTurbulenceCategory wakeTurbulenceCategory de l'aéronef
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 * @throws NullPointerException si l'un de ses arguments est nul
 */
public record AircraftData(AircraftRegistration registration, AircraftTypeDesignator typeDesignator, String model,
                           AircraftDescription description, WakeTurbulenceCategory wakeTurbulenceCategory) {
    public AircraftData {
        Objects.requireNonNull(registration);
        Objects.requireNonNull(typeDesignator);
        Objects.requireNonNull(model);
        Objects.requireNonNull(description);
        Objects.requireNonNull(wakeTurbulenceCategory);
    }

}
