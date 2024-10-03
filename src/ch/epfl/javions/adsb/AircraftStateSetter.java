package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

/**
 * L'interface AircraftStateSetter publique, a pour but d'être implémentée par toutes les classes représentant
 * l'état modifiable d'un aéronef.
 *
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */
public interface AircraftStateSetter {


    /**
     * Cette méthode change l'horodatage du dernier message reçu de l'aéronef à la valeur donnée.
     *
     * @param timeStampNs l'horodotage du message
     */
    abstract void setLastMessageTimeStampNs(long timeStampNs);


    /**
     * Cette méthode change la catégorie de l'aéronef à la valeur donnée .
     *
     * @param category la catégorie d'un aéronef.
     */
    abstract void setCategory(int category);


    /**
     * Cette méthode change l'indicatif de l'aéronef à la valeur donnée.
     *
     * @param callSign l'indicatif de l'aéronef.
     */
    abstract void setCallSign(CallSign callSign);


    /**
     * Cette méthode change la position de l'aéronef à la valeur donnée.
     *
     * @param position la position de l'aéronef.
     */
    abstract void setPosition(GeoPos position);


    /**
     * Cette méthode change l'altitude de l'aéronef à la valeur donnée.
     *
     * @param altitude l'altitude de l'aéronef.
     */
    abstract void setAltitude(double altitude);


    /**
     * Cette méthode change la vitesse de l'aéronef à la valeur donnée.
     *
     * @param velocity la vitesse de l'aéronef.
     */
    abstract void setVelocity(double velocity);


    /**
     * Cette méthode change la direction de l'aéronef à la valeur donnée.
     *
     * @param trackOrHeading la direction de l'aéronef.
     */
    abstract void setTrackOrHeading(double trackOrHeading);


}