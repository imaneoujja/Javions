package ch.epfl.javions;

/**
 * La classe Crc24 est publique et finale représente un calculateur de CRC de 24 bits.
 *
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */

public final class Crc24 {
    public static final int GENERATOR = 0xFFF409;
    private static final int TAB_SIZE = 256;
    private static final int BYTE_SIZE = 8;
    private static final int LSB_24 = 24;
    private static final int CRC_START = 0;
    private static final int CRC_ONE = 1;
    private static final int TABLE_START = 16;
    private final int[] table;


    //Méthode qui prend en arguments le générateur et le tableau d'octets dont le CRC24 doit être calculé en le retournant.
    private static int crc_bitwise(int generator, byte[] bytesArray) {
        int crc = CRC_START;
        for (byte value : bytesArray) {
            for (int j = BYTE_SIZE - 1; j >= CRC_START; j--) {
                int b = Bits.extractUInt(Byte.toUnsignedInt(value), j, CRC_ONE);
                if (Bits.testBit(crc, LSB_24 - 1)) {
                    crc = ((crc << CRC_ONE) | b) ^ generator;
                } else {
                    crc = (crc << CRC_ONE) | b;
                }
            }
        }
        for (int k = CRC_START; k < LSB_24; k++) {
            if (Bits.testBit(Bits.extractUInt(crc, LSB_24 - 1, CRC_ONE), CRC_START)) {
                crc = ((crc << CRC_ONE)) ^ generator;
            } else {
                crc = (crc << CRC_ONE);
            }
        }
        return Bits.extractUInt(crc, CRC_START, LSB_24);
    }


    // méthode qui construit un tableau de 256 entrées correspondant à un générateur.
    private static int[] buildTable(int generator) {
        int[] table = new int[TAB_SIZE];
        for (int i = CRC_START; i < TAB_SIZE; i++) {
            byte[] byte_array = new byte[]{(byte) i};
            table[i] = crc_bitwise(generator, byte_array);
        }
        return table;
    }

    /**
     * Le constructeur public retourne un calculateur de CRC24 utilisant le générateur dont les 24
     * bits de poids faible sont ceux du générateur .
     *
     * @param generator le générateur
     */
    public Crc24(int generator) {
        table = buildTable(generator);
    }

    /**
     * Méthode publique retournant le CRC24 du tableau donné
     *
     * @param bytes tableau d'octets
     * @return le CRC24 du tableau donné
     */
    public int crc(byte[] bytes) {
        int crc = CRC_START;
        for (int i = CRC_START; i < bytes.length; i++) {
            crc = ((crc << BYTE_SIZE) | Byte.toUnsignedInt(bytes[i])) ^ table[Bits.extractUInt(crc, TABLE_START, BYTE_SIZE)];
        }
        for (int j = CRC_START; j < LSB_24 / BYTE_SIZE; j++) {
            crc = (crc << BYTE_SIZE) ^ table[Bits.extractUInt(crc, TABLE_START, BYTE_SIZE)];
        }
        return Bits.extractUInt(crc, CRC_START, LSB_24);
    }
}