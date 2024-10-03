package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

/**
 * L'enregistrement CallSign représente ce que l'on appelle l'indicatif de l'aéronef
 *
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */
public record CallSign(String string) {
    private static final Pattern string_ = Pattern.compile("[A-Z0-9 ]{0,8}");

    /**
     * Le constructeur compact de cet enregistrement valide la chaîne qui lui est passée
     *
     * @throws IllegalArgumentException si elle ne représente pas d'indicatif valide.
     */
    public CallSign {
        Preconditions.checkArgument(Pattern.matches(String.valueOf(string_), string));
    }
}
