package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

import java.util.Objects;




/**
 * La classe AircraftStateAccumulator public représente un accumulateur d'état d'aéronef, un objet accumulant les messages ADS-B provenant d'un seul aéronef afin de déterminer son état au cours du temps.
 *
 * @param <T> paramètre de type de cette classe générique
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */

public class AircraftStateAccumulator<T extends AircraftStateSetter> {
    private final static int PAIR = 0;
    private final static int IMPAIR = 1;
    private final static long BOUND = 10000000000L;
    private final T stateSetter;
    private Message messagePair;
    private Message messageImpair;


    /**
     * Constructeur public de la classe retournant un accumulateur d'état d'aéronef associé à l'état modifiable donne ou lève NullPointerException si celui-ci est nul.
     *
     * @param stateSetter l'état modifiable.
     */
    public AircraftStateAccumulator(T stateSetter) {
        Objects.requireNonNull(stateSetter);
        this.stateSetter = stateSetter;
    }


    /**
     * Méthode publique qui retourne l'état modifiable de l'aéronef passé à son constructeur.
     *
     * @return l'état modifiable de l'aéronef passé au constructeur.
     */
    public T stateSetter() {
        return stateSetter;
    }


    /**
     * Méthode publique qui met à jour l'état modifiable en fonction du message donné
     *
     * @param message le message donné.
     */
    public void update(Message message) {
        stateSetter.setLastMessageTimeStampNs(message.timeStampNs());
        switch (message) {
            case AircraftIdentificationMessage aim -> {
                stateSetter.setCategory(aim.category());
                stateSetter.setCallSign(aim.callSign());
            }

            case AirbornePositionMessage m -> {
                stateSetter.setAltitude(m.altitude());
                int parite = m.parity();

                if (parite == PAIR) {
                    messagePair = m;
                    if (messageImpair == null) {
                        break;
                    }

                    if ((m.timeStampNs() - messageImpair.timeStampNs()) <= BOUND) {
                        GeoPos position = CprDecoder.decodePosition(m.x(), m.y(), ((AirbornePositionMessage) messageImpair).x(), ((AirbornePositionMessage) messageImpair).y(), parite);
                        if (position == null) {
                            break;
                        } else {
                            stateSetter.setPosition(position);
                        }
                    }
                    messagePair = m;

                } else if (parite == IMPAIR) {
                    messageImpair = m;
                    if (messagePair == null) {
                        break;
                    }

                    if ((m.timeStampNs() - messagePair.timeStampNs()) <= BOUND) {
                        GeoPos position = CprDecoder.decodePosition(((AirbornePositionMessage) messagePair).x(), ((AirbornePositionMessage) messagePair).y(), m.x(), m.y(), parite);
                        if (position == null) {
                            break;
                        } else {
                            stateSetter.setPosition(position);
                        }
                    }
                    messageImpair = m;
                }
            }
            case AirborneVelocityMessage m2 -> {
                stateSetter.setVelocity(m2.speed());
                stateSetter.setTrackOrHeading(m2.trackOrHeading());
            }

            default -> throw new Error();  // ce cas ne devrait jamais se produire
        }

    }

}

