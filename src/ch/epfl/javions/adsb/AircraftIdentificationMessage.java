package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;


import java.util.Objects;

import static ch.epfl.javions.Bits.extractUInt;

/**
 * Enregistrement Messages d'identification implémentant l'interface Message
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 * @param timeStampNs l'horodatage du message, en nanosecondes
 * @param icaoAddress l'adresse OACI de l'expéditeur du message
 * @param category la catégorie d'aéronef de l'expéditeur
 * @param callSign l'indicatif de l'expéditeur
 */



public record AircraftIdentificationMessage (long timeStampNs,IcaoAddress icaoAddress, int category,CallSign callSign) implements Message {

    private static final String[] tabLettre = new String[]{"","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    private static final String[] tabChiffre = new String[]{"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","0","1","2","3","4","5","6","7","8","9"} ;


    /**
     * Retourne le message d'identification correspondant au message brut donné,
     * ou null si au moins un des caractères de l'indicatif qu'il contient est invalide
     * @param rawMessage le message brut donné
     * @return  le message d'identification correspondant au message brut donné
     */

    public static AircraftIdentificationMessage of(RawMessage rawMessage){
        StringBuilder builder = new StringBuilder();

        int CA = extractUInt(rawMessage.payload(), 48,3);
        int i = (14 - rawMessage.typeCode(rawMessage.payload()))<<4;
        for(int j=0 ; j<48; j=j+6){
            int msg = extractUInt(rawMessage.payload(), j , 6);
            if((msg < 1 ||( msg > 26 && msg < 48 && msg!= 32) || msg > 57)){
                return null ;
            } else if(msg>=1 && msg<=26){
                builder.append(tabLettre[msg]);
            }else if (msg ==32){
                builder.append("");
            }else if(msg>=48 && msg<=57){
                builder.append(tabChiffre[msg]);
            }
        }
        return new AircraftIdentificationMessage(rawMessage.timeStampNs(),rawMessage.icaoAddress(),i | CA, new  CallSign(builder.reverse().toString()));
    }


    /**
     * Le constructeur de la classe AircraftIdentificationMessage
     * @throws NullPointerException si l'adresse OACI est nulle
     * @throws IllegalArgumentException si l'horodotage est inférieur strictement à 0
     */
    public AircraftIdentificationMessage{
        Objects.requireNonNull(icaoAddress);
        Objects.requireNonNull(callSign);
        Preconditions.checkArgument(timeStampNs>=0);
    }

}