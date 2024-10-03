package ch.epfl.javions;

import java.util.Objects;


/**
 * Le type Bits public et non instanciable,
 * contient des méthodes permettant d'extraire un sous-ensemble des 64 bits d'une valeur de type long
 *
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */
public final class Bits {

    /**
     * Cette méthode public et statique lève IllegalArgumentException si la taille est invalide
     * ou IndexOutOfBoundsException si la plage décrite par start et size est invalide
     *
     * @param value
     * @param start indice de départ
     * @param size  La taille du vecteur que veut extraire
     * @return extrait du vecteur de 64 bits value la plage de size bits commençant au bit d'index start qu'elle interprète comme une valeur non signée
     * @throws IllegalArgumentException  si la taille est invalide
     * @throws IndexOutOfBoundsException si la plage décrite par start et size est invalide
     */
    public static int extractUInt(long value, int start, int size) {
        Preconditions.checkArgument(size < Integer.SIZE && size > 0);
        Objects.checkFromIndexSize(start, size, Long.SIZE);
        long masque = ((long) 1 << size) - 1;
        return (int) ((value >>> start) & masque);
    }

    /**
     * Cette méthode public et statique test si le bit d'indice index est un 1 ou 0 (les bits sont numérotés de droite à gauche, le bit le plus à droite ayant l'index 0)
     *
     * @param value
     * @param index indice de départ
     * @return vrai ssi le bit de value d'index donné vaut 1
     * @throws IndexOutOfBoundsException s'il n'est pas compris entre 0 (inclus) et 64 (exclu)
     */

    public static boolean testBit(long value, int index) {
        Objects.checkIndex(index, Long.SIZE);
        return ((value >> index) & 1) == 1;
    }

    /**
     * Le constructeur par défaut prive de Bits
     */
    private Bits() {
    }
}

