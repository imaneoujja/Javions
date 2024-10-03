package ch.epfl.javions.gui;

import ch.epfl.javions.Preconditions;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * La classe TileManager représente un gestionnaire de tuiles OSM.
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */
public final class TileManager {
    public static final float LOAD_FACTOR = 0.75f;
    private static final int CACHE_SIZE = 100;
    public static final String PNG = ".png";
    private final Path diskCachePath;
    private final String tileServer;
    private final Map<TileId, Image> memoryCache;




    /**
     * Son constructeur public
     * @param diskCachePath le chemin d'accès au dossier contenant le cache disque
     * @param tileServer le nom du serveur de tuile
     */
    public TileManager(Path diskCachePath, String tileServer) {
        this.diskCachePath = diskCachePath;
        this.tileServer = tileServer;
        this.memoryCache = new LinkedHashMap<>(CACHE_SIZE, LOAD_FACTOR, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<TileId, Image> eldest) {
                return size() > CACHE_SIZE;}
        };
        try {
            Files.createDirectories(diskCachePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     *
     * @param zoom
     * @param x
     * @param y
     */
    public record TileId (int zoom , int x , int y){
        public TileId {
            Preconditions.checkArgument(isValid(zoom,x,y));
        }
        public static boolean  isValid (int zoom , int x , int y){
            int bound = (1<<zoom)-1;
            return 0<=x && x<=bound && 0<= y && y<=bound;
        }

    }


    /**
     * Cette méthode prend l'identité d'une tuile et retourne son image
     * @param id l'identité d'une tuile
     * @return  son image de type Image de la bibliothèque JavaFX
     * @throws IOException
     */
    public Image imageForTileAt(TileId id) throws IOException {
        Image image = memoryCache.get(id);
        if (image != null) {
            return image;
        }

        Path pathImage = diskCachePath.resolve(String.valueOf(tileServer))
                .resolve(String.valueOf(id.zoom))
                .resolve(String.valueOf(id.x))
                .resolve(id.y + PNG);

        if (Files.exists(pathImage)) {
            byte[] tileBytes = Files.readAllBytes(pathImage);
            return imageInCacheMemory(tileBytes, id);
        }


        URL url = new URL("https://" + tileServer + "/" +
                id.zoom + "/" + id.x + "/" + id.y + PNG);
        URLConnection c = url.openConnection();
        c.setRequestProperty("User-Agent", "Javions");

        Files.createDirectories(pathImage.getParent());

        try (InputStream inputStream = c.getInputStream()) {
            byte[] tileBytes = inputStream.readAllBytes();
            try (OutputStream outputStream = Files.newOutputStream(pathImage)) {
                outputStream.write(tileBytes);
            }
            return imageInCacheMemory(tileBytes, id);
        }
    }

    //Verifie que
    private Image imageInCacheMemory(byte[] tileBytes, TileId id){
        Image image = new Image(new ByteArrayInputStream(tileBytes));
        memoryCache.put(id, image);
        return image;
    }


}