package ch.epfl.javions;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

/**
 * La classe ByteString public et finale, représente une séquence d'octets
 *
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */

public final class ByteString {

    /**
     * Cette méthode public et statique construit des chaînes d'octets à partir de leur représentation hexadécimale
     *
     * @param hexString l'écriture hexadécimale
     * @return la chaîne d'octets dont la chaîne passée en argument est la représentation hexadécimale,
     * ou lève une exception NumberFormatException si la chaîne donnée n'est pas de longueur paire, ou si elle contient un caractère qui n'est pas un chiffre hexadécimal
     */
    public static ByteString ofHexadecimalString(String hexString) {
        Preconditions.checkArgument(hexString.length() % 2 == 0);
        HexFormat hf = HexFormat.of().withUpperCase();
        ByteString octets = new ByteString(hf.parseHex(hexString));
        return octets;
    }

    private final byte[] bytes;


    /**
     * Le contructeur public qui retourne une chaîne d'octets dont le contenu est celui du tableau passé en argument
     *
     * @param bytes le tableau d'octets
     */
    public ByteString(byte[] bytes) {
        this.bytes = bytes.clone();
    }


    /**
     * Cette méthode public retourne le nombre d'octets qu'elle contient.
     *
     * @return la taille de la chaîne.
     */
    public int size() {
        return bytes.length;
    }

    /**
     * Cette méthode public interprète l'octet qu'elle retourne comme non signé
     *
     * @param index l'indice donné
     * @return l'octet à l'index donné
     * @throws IndexOutOfBoundsException si celui-ci est invalide
     */
    public int byteAt(int index) {
        Objects.checkIndex(index, bytes.length);
        return Byte.toUnsignedInt(bytes[index]);
    }

    /**
     * Cette méthode public les octets compris entre les index fromIndex (inclus) et toIndex (exclu) sous la forme de long
     * @param fromIndex index de départ
     * @param toIndex index d'arrivée
     * @return les octets compris entre les index fromIndex (inclus) et toIndex (exclu) sous la forme d'une valeur de type long
     * @throws IndexOutOfBoundsException si la plage décrite par fromIndex et toIndex n'est pas totalement comprise entre 0 et la taille de la chaîne
     * @throws IllegalArgumentException  si la différence entre toIndex et fromIndex n'est pas strictement inférieure au nombre d'octets contenus dans une valeur de type long
     */
    public long bytesInRange(int fromIndex, int toIndex) {
        Objects.checkFromToIndex(fromIndex, toIndex, bytes.length);
        Preconditions.checkArgument(toIndex - fromIndex <= Long.BYTES);
        long octets = 0;
        for (int i = fromIndex; i < toIndex; i++) {
            octets = (octets << 8) | Byte.toUnsignedLong(bytes[i]);
        }
        return octets;
    }


    /**
     * Cette méthode public est une redefinition de la méthode equals
     *
     * @param objet0 la valeur qu'on lui donne
     * @return Vrai ssi la valeur qu'on lui passe est aussi une instance de ByteString et que ses octets sont identiques à ceux du récepteur
     * et False sinon
     */
    @Override
    public boolean equals(Object objet0) {
        if (objet0 instanceof ByteString objet1) {
            return Arrays.equals(this.bytes, objet1.bytes);
        }
        return false;
    }


    /**
     * Cette méthode public est une redefinition de la méthode hashCode
     *
     * @return la valeur retournée par la méthode hashCode de Arrays appliquée au tableau contenant les octets
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }


    /**
     * Cette méthode public est une redefinition de la méthode toString
     *
     * @return une représentation des octets de la chaîne en hexadécimal
     */
    @Override
    public String toString() {
        HexFormat hf = HexFormat.of().withUpperCase();
        return hf.formatHex(bytes);
    }

}