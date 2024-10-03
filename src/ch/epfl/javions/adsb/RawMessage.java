package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.HexFormat;

/**
 * L'enregistrement RawMessage du sous-paquetage adsb, public, représente un message ADS-B.
 *
 * @param timeStampNs l'horodatage du message, exprimé en nanosecondes.
 * @param bytes       les octets du message.
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */

public record RawMessage(long timeStampNs, ByteString bytes) {
    public final static int LENGTH = 14;


    /**
     * Méthode retournant le message ADS-B brut.
     *
     * @param timeStampNs l'horodotage.
     * @param bytes       tableau de bytes.
     * @return retourne le message ADS-B brut avec l'horodatage et les octets donnés,
     * ou null si le CRC24 des octets ne vaut pas 0
     */
    public static RawMessage of(long timeStampNs, byte[] bytes) {
        return (crc24.crc(bytes) == 0) ? new RawMessage(timeStampNs, new ByteString(bytes)) : null;

    }

    /**
     * Méthode retournant retourne la taille d'un message dont le premier octet est celui donné, et qui vaut LENGTH si l'attribut DF contenu dans ce premier octet vaut 17,
     * et 0 sinon , indiquant que le message n'est pas d'un type connu.
     *
     * @param byte0 le premier octet du message.
     * @return la taille du message dont le premier octet est donné en argument.
     */

    public static int size(byte byte0) {
        int DF = Bits.extractUInt(byte0, START, SIZE);
        return (DF == FORMAT) ? LENGTH : 0;
    }

    /**
     * @param payload l'attribut ME.
     * @return le code de type de l'attribut ME passé en argument.
     */
    public static int typeCode(long payload) {
        return Bits.extractUInt(payload, START_EXTRACT, SIZE);
    }


    private final static Crc24 crc24 = new Crc24(Crc24.GENERATOR);
    private static final int SIZE = 5;
    private static final int START_EXTRACT = 51;
    private static final int START = 3;
    private static final int FORMAT = 17;
    private static final int START_DF = 0;
    private static final int START_ICAO = 1;
    private static final int START_ME = 4;
    private static final int START_CRC = 11;
    private static final int DIGITS = 6;

    /**
     * Le constructeur compact de RawMessage lève IllegalArgumentException si l'horodatage est (strictement) négatif,
     * ou si la chaîne d'octets ne contient pas LENGTH octets.
     */
    public RawMessage {
        Preconditions.checkArgument((timeStampNs >= 0) && (bytes.size() == LENGTH));
    }

    /**
     * @return le format du message, c.-à-d. l'attribut DF stocké dans son premier octet
     */
    public int downLinkFormat() {
        return Bits.extractUInt(bytes.bytesInRange(START_DF, START_ICAO), START, SIZE);
    }

    /**
     * @return l'adresse OACI de l'expéditeur du message.
     */
    public IcaoAddress icaoAddress() {
        return new IcaoAddress(HexFormat.of().withUpperCase().toHexDigits(bytes.bytesInRange(START_ICAO, START_ME), DIGITS));
    }


    /**
     * @return l'attribut ME du message — sa « charge utile ».
     */
    public long payload() {
        return bytes.bytesInRange(START_ME, START_CRC);
    }

    /**
     * @return le code de type du message : les cinq bits de poids le plus fort de son attribut ME.
     */
    public int typeCode() {
        return Bits.extractUInt(payload(), START_EXTRACT, SIZE);
    }

}


