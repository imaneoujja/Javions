package ch.epfl.javions;

/**
 * La classe Math2 offre des méthodes statiques permettant d'effectuer certains calculs mathématiques
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */

public final class Math2{

    /**
     *limite la valeur v à l'intervalle allant de min à max, en retournant min si v est inférieure à min,
     max si v est supérieure à max,et v sinon lève IllegalArgumentException si min est (strictement) supérieur à max
     * @param min le minimum
     * @param v la valeur à limiter
     * @param max le maximum
     * @return min si v est inférieure à min, max si v est supérieure à max,et v sinon,
     * @throws  IllegalArgumentException si min est (strictement) supérieur à max
     */
    public static int clamp(int min, int v, int max) {
        Preconditions.checkArgument(!(min>max));
        return (v<min) ? min : Math.min(v,max);
    }



    /**
     * Cette méthode calcule le sinus hyperbolique réciproque
     * @param x la valeur qu'on souhaite calculer son sinus hyperbolique réciproque
     * @return le sinus hyperbolique réciproque de son argument x
     */

    public static double asinh(double x){
        double root = Math.sqrt(1+Math.pow(x,2));
        return Math.log(x+root);
    }


    /**
     * Le constructeur par défaut prive de Math2
     */
    private Math2(){}


}
