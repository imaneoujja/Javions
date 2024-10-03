package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

import static ch.epfl.javions.Bits.extractUInt;
import static ch.epfl.javions.Units.Length.FOOT;
import static ch.epfl.javions.Units.Length.METER;

/**
 * Enregistrement Messages de positionnement implémentant l'interface Message
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */
public record AirbornePositionMessage (long timeStampNs, IcaoAddress icaoAddress, double altitude, int parity, double x, double y) implements Message {

    private static final int SIZE_L = 17;
    private static final double POWER = Math.pow(2, -SIZE_L);
    private static final int ZERO = 0;
    private static final int UN = 1;
    private static final int START_ALT = 36;
    private static final int SIZE_ALT = 12 ;
    private static final int START_F = 34 ;
    private static final int INDEX =4;
    private static final int PIED1 =25;
    private static final int PIED2 =1000;
    private static final int START_A = 5;
    private static final int SIZE_A = 7 ;
    private static final int START =11;
    private static final int BIT_SIZE= 1 ;
    private static final int D = 4;
    private static final int A = 10;
    private static final int B = 5;
    private static final int C = 11;




    /**
     *
     * @param timeStampNs l'horodatage du message, en nanosecondes.
     * @param icaoAddress l'adresse OACI de l'expéditeur du message.
     * @param altitude l'altitude à laquelle se trouvait l'aéronef au moment de l'envoi du message, en mètres.
     * @param parity la parité du message (0 s'il est pair, 1 s'il est impair).
     * @param x la longitude locale et normalisée donc comprise entre 0 et 1 à laquelle se
     *          trouvait l'aéronef au moment de l'envoi du message,
     * @param y la latitude locale et normalisée à laquelle se trouvait l'aéronef au moment
     *          de l'envoi du message
     * @throws NullPointerException si l'adresse OACI est nulle
     * @throws IllegalArgumentException si l'horodotage est strictement inférieure à 0, ou parity est différent de 0 ou 1
     *  ou x ou y ne sont pas compris entre 0 (inclus) et 1 (exclu)
     */
    public AirbornePositionMessage {
        Objects.requireNonNull(icaoAddress);
        Preconditions.checkArgument(timeStampNs>=0 && (parity==0 || parity==1)&& (x>=0 && x<1)&&(y>=0 && y<1));
    }


    /**
     * Méthode retournant le message de positionnement en vol correspondant au message brut donné,
     * ou null si l'altitude qu'il contient est invalide
     * @param rawMessage le message brut.
     * @return le message de positionnement en vol correspondant au message brut donné
     */
    public static AirbornePositionMessage of(RawMessage rawMessage) {
        double altitude = 0;
        int parite = extractUInt(rawMessage.payload(), START_F, UN);
        double x = extractUInt(rawMessage.payload(), ZERO, SIZE_L) * POWER;
        double y = extractUInt(rawMessage.payload(), SIZE_L, SIZE_L) * POWER;
        int ALT = extractUInt(rawMessage.payload(), START_ALT, SIZE_ALT);
        if (Bits.testBit(ALT, INDEX)) {
            int alt = (extractUInt(ALT,START_A , SIZE_A)) << INDEX | extractUInt(ALT, ZERO, INDEX);
            altitude = -PIED2 + alt * PIED1;


        } else if (!(Bits.testBit(ALT, INDEX))) {
            int D4 = extractUInt(ALT, 0, 1);
            int B4 = extractUInt(ALT, 1, 1);
            int D2 = extractUInt(ALT, 2, 1);
            int B2 = extractUInt(ALT, 3, 1);
            int D1 = extractUInt(ALT, 4, 1);
            int B1 = extractUInt(ALT, 5, 1);
            int A4 = extractUInt(ALT, 6, 1);
            int C4 = extractUInt(ALT, 7, 1);
            int A2 = extractUInt(ALT, 8, 1);
            int C2 = extractUInt(ALT, 9, 1);
            int A1 = extractUInt(ALT, 10, 1);
            int C1 = extractUInt(ALT, 11, 1);

            int ALTDemele = (D1 << 11) | (D2 << 10) | (D4 << 9) | (A1 << 8) | (A2 << 7) | (A4 << 6) | (B1 << 5) | (B2 << 4) | (B4 << 3) | (C1 << 2) | (C2 << 1) | C4;
            int groupe1 = decodeGray(extractUInt(ALTDemele, ZERO, 3));
            int groupe2 = decodeGray(extractUInt(ALTDemele, 3, 9));
            if (groupe1 == 0 || groupe1 == 5 || groupe1 == 6) {
                altitude = Double.NaN;
            } else {
                if (groupe1 == 7) {
                    groupe1 = 5;
                }
                if (groupe2 % 2 != 0) {
                    groupe1 = 6 - groupe1;
                }

                altitude = -1300 + 100 * groupe1 + 500 * groupe2;
            }

        }
        if (Double.isNaN(altitude)) {
            return null;
        } else {
            altitude = Units.convert(altitude, FOOT, METER);
            return new AirbornePositionMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), altitude, parite, x, y);
        }
    }
    //Cette méthode a pour but le démêlage, consiste à permuter l'ordre des bits afin de faciliter leur interprétation ultérieure
    private static int ALTdem(int ALT){
        int j=1;
        int Alt = (Bits.extractUInt(ALT,D,1)<<START) |
                (Bits.extractUInt(ALT,D-2,BIT_SIZE)<<START-j) |
                (Bits.extractUInt(ALT,D-4,BIT_SIZE)<<START-j-1) |
                (Bits.extractUInt(ALT,A,BIT_SIZE)<<START-j-2) |
                (Bits.extractUInt(ALT,A-2,BIT_SIZE)<<START-j-3) |
                (Bits.extractUInt(ALT,A-4,BIT_SIZE)<<START-j-4) |
                (Bits.extractUInt(ALT,B,BIT_SIZE)<<START-j-5) |
                (Bits.extractUInt(ALT,B-2,BIT_SIZE)<<START-j-6) |
                (Bits.extractUInt(ALT,B-4,BIT_SIZE)<<START-j-7) |
                (Bits.extractUInt(ALT,C,BIT_SIZE)<<START-j-8) |
                (Bits.extractUInt(ALT,C-2,BIT_SIZE)<<START-j-9) |
                Bits.extractUInt(ALT,C-4,BIT_SIZE);
        return Alt;
    }




    //Cette méthode prend comme un argment un nombre représentés par le code Gray et les convertit à la representation binaire
    private static int decodeGray(int numberGray){
        int numberBinaire =numberGray;
        for(int k=8 ; k>=1; k=k/2){
            numberBinaire= numberBinaire ^ (numberBinaire>>k);
        }
        return numberBinaire;
    }
}



