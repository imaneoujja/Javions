package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

/**
 * La classe AdsbDemodulator publique et finale représente un démodulateur de messaes ADSB.
 *
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */
public final class AdsbDemodulator {

    private static final int WINDOW_SIZE = 1200;
    private static final int BYTE_SIZE = 8;
    private static final int MSG_LENGTH = 14;
    private static final int PREAMBULE_SIZE = 80;
    private static final int STEP = 10;

    private final PowerWindow powerWindow;
    private final byte[] msg = new byte[MSG_LENGTH];


    /**
     * Le constructeur retourne un démodulateur obtenant les octets contenant les échantillons du flot passé en argument
     *
     * @param samplesStream le flot
     * @throws IOException si une erreur d'entrée/sortie se produit.
     */
    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        powerWindow = new PowerWindow(samplesStream, WINDOW_SIZE);
    }


    /**
     * Méthode retournant le prochain message ADS-B du flot d'échantillons passé au constructeur ou null s'il n'y en a plus.
     *
     * @return le prochain message ADS-B du flot d'échantillons passé au constructeur
     * @throws IOException en cas d'erreur d'entrée/sortie.
     */
    public RawMessage nextMessage() throws IOException {
        RawMessage mess = null;
        while (powerWindow.isFull()) {
            int psumRight = powerWindow.get(2) + powerWindow.get(12) + powerWindow.get(37) + powerWindow.get(47);
            int psum = powerWindow.get(1) + powerWindow.get(11) + powerWindow.get(36) + powerWindow.get(46);
            int psumLeft = powerWindow.get(0) + powerWindow.get(10) + powerWindow.get(35) + powerWindow.get(45);
            int vsum = powerWindow.get(5) + powerWindow.get(15) + powerWindow.get(20) + powerWindow.get(25) + powerWindow.get(30) + powerWindow.get(40);

            if (!(psum >= 2 * vsum && psum > psumRight && psumLeft < psum)) {
                powerWindow.advance();
                continue;
            }
            powerWindow.advance();
            for (int i = 0; i < MSG_LENGTH; i++) {
                byte b = 0;
                for (int j = 0; j < BYTE_SIZE; j++) {
                    if (powerWindow.get(PREAMBULE_SIZE + STEP * (i * BYTE_SIZE + j)) < powerWindow.get(PREAMBULE_SIZE + 5 + STEP * (i * BYTE_SIZE + j))) {
                        b = (byte) (b << 1);
                    } else {
                        b = (byte) ((b << 1) | 1);
                    }
                }
                msg[i] = b;
            }
            long time = (powerWindow.position() * 100);
            if (RawMessage.of(time, msg) != null) {
                powerWindow.advanceBy(WINDOW_SIZE);
                return RawMessage.of(time, msg);
            }
        }
        return null;
    }
}

