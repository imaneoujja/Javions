package ch.epfl.javions;

import static ch.epfl.javions.Math2.asinh;
import static ch.epfl.javions.Units.Angle.TURN;
import static ch.epfl.javions.Units.convertTo;


/**
 * La classe WebMercator contient des méthodes permettant de projeter des coordonnées géographiques selon la projection WebMercator
 *
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */

public final class WebMercator {
    /**
     * Cette méthode est publique et non instanciable
     *
     * @param zoomLevel niveau de zoom
     * @param longitude la longitude donnée en radians
     * @return la coordonnée x correspondant à la longitude au niveau de zoom donné
     */
    public static double x(int zoomLevel, double longitude) {
        double zl0 = Math.pow(2, 8 + zoomLevel);
        double longi = (convertTo(longitude, TURN) + 0.5);
        return zl0 * longi;
    }


    /**
     * Cette méthode est publique et non instanciable
     *
     * @param zoomLevel niveau de zoom
     * @param latitude  la latitude donnée en radians
     * @return la coordonnée y correspondant à la latitude au niveau de zoom donné
     */
    public static double y(int zoomLevel, double latitude) {
        double zl1 = Math.pow(2, 8 + zoomLevel);
        double lat = (convertTo(-asinh(Math.tan((latitude))), TURN) + 0.5);
        return zl1 * lat;
    }

}
