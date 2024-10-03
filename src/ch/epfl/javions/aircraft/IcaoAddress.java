package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Cet enregistrement public représente une adresse OACI
 *
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */

public record IcaoAddress(String string) {
    private static final Pattern expReg1 = Pattern.compile("[0-9A-F]{6}");

    /**
     * Le constructeur compact de cet enregistrement valide la chaîne qui lui est passée
     *
     * @throws IllegalArgumentException si elle ne représente pas une adresse OACI valide.
     */

    public IcaoAddress {
        Objects.requireNonNull(string);
        Preconditions.checkArgument((expReg1.matcher(string).matches()));
    }

    public String getString() {
        return string;
    }

}
