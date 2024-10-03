package ch.epfl.javions.aircraft;

import java.io.*;
import java.net.URLDecoder;
import java.util.Objects;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * La classe AircraftDatabase publique et finale représente la base de données mictronics des aéronefs.
 *
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */

public final class AircraftDatabase {

    private static final int BEGIN_INDEX = 4;
    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final int FOUR = 4;
    private static final int FIVE = 5;
    private static final int LIMIT = -2;
    private final String fileName;


    /**
     * Le constructeur public de AircraftDatabase
     *
     * @param fileName
     * @return un objet représentant la base de données mictronics, stockée dans le fichier de nom donné
     */
    public AircraftDatabase(String fileName) {
        this.fileName = Objects.requireNonNull(fileName);
    }


    /**
     * Cette méthode lit les données de la base de données
     *
     * @param address l'adresse OACI
     * @return les données de l'aéronef dont l'adresse OACI est celle donnée en argument.
     * @throws IOException en cas d'erreur d'entrée/sortie
     */
    public AircraftData get(IcaoAddress address) throws IOException {
        Objects.requireNonNull(address);
        String d = getClass().getResource("/aircraft.zip").getFile();
        d = URLDecoder.decode(d, UTF_8);
        try (ZipFile fichier = new ZipFile(d);
             InputStream flotEntree = fichier.getInputStream(fichier.getEntry((address.string()).substring(BEGIN_INDEX) + ".csv"));
             Reader reader = new InputStreamReader(flotEntree, UTF_8);
             BufferedReader buffer = new BufferedReader(reader)) {
            String l = "";
            while ((l = buffer.readLine()) != null) {
                String[] columns = l.split(",", LIMIT);
                if (l.startsWith(address.string())) {
                    AircraftRegistration registration = new AircraftRegistration(columns[ONE]);
                    AircraftTypeDesignator typeDesignator = new AircraftTypeDesignator(columns[TWO]);
                    AircraftDescription description = new AircraftDescription(columns[FOUR]);
                    WakeTurbulenceCategory category = WakeTurbulenceCategory.of(columns[FIVE]);
                    AircraftData data = new AircraftData(registration, typeDesignator, columns[THREE], description, category);
                    return data;
                } else if (columns[ZERO].compareTo(address.getString()) > 0) {
                    return null;
                }
            }
            return null;
        }

    }

}








