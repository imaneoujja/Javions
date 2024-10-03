package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Cette classe publique et finale représente un décodeur d'échantillons
 *
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */

public final class SamplesDecoder {
    private final byte[] tableauOctets;
    private final int batchSize;
    private final InputStream stream;
    private final static int BYTE_SIZE = 8;
    private final static int FOUR_BITS = 4;

    private final static int BIAS = 2048;

    private final static int MASK = 0xFF;


    /**
     * Le constructeur de SamplesDecoder retourne un décodeur d'échantillons utilisant le flot d'entrée donné pour obtenir les octets de la radio AirSpy et produisant les échantillons par lots de taille donné
     *
     * @param stream    flot d'entrée
     * @param batchSize taille des lots
     * @throws IllegalArgumentException si la taille des lots n'est pas strictement positive
     * @throws NullPointerException     si le flot est nul
     */
    public SamplesDecoder(InputStream stream, int batchSize) {
        Preconditions.checkArgument(batchSize > 0);
        //Objects.requireNonNull(stream);
        this.stream = stream;
        this.batchSize = batchSize;
        tableauOctets = new byte[batchSize * 2];
    }

    /**
     * Cette méthode lit depuis le flot passé au constructeur le nombre d'octets correspondant à un lot, puis convertit ces octets en échantillons signés, qui sont placés dans le tableau passé en argument
     *
     * @param batch le lot
     * @return le nombre d'échantillons effectivement converti
     * @throws IOException              en cas d'erreur d'entrée/sortie
     * @throws IllegalArgumentException si la taille du tableau passé en argument n'est pas égale à la taille d'un lot
     */

    public int readBatch(short[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);
        int bytesRead = stream.readNBytes(tableauOctets, 0, tableauOctets.length);
        for (int i = 0; i < bytesRead / 2; i++) {
            short a = (short) (((tableauOctets[2 * i + 1] & MASK) << BYTE_SIZE) | (tableauOctets[2 * i] & MASK));
            short echantillon12Bits = (short) ((a << FOUR_BITS) >> FOUR_BITS);
            batch[i] = (short) (echantillon12Bits - BIAS);
        }
        return bytesRead / 2;
    }

}



