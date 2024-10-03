package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 * La classe PowerComputer publique et finale, représente un calculateur de puissance
 *
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */
public final class PowerComputer {
    private final int batchSize;
    private final SamplesDecoder Sd;
    private final short[] tableau;
    private final short[] tab;

    /**
     * Le constructeur de PowerComputer publique et finale représente un calculateur de puissance
     *
     * @param stream    flot d'entrée
     * @param batchSize la taille des lots
     * @throws IllegalArgumentException si la taille des lots est invalide
     */
    public PowerComputer(InputStream stream, int batchSize) {
        Preconditions.checkArgument(batchSize > 0 && batchSize % 8 == 0);
        this.batchSize = batchSize;
        this.tableau = new short[2 * batchSize];
        tab = new short[8];
        Sd = new SamplesDecoder(stream, 2 * batchSize);
    }


    /**
     * Cette méthode publique lit depuis le décodeur d'échantillons le nombre d'échantillons nécessaire a, puis calcule les puissances et les place dans le tableau passé en argument
     *
     * @param batch lot
     * @return le nombre d'échantillons de puissance placés dans le tableau,
     * @throws IOException              en cas d'erreur d'entrée/sortie
     * @throws IllegalArgumentException si la taille du tableau passé en argument n'est pas égale à la taille d'un lot
     */

    public int readBatch(int[] batch) throws IOException {
        Preconditions.checkArgument(batch.length == batchSize);
        {
        }
        int compteur = Sd.readBatch(tableau);
        int pos = 0;
        int j = 0;
        for (int i = 0; i < tableau.length - 1; i = i + 2) {
            if (compteur > 0) {
                tab[pos + 1] = tableau[i];
                tab[pos] = tableau[i + 1];
                int i1 = tab[1] - tab[3] + tab[5] - tab[7];
                int i2 = tab[0] - tab[2] + tab[4] - tab[6];
                int power = (i1 * i1) + (i2 * i2);
                batch[j] = power;
                j++;
                compteur--;
                pos = (pos + 2) % tab.length;
            }
        }
        return j;
    }
}





