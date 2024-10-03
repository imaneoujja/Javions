package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.demodulation.AdsbDemodulator;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.Collections.unmodifiableList;
import static javafx.beans.binding.Bindings.createObjectBinding;


/**
 * La classe publique et finale contient le programme principal, elle représente une application JavaFX.
 *
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */

public final class Main extends Application {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    public static final int CONVERSION = 1000000;
    public static final String JAVIONS = "Javions";
    public static final String PATH = "tile-cache";
    public static final String TILE_SERVER = "tile.openstreetmap.org";
    public static final String RESOURCE = "/aircraft.zip";
    public static final int ZOOM = 8;
    public static final int MIN_X = 33_530;
    public static final int MIN_Y = 23_070;
    public static final int SECOND = 1_000_000_000;
    private final ConcurrentLinkedQueue<RawMessage> messageQueue = new ConcurrentLinkedQueue<>();
    private final StatusLineController lineController = new StatusLineController();
    private long counter = 0;
    private long purge;


    /**
     * La méthode main appelle la méthode launch en lui passant son argument comme paramètre.
     *
     * @param args les arguments fournis sous la forme d'un tableau de chaînes de caractères.
     */
    public static void main(String[] args) {
        launch(args);
    }


    /**
     * @param primaryStage démarre l'application en construisant le graphe de scène correspondant à
     *                     l'interface graphique, démarrant le fil d'exécution chargé d'obtenir les messages,
     *                     et enfin démarrant le minuteur chargé de mettre à jour les états d'aéronefs en
     *                     fonction des messages reçus.
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Path tileCache = Path.of(PATH);
        TileManager tileManager = new TileManager(tileCache, TILE_SERVER);
        MapParameters map = new MapParameters(ZOOM, MIN_X, MIN_Y);
        BaseMapController baseMapController = new BaseMapController(tileManager, map);

        Label aircraftCountLabel = new Label();
        IntegerProperty aircraftCountProperty = new SimpleIntegerProperty();
        aircraftCountLabel.textProperty().bind(Bindings.convert(aircraftCountProperty));

        //Obtention de la base de données
        URL u = getClass().getResource(RESOURCE);
        assert u != null;
        Path p = Path.of(u.toURI());
        AircraftDatabase database = new AircraftDatabase(p.toString());

        AircraftStateManager aircraftStateManager = new AircraftStateManager(database);
        ObjectProperty<ObservableAircraftState> property = new SimpleObjectProperty<>();
        AircraftController aircraftController = new AircraftController(map, aircraftStateManager.states(), property);
        AircraftTableController aircraftTable = new AircraftTableController(aircraftStateManager.states(), property);

        // panneau superposant la vue des aéronefs au fond de carte.
        StackPane mapAndPlanes = new StackPane(baseMapController.pane(), aircraftController.pane());

        // panneau dont la zone centrale (center) est occupée par la table des aéronefs et la zone supérieure (top)
        // par la ligne d'état.
        BorderPane tableAndStatusLine = new BorderPane();
        tableAndStatusLine.setCenter(aircraftTable.getTableView());
        tableAndStatusLine.setTop(lineController.getPane());

        // panneau possédant les deux panneaux d'avant comme deux fils.
        SplitPane splitPane = new SplitPane(mapAndPlanes, tableAndStatusLine);
        splitPane.setOrientation(Orientation.VERTICAL);

        lineController.aircraftCountProperty().bind(createObjectBinding(() -> aircraftStateManager.states().size(),
                aircraftStateManager.states()));
        //aircraftTable.setOnDoubleClick(position -> baseMapController.centerOn(position.getPosition()));

        //fil chargé d'obtenir les messages provenant des aéronefs.
        Thread messageThread;
        if (getParameters().getRaw().isEmpty()) {
            //soit en démodulant le signal radio.
            messageThread = threadFromDemodulator();
        } else {
            //soit en lisant les messages depuis un fichier.
            messageThread = threadFromFile();
        }
        messageThread.setDaemon(true);
        messageThread.start();

        // Animation des aéronefs
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    while (!messageQueue.isEmpty()) {
                        Message m = MessageParser.parse(messageQueue.remove());
                        if (m != null) aircraftStateManager.updateWithMessage(m);
                    }
                    lineController.messageCountProperty().set(++counter);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
                if (now - purge >= SECOND) {
                    aircraftStateManager.purge();
                    purge = now;

                }
            }
        }.start();

        // fenêtre principale.
        Scene scene = new Scene(splitPane);
        primaryStage.setTitle(JAVIONS);
        primaryStage.setMinWidth(WIDTH);
        primaryStage.setMinHeight(HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    // la méthode lit la totalité des messages du fichier binaire fourni, et les retourne dans une liste
    private List<RawMessage> readAllMessages(String fileName)
            throws IOException {
        List<RawMessage> allMessages = new LinkedList<>();
        try (DataInputStream s = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)))) {
            byte[] bytes = new byte[RawMessage.LENGTH];
            while (true) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);
                Message parsedMessage = MessageParser.parse(new RawMessage(timeStampNs, message));
                if (parsedMessage != null) {
                    allMessages.add(new RawMessage(timeStampNs, message));
                }
            }
        } catch (EOFException e) {
        }
        return unmodifiableList(allMessages);
    }

    //Methode retournant un thread en lisant les messages depuis un fichier.
    private Thread threadFromFile() {
        return new Thread(() -> {
            long begin = System.currentTimeMillis();
            try {
                List<RawMessage> list = readAllMessages(getParameters().getRaw().get(0));
                for (RawMessage message : list) {
                    long dt = System.currentTimeMillis() - begin;
                    if (MessageParser.parse(message) != null) {
                        if ((MessageParser.parse(message).timeStampNs() / CONVERSION - dt) >= 0) {
                            Thread.sleep((MessageParser.parse(message).timeStampNs() / CONVERSION - dt));
                            messageQueue.add(message);
                        }
                    }
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // Methode retournant un thread en démodulant les messages.
    private Thread threadFromDemodulator() {
        return new Thread(() -> {
            try {
                AdsbDemodulator demodulator = new AdsbDemodulator(System.in);
                while (true) {
                    RawMessage message = demodulator.nextMessage();
                    if (message != null) {
                        messageQueue.add(message);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}