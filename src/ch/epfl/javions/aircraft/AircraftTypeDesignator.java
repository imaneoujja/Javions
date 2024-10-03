package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;


/**
 * L'enregistrement public AircraftTypeDesignator représente l'indicateur de type de l'aéronef
 *
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */
public record AircraftTypeDesignator(String string) {


    private static final Pattern expReg = Pattern.compile("^$|[A-Z0-9]{2,4}");

    /**
     * Le constructeur compact de cet enregistrement valide la chaîne qui lui est passée
     *
     * @throws IllegalArgumentException si elle ne représente pas d'indicateur de type valide.
     */

    public AircraftTypeDesignator {
        Preconditions.checkArgument(expReg.matcher(string).matches());
    }
}

