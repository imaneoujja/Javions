package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;
import java.util.Objects;

import static ch.epfl.javions.Bits.extractUInt;
import static ch.epfl.javions.Units.Angle.*;
import static ch.epfl.javions.Units.Speed.KNOT;
import static ch.epfl.javions.Units.Speed.METRE_PER_SECOND;
import static ch.epfl.javions.Units.convert;
import static java.lang.Math.atan2;
import static java.lang.Math.hypot;

/**
 * @param timeStampNs    l'horodatage du message, en nanosecondes.
 * @param icaoAddress    l'adresse OACI de l'expéditeur du message.
 * @param speed          la vitesse de l'aéronef, en m/s.
 * @param trackOrHeading la direction de déplacement de l'aéronef, en radians.
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 * L'enregistrement AirborneVelocityMessage public, représente un message de vitesse en vol.
 * Il implémente l'interface Message et ses attributs sont :
 */
public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress, double speed,
                                      double trackOrHeading) implements Message {

    private static final int START_ST = 48;
    private static final int SIZE_ST = 3;
    private static final int START_22 = 21;
    private static final int SIZE_22 = 22;
    private static final int SIZE_V = 10;
    private static final int SIZE_D = 1;
    private static final int START_V = 11;
    private static final int[] TYPECODE = {1, 2, 3, 4};
    private static int VNS;
    private static int DNS;
    private static int VEW;
    private static int DEW;
    private static int SH;
    private static int HDG;
    private static int AS;
    private static double vitesse = 0;
    private static double direction = 0;


    /**
     * Le constructeur compact lève :
     * NullPointerException si icaoAddress est nul.
     * IllegalArgumentException si timeStampNs, speed ou trackOrHeading sont strictement négatifs.
     */
    public AirborneVelocityMessage {
        Objects.requireNonNull(icaoAddress);
        Preconditions.checkArgument((timeStampNs >= 0) && speed >= 0 && trackOrHeading >= 0);
    }

    /**
     * Méthode retournant le message de vitesse en vol correspondant au message brut donné,
     * ou null si le sous-type est invalide,
     * ou si la vitesse ou la direction de déplacement ne peuvent pas être déterminés.
     *
     * @param rawMessage le message brut
     * @return le message de vitesse en vol correspondant au message brut donné.
     */
    public static AirborneVelocityMessage of(RawMessage rawMessage) {
        int sousType = extractUInt(rawMessage.payload(), START_ST, SIZE_ST);
        int bits22 = extractUInt(rawMessage.payload(), START_22, SIZE_22);
        if (sousType != TYPECODE[0] && sousType != TYPECODE[1] && sousType != TYPECODE[2] && sousType != TYPECODE[3]) {
            return null;
        }
        if (sousType == TYPECODE[0] || sousType == TYPECODE[1]) {
            groundDisplacement(bits22);
            if (VEW == -1 || VNS == -1) {
                return null;
            }if (DNS == 1) {
                VNS = -VNS;
            }if (DEW == 1) {
                VEW = -VEW;
            }
            groundCalcul(sousType);
        } else {
            airDisplacement(bits22);
            if (SH == 1) {
                double capTours = HDG / Math.pow(2, 10); //direction
                direction = convert(capTours, TURN, RADIAN);
            }
            if (SH == 0 || AS == -1) {
                return null; //impossible de determiner la direction
            }
            vitesse = (sousType == TYPECODE[2]) ? convert(AS, KNOT, METRE_PER_SECOND) : (convert(AS, KNOT, METRE_PER_SECOND)) * 4;

        }
        return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), vitesse, direction);
    }

    //permet de trouver les attributs du déplacement par rapport au sol.
    private static void groundDisplacement(int b) {
        VNS = (extractUInt(b, 0, SIZE_V)) - 1;
        DNS = extractUInt(b, SIZE_V, SIZE_D);
        VEW = (extractUInt(b, START_V, SIZE_V)) - 1;
        DEW = extractUInt(b, START_22, SIZE_D);
    }

    //permet de trouver les attributs du déplacement dans l'air.
    private static void airDisplacement(int b) {
        SH = extractUInt(b, START_22, SIZE_D);
        HDG = extractUInt(b, START_V, SIZE_V);
        AS = (extractUInt(b, 0, SIZE_V)) - 1;
    }


    // permet de calculer la vitesse et la direction du déplacement au sol.
    private static void groundCalcul(int sousType){
        vitesse = hypot(VNS, VEW);
        direction = atan2(VEW, VNS); //en radian
        if (direction < 0) {
            direction += (2 * Math.PI);
        }
        if (sousType == TYPECODE[0]) {
            vitesse = convert(vitesse, KNOT, METRE_PER_SECOND);
        } else {
            vitesse = (convert(vitesse, KNOT, METRE_PER_SECOND)) * 4;
        }
    }

}
