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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class essai extends Application {
    private final ConcurrentLinkedQueue<RawMessage> messageQueue = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {

        Path tileCache = Path.of("tile-cache");
        TileManager tm = new TileManager(tileCache, "tile.openstreetmap.org");
        MapParameters mp = new MapParameters(8, 33_530, 23_070);
        BaseMapController bmc = new BaseMapController(tm, mp);


        URL url = getClass().getResource("/aircraft.zip");
        assert url != null;
        String f = Path.of(url.toURI()).toString();
        var db = new AircraftDatabase(f);

        AircraftStateManager asm = new AircraftStateManager(db);
        ObjectProperty<ObservableAircraftState> sap = new SimpleObjectProperty<>();
        AircraftController ac = new AircraftController(mp, asm.states(), sap);
        AircraftTableController atc  = new AircraftTableController(asm.states(), sap);

        Label aircraftCountLabel = new Label();
        IntegerProperty aircraftCountProperty = new SimpleIntegerProperty();
        aircraftCountLabel.textProperty().bind(Bindings.convert(aircraftCountProperty));

        StackPane mapAndPlanes = new StackPane(bmc.pane(), ac.pane());

        StatusLineController slc = new StatusLineController();
        slc.aircraftCountProperty().bind(Bindings.createObjectBinding(() ->
                asm.states().size(),asm.states()));

        BorderPane tableAndStatusLine = new BorderPane();
        tableAndStatusLine.setCenter(atc.getTableView());
        tableAndStatusLine.setTop(slc.getPane());

        SplitPane root = new SplitPane(mapAndPlanes,tableAndStatusLine);
        root.setOrientation(Orientation.VERTICAL);
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Javions");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();


        Thread thread = new Thread(()-> {
            if (getParameters().getRaw().isEmpty()){
                try {
                    AdsbDemodulator demodulator = new AdsbDemodulator(System.in);
                    while (true){
                        RawMessage message = demodulator.nextMessage();
                        if (message!= null){
                            messageQueue.add(message);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    List<RawMessage> list = readAllMessages(getParameters().getRaw().get(0), slc);
                    for (RawMessage message : list){
                        Message m = MessageParser.parse(message);
                        if(m != null){
                            Thread.sleep(1);
                            messageQueue.add(message);}
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            slc.messageCountProperty().set(slc.messageCountProperty().get()+1);
        });




        new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    while (!messageQueue.isEmpty()){
                        Message m = MessageParser.parse( messageQueue.remove());
                        if (m != null) asm.updateWithMessage(m);}
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }.start();


    }
    static List<RawMessage> readAllMessages(String fileName, StatusLineController slc)
            throws IOException {
        long counter = 0;
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
                    counter++;
                    slc.messageCountProperty().set(++counter);
                }
            }

        } catch (EOFException e) {
            System.out.println("----------------------------------------------end-----------------------------------------------------");
        }
        return Collections.unmodifiableList(allMessages);
    }
}


