package ch.epfl.javions.gui;

import javafx.beans.property.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

/**
 * La classe StatusLineController du sous-paquetage gui, publique et finale, gère la ligne d'état.
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */

public final class StatusLineController {
    private final BorderPane pane;
    private final IntegerProperty aircraftCountProperty;
    private final LongProperty messageCountProperty;


    /**
     * Le constructeur par défaut, qui construit le graphe de scène.
     */
    public StatusLineController() {
        Text aircraftCountText = new Text();
        Text messageCountText = new Text();
        aircraftCountProperty = new SimpleIntegerProperty();
        messageCountProperty = new SimpleLongProperty();
        pane = new BorderPane(null,null,messageCountText,null,aircraftCountText);
        pane.getStyleClass().add("status.css");

        // bindings
        aircraftCountText.textProperty().bind(
                aircraftCountProperty.asString("Aéronefs visibles : %s"));
        messageCountText.textProperty().bind(
                messageCountProperty.asString("Messages reçus : %s"));
    }

    /**
     *Retourne le panneau contenant la ligne d'état.
     * @return pane.
     */
    public BorderPane getPane() {return pane;}

    /**
     * retourne la propriété (modifiable) contenant le nombre d'aéronefs actuellement visibles
     * @return aircraftCountProperty.
     */
    public IntegerProperty aircraftCountProperty() {return aircraftCountProperty;}

    /**
     * retourne la propriété (modifiable) contenant le nombre de messages reçus
     * depuis le début de l'exécution du programme.
     * @return messageCountProperty.
     */
    public LongProperty messageCountProperty() {return messageCountProperty;}
}