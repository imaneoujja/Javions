package ch.epfl.javions.aircraft;

/**
 * Le type énuméré WakeTurbulenceCategory représente la catégorie de turbulence de sillage d'un aéronef
 *
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */
public enum WakeTurbulenceCategory {

    LIGHT("L"),
    MEDIUM("M"),
    HEAVY("H"),
    UNKNOWN("");


    /**
     * Cette methode convertit les valeurs textuelles de la base de données en éléments du type énuméré
     *
     * @param s une valeur textuelle de la base de données
     * @return la catégorie de turbulence de sillage correspondant à la chaîne donnée
     */
    public static WakeTurbulenceCategory of(String s) {
        for (WakeTurbulenceCategory categorie : WakeTurbulenceCategory.values()) {
            if (categorie.cat.equals(s)) {
                return categorie;
            }
        }
        return UNKNOWN;
    }

    private final String cat;


    /**
     * Le constructeur de WakeTurbulenceCategory
     *
     * @param cat categorie de turbulence de sillage d'un aéronef
     */
    WakeTurbulenceCategory(String cat) {
        this.cat = cat;
    }

}