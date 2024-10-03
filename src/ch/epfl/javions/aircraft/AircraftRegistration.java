package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * L'enregistrement public AircraftRegistration représente l'immatriculation de l'aéronef
 *
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */
public record AircraftRegistration(String string) {
    private static final Pattern expReg1 = Pattern.compile("[A-Z0-9 .?/_+-]+");


    /**
     * Le constructeur compact de cet enregistrement valide la chaîne qui lui est passée
     *
     * @throws IllegalArgumentException si elle ne représente pas d'immatriculation valide.
     */
    public AircraftRegistration {
        Objects.requireNonNull(string);
        Preconditions.checkArgument((expReg1.matcher(string).matches()));
    }
}
