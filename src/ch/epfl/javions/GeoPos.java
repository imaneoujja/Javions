package ch.epfl.javions;

import javafx.beans.value.ObservableValue;

import static ch.epfl.javions.Units.Angle.*;

/**
 * L'enregistrement public GeoPos représente des coordonnées géographiques.
 *
 * @param longitudeT32 la longitude exprimée en t32 et stockée sous la forme d'entiers de 32 bits (type int)
 * @param latitudeT32  la latitude exprimée en t32 et stockée sous la forme d'entiers de 32 bits (type int)
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */

public record GeoPos(int longitudeT32, int latitudeT32) {

    private static final int BOUND = 1 << 30;


    /**
     * Cette méthode publique et statique vérifie si la latitude est comprise entre -2^30 et 2^30
     *
     * @param latitudeT32 la longitude exprimée en t32 et stockée sous la forme d'entiers de 32 bits (type int)
     * @return Vrai ssi la valeur passée, interprétée comme une latitude exprimée en t32, est valide.
     */
    public static boolean isValidLatitudeT32(int latitudeT32) {
        return (latitudeT32 >= -BOUND && latitudeT32 <= BOUND);

    }


    /**
     * Le constructeur compact de GeoPos pour valider la latitude reçue et
     *
     * @throws IllegalArgumentException si elle est invalide
     */
    public GeoPos {
        Preconditions.checkArgument(isValidLatitudeT32(latitudeT32));
    }


    /**
     * Cette méthode publique appelle la méthode convert de Units pour calculer la longitude en radians
     *
     * @return la longitude en radians
     */

    public double longitude() {
        return Units.convert(longitudeT32, Units.Angle.T32, RADIAN);
    }


    /**
     * Cette méthode publique appelle la méthode convert de Units pour calculer la latitude en radians
     *
     * @return la latitude en radians
     */

    public double latitude() {
        return Units.convert(latitudeT32, Units.Angle.T32, RADIAN);
    }


    /**
     * Une redéfinition de la méthode toString de Object
     *
     * @return une représentation textuelle de la position dans laquelle la longitude et la latitude sont données en degrés
     */

    @Override
    public String toString() {
        return "(" + Units.convert(longitudeT32, T32, DEGREE) + "°" + ", " + Units.convert(latitudeT32, T32, DEGREE) + "°" + ")";
    }
}

