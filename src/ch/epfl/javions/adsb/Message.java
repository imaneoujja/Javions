package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * L'interface Message publique, a pour but d'être implémentée par toutes les classes représentant des messages ADS-B «analysés».
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */
public interface Message {

    /**
     * Retourne l'horodatage du message, en nanosecondes.
     * @return l'horodatage du message, en nanosecondes
     */

    public abstract long timeStampNs();




    /**
     * Retourne l'adresse OACI de l'expéditeur du message.
     * @return l'adresse OACI de l'expéditeur du message
     */

    public abstract IcaoAddress icaoAddress();
}