package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 * La classe PowerWindow publique et finale représente une fenêtre de taille fixe sur une séquence d'échantillons
 * de puissance produits par un calculateur de puissance.
 *
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */
public final class PowerWindow {
    private int[] tab1;
    private int[] tab2;
    private final int windowSize;
    private final PowerComputer powerComputer;
    private int positionActuelle;
    private int positionAbsolue;
    private int nbSamples;
    private final int BATCH_SIZE = 1 << 16;


    /**
     * Le constructeur publique retourne une fenêtre de taille donnée sur la séquence d'échantillons de puissance calculés à partir des octets fournis par le flot d'entrée donné
     *
     * @param stream     flot d'entrée
     * @param windowSize la taille de la fenêtre
     * @throws IOException si la taille de la fenêtre donnée est invalide
     */
    public PowerWindow(InputStream stream, int windowSize) throws IOException {

        Preconditions.checkArgument(!(windowSize <= 0 || windowSize > BATCH_SIZE));

        this.windowSize = windowSize;
        this.powerComputer = new PowerComputer(stream, BATCH_SIZE);
        this.positionActuelle = 0;
        this.tab1 = new int[BATCH_SIZE];
        this.tab2 = new int[BATCH_SIZE];
        this.nbSamples = powerComputer.readBatch(tab1);
        positionAbsolue = 0;
    }


    /**
     * @return la taille de la fenêtre
     */
    public int size() {
        return windowSize;
    }


    /**
     * Cette méthode pulbique vaut initialement 0 et est incrémentée à chaque appel à advance
     *
     * @return la position actuelle de la fenêtre par rapport au début du flot de valeurs de puissance
     */
    public long position() {
        return positionAbsolue;
    }


    /**
     * Cette méthode verifie si la fenetre contient autant d'échantillons que sa taille
     *
     * @return retourne vrai ssi la fenêtre est pleine
     */
    public boolean isFull() {
        return (windowSize <= nbSamples);
    }


    /**
     * Cette méthode avance la fenêtre d'un échantillon
     *
     * @throws IOException
     */
    public void advance() throws IOException {
        nbSamples--;
        positionAbsolue++;
        positionActuelle++;
        if ((windowSize + positionActuelle - 1) == BATCH_SIZE) {
            nbSamples += powerComputer.readBatch(tab2);
        } else if ((positionActuelle) == BATCH_SIZE) {
            int[] temp = tab2;
            tab2 = tab1;
            tab1 = temp;
            positionActuelle = 0;
        }


    }


    /**
     * @param i l'indice donné
     * @return l'échantillon de puissance à l'index donné de la fenêtre
     */
    public int get(int i) {
        if (i < 0 || i >= windowSize) {
            throw new IndexOutOfBoundsException();
        }
        int l = (int) (i + positionActuelle);
        return (l < tab1.length) ? tab1[l] : tab2[l - BATCH_SIZE];
    }


    /**
     * Cette méthode avance la fenêtre du nombre d'échantillons donné
     *
     * @param offset nombre d'échantillons
     * @throws IllegalArgumentException si ce nombre n'est pas positif ou nul
     */
    public void advanceBy(int offset) throws IOException {
        Preconditions.checkArgument(offset >= 0);
        for (int i = 0; i < offset; i++) {
            advance();
        }
    }
}
