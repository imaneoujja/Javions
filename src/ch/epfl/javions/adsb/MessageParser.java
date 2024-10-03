package ch.epfl.javions.adsb;

/**
 * La classe MessageParser, publique et non instantiable, a pour but de transformer les messages ADS-B bruts en
 * messages d'un des trois types décrits précédemment identification, position en vol, vitesse en vol.
 *
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */
public class MessageParser {

    private static final int CODE_TYPE_ID1 = 1;
    private static final int CODE_TYPE_ID2 = 2;
    private static final int CODE_TYPE_ID3 = 3;
    private static final int CODE_TYPE_ID4 = 4;
    private static final int CODE_TYPE_POS1 = 9;
    private static final int CODE_TYPE_POS2 = 18;
    private static final int CODE_TYPE_POS3 = 20;
    private static final int CODE_TYPE_POS4 = 22;

    private static final int CODE_TYPE_VELOCITY = 19;


    /**
     * Constructeur privé de la classe non instantiable.
     */
    private MessageParser() {
    }

    /**
     * Méthode publique et statique retournant l'instance de AircraftIdentificationMessage,
     * de AirbornePositionMessage ou de AirborneVelocityMessage correspondant au message brut donné,
     * ou null si le code de type de ce dernier ne correspond à aucun de ces trois types de messages,
     * ou s'il est invalide.
     *
     * @param rawMessage le message brut.
     * @return l'instance d'un des trois types de messages correspondant au message brut donné.
     */
    public static Message parse(RawMessage rawMessage) {

        if (rawMessage.typeCode() == CODE_TYPE_ID1 || rawMessage.typeCode() == CODE_TYPE_ID2 ||
                rawMessage.typeCode() == CODE_TYPE_ID3 || rawMessage.typeCode() == CODE_TYPE_ID4) {
            return AircraftIdentificationMessage.of(rawMessage);

        } else if ((rawMessage.typeCode() >= CODE_TYPE_POS1 && rawMessage.typeCode() <= CODE_TYPE_POS2) ||
                (rawMessage.typeCode() >= CODE_TYPE_POS3 && rawMessage.typeCode() <= CODE_TYPE_POS4)) {
            return (AirbornePositionMessage.of(rawMessage));

        } else if (rawMessage.typeCode() == CODE_TYPE_VELOCITY) {
            return (AirborneVelocityMessage.of(rawMessage));

        } else return null;
    }

}