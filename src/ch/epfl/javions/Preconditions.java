package ch.epfl.javions;

/**
 * La classe Preconditions a pour but de faciliter l'ecriture de preconditions en offrant la methode checkArgument
 *
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */

public final class Preconditions {

    /**
     * Cette methode leve l'exception IllegalArgumentException si son argument est faux, et ne fait rien sinon
     *
     * @param shouldBeTrue
     * @throws IllegalArgumentException si son argument est faux
     */

    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException("Your argument should be true");
        }
    }


    /**
     * Le constructeur par defaut prive de Preconditions
     */
    private Preconditions() {
    }


}
