package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * L'enregistrement public AircraftDescription représente la description de l'aéronef
 *
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */
public record AircraftDescription(String string) {

    private static final Pattern expReg = Pattern.compile("^$|[ABDGHLPRSTV-][0123468][EJPT-]");

    /**
     * Le constructeur compact de cet enregistrement valide la chaîne qui lui est passée
     *
     * @throws IllegalArgumentException si elle ne représente pas de description valide.
     */
    public AircraftDescription {

        Preconditions.checkArgument((expReg.matcher(string).matches()));
    }
}