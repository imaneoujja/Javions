package ch.epfl.javions.gui;

import ch.epfl.javions.Preconditions;
import javafx.beans.Observable;
import javafx.beans.property.*;


/**
 * La classe public et finale MapParameters représente les paramètres de la portion de la carte visible dans l'interface graphique
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */
public final class MapParameters {

    private static final int ZOOM_MIN = 6;
    private static final int ZOOM_MAX = 19;
    private final IntegerProperty zoom;
    private final DoubleProperty minX;
    private final DoubleProperty minY;


    /**
     * Le constructeur public de la classe MapParameters
     * @param zoom le niveau de zoom
     * @param minX la coordonnée x du coin haut-gauche de la portion visible de la carte
     * @param minY  la coordonnée y du coin haut-gauche de la portion visible de la carte
     */
    public MapParameters(int zoom, double minX, double minY) {
        Preconditions.checkArgument(ZOOM_MIN <= zoom && zoom <= ZOOM_MAX);
        this.zoom = new SimpleIntegerProperty(zoom);
        this.minX = new SimpleDoubleProperty(minX);
        this.minY = new SimpleDoubleProperty(minY);

    }


    /**
     * Un getter public de la coordonnée x du coin haut-gauche de la portion visible de la carte
     * @return coordonnée x du coin haut-gauche de la portion visible de la carte
     */
    public double getMinX() {
        return minX.get();
    }


    /**
     * Un getter public de la coordonnée y du coin haut-gauche de la portion visible de la carte
     * @return  coordonnée y du coin haut-gauche de la portion visible de la carte
     */
    public double getMinY() {
        return minY.get();
    }


    /**
     * Un getter public du niveau de zoom
     * @return niveau de zoom
     */
    public int getZoom() {
        return zoom.get();
    }


    /**
     * Cette méthode public ajoute la difference de niveau de zoom au niveau de zoom actuel, en garantissant toutefois qu'il reste dans les limites susmentionnées
     * @param dZoom la différence de niveau de zoom
     */
    public void changeZoomLevel(int dZoom) {
        int newZoom = zoom.get() + dZoom;
        if (newZoom < ZOOM_MIN) {
            setZoom(ZOOM_MIN);
        } else if (newZoom > ZOOM_MAX) {
            setZoom(ZOOM_MAX);
        } else {
            setZoom(newZoom);
            setMinX(getMinX() * Math.pow(2, dZoom));
            setMinY(getMinY() * Math.pow(2, dZoom));
        }
    }


    /**
     * Cette méthode public translate le coin haut-gauche de la portion de carte affichée d'un vecteur
     * @param x la coordonnée x du coin haut-gauche d'un vecteur
     * @param y la coordonnée y du coin haut-gauche d'un vecteur
     */
    public void scroll(double x, double y) {
        setMinX(minX.get() - x);
        setMinY(minY.get() - y);
    }


    /**
     * Un setter public de la coordonnée x du coin haut-gauche de la portion visible de la carte
     * @param newMinX nouvelle coordonnée x du coin haut-gauche de la portion visible de la carte
     */
    public void setMinX(double newMinX) {
        minX.set(newMinX);
    }


    /**
     * Un setter public du niveau de zoom
     * @param newZoom nouveau niveau de zoom
     */
    public void setZoom(int newZoom){zoom.set(newZoom);}


    /**
     * Un setter public de la coordonnée y du coin haut-gauche de la portion visible de la carte
     * @param newMinY nouvelle coordonnée y du coin haut-gauche de la portion visible de la carte
     */
    public void setMinY(double newMinY) {
        minY.set(newMinY);
    }


    /**
     * Cette méthode public est une propriété en lecture seule, qui permet d'observer les changements de la valeur de zoom de sans pouvoir la modifier
     * @return la propriété de l'attribut zoom qui représente le niveau de zoom
     */

    public ReadOnlyIntegerProperty zoom(){return zoom;}

    /**
     * Cette méthode public est une propriété en lecture seule, qui permet d'observer les changements de la valeur de minX de sans pouvoir la modifier
     * @return la propriété de l'attribut minX qui représente la coordonnée x
     */
    public ReadOnlyDoubleProperty minX(){return minX;}

    /**
     * Cette méthode public est une propriété en lecture seule, qui permet d'observer les changements de la valeur minY de sans pouvoir la modifier
     * @return la propriété de l'attribut minY qui représente la coordonnée y
     */
    public ReadOnlyDoubleProperty minY(){return minY;}


}