package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.text.NumberFormat;
import java.util.function.Consumer;


/**
 * La classe AircraftTableController gère la table des aéronefs.
 * @author Marwa Chiguer (325221)
 * @author Imane Oujja (344332)
 */

public final class AircraftTableController {


    private static final int INT = 2;
    private static final int OACI_WIDTH = 60;
    private static final int DESCRIPTION_WIDTH = 70;
    private static final int INDICATIF_WIDTH = 70;
    private static final int IMMATRICULATION_WIDTH = 90;
    private static final int MODEL_WIDTH = 230;
    private static final int Type_WIDTH = 50;
    private static final int MINIMUM_FRACTION_DIGITS1 = 0;
    private static final int MINIMUM_FRACTION_DIGITS2 = 4;
    private final TableView<ObservableAircraftState> tableView;
    private final ObjectProperty<ObservableAircraftState> selectedAircraft;

    private final ObservableSet<ObservableAircraftState> aircraftStates;



    /**
     * Son constructeur public prend en arguments
     * @param aircraftStates  l'ensemble des états des aéronefs qui doivent apparaître sur la vue
     * @param selectedAircraft la propriété JavaFX contenant l'état de l'aéronef sélectionné,
     */
    public AircraftTableController(ObservableSet<ObservableAircraftState> aircraftStates,
                                   ObjectProperty<ObservableAircraftState> selectedAircraft) {
        this.tableView = new TableView<>();
        this.selectedAircraft = selectedAircraft;
        this.aircraftStates= aircraftStates;

        configureTableView();
        configureColumns();
        addEventHandlers();

    }



    /**
     * La méthode getTableView() retourne une vue du tableau
     * @return une vue du tableau
     */
    public TableView<ObservableAircraftState> getTableView() {
        return tableView;
    }





    public void setOnDoubleClick(Consumer<ObservableAircraftState> doubleClickConsumer) {
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == INT && event.getButton().equals(javafx.scene.input.MouseButton.PRIMARY)) {
                ObservableAircraftState selectedState = tableView.getSelectionModel().getSelectedItem();
                if (selectedState != null) {
                    doubleClickConsumer.accept(selectedState);
                }
            }
        });
    }


    private void configureTableView() {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS); // Définit la politique de redimensionnement des colonnes
        tableView.setTableMenuButtonVisible(true);   // Affiche le bouton du menu du tableView
        tableView.getStyleClass().add("table.css");  // Ajoute une classe CSS spécifique au tableView
    }



    private void configureColumns() {
        // Crée les colonnes du tableView avec leurs titres
        TableColumn<ObservableAircraftState, String> oaciColumn = new TableColumn<>("OACI");
        TableColumn<ObservableAircraftState, String> indicatifColumn = new TableColumn<>("Indicatif");
        TableColumn<ObservableAircraftState, String> immatriculationColumn = new TableColumn<>("Immatriculation");
        TableColumn<ObservableAircraftState, String> modeleColumn = new TableColumn<>("Modèle");
        TableColumn<ObservableAircraftState, String> typeColumn = new TableColumn<>("Type");
        TableColumn<ObservableAircraftState, String> descriptionColumn = new TableColumn<>("Description");
        TableColumn<ObservableAircraftState, String> longitudeColumn = new TableColumn<>("Longitude (°)");
        TableColumn<ObservableAircraftState, String> latitudeColumn = new TableColumn<>("Latitude (°)");
        TableColumn<ObservableAircraftState, String> altitudeColumn = new TableColumn<>("Altitude (m)");
        TableColumn<ObservableAircraftState, String> vitesseColumn = new TableColumn<>("Vitesse (km/h)");


        tableView.getColumns().setAll(oaciColumn, indicatifColumn, immatriculationColumn,
                modeleColumn, typeColumn, descriptionColumn, longitudeColumn, latitudeColumn, altitudeColumn, vitesseColumn);



        // Définit la largeur préférée des colonnes
        oaciColumn.setPrefWidth(OACI_WIDTH);
        indicatifColumn.setPrefWidth(INDICATIF_WIDTH);
        immatriculationColumn.setPrefWidth(IMMATRICULATION_WIDTH);
        modeleColumn.setPrefWidth(MODEL_WIDTH);
        typeColumn.setPrefWidth(Type_WIDTH);
        descriptionColumn.setPrefWidth(DESCRIPTION_WIDTH);



        // Configure les valeurs des cellules pour chaque colonne
        oaciColumn.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().getIcaoAddress().string()));
        indicatifColumn.setCellValueFactory(f ->
                f.getValue().callSignProperty().map(CallSign::string));
        immatriculationColumn.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>((f.getValue().getData())).map(a-> a.registration().string()));
        modeleColumn.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>((f.getValue().getData())).map(AircraftData::model));
        typeColumn.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>((f.getValue().getData())).map(a-> a.typeDesignator().string()));
        descriptionColumn.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>((f.getValue().getData())).map(a-> a.description().string()));



        // Configuration des formats numériques
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(MINIMUM_FRACTION_DIGITS1);
        numberFormat.setMaximumFractionDigits(MINIMUM_FRACTION_DIGITS1);
        NumberFormat numberFormat2 = NumberFormat.getInstance();
        numberFormat2.setMinimumFractionDigits(MINIMUM_FRACTION_DIGITS2);
        numberFormat2.setMaximumFractionDigits(MINIMUM_FRACTION_DIGITS2);



        // Configure les valeurs des cellules pour les colonnes numériques
        longitudeColumn.setCellValueFactory( f -> f.getValue().positionProperty().map(p-> numberFormat2.format(p.longitude())));
        latitudeColumn.setCellValueFactory(f -> f.getValue().positionProperty().map(p -> numberFormat2.format(p.latitude())));
        altitudeColumn.setCellValueFactory(f -> f.getValue().altitudeProperty().map(p -> p != null ? numberFormat.format(p.doubleValue()) : ""));
        vitesseColumn.setCellValueFactory(f -> f.getValue().velocityProperty().map(p -> p != null ? numberFormat.format(p.doubleValue()) : ""));


        //Définit les comparateurs
        latitudeColumn.setComparator((s1, s2) -> compareNumericValues(s1, s2, numberFormat2));  // Compare les valeurs numériques pour la colonne latitude
        longitudeColumn.setComparator((s1, s2) -> compareNumericValues(s1, s2, numberFormat2)); // Compare les valeurs numériques pour la colonne longitude
        altitudeColumn.setComparator((s1, s2) -> compareNumericValues(s1, s2, numberFormat)); // Compare les valeurs numériques pour la colonne altitude
        vitesseColumn.setComparator((s1, s2) -> compareNumericValues(s1, s2, numberFormat)); // Compare les valeurs numériques pour la colonne vitesse
    }



    private void addEventHandlers() {
        selectedAircraft.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                tableView.getSelectionModel().select(newValue); // Sélectionne la nouvelle valeur dans le tableView.
                tableView.scrollTo(newValue);   // Défile le tableView pour afficher la nouvelle valeur
            }
        });

        aircraftStates.addListener((SetChangeListener<ObservableAircraftState>)  change -> {
            if(change.wasRemoved()) {
                tableView.getItems().remove(change.getElementRemoved());  // Supprime l'élément supprimé de tableView
            }else if(change.wasAdded()){
                tableView.getItems().add(change.getElementAdded());   // Ajoute l'élément ajouté à tableView
                tableView.sort();    // Trie les éléments dans le tableView
            }
        } );


        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedAircraft.set(newValue);   // Met à jour la valeur sélectionnée dans selectedAircraft
        });
    }



    private int compareNumericValues(String s1, String s2, NumberFormat numberFormat) {
        if (s1.isEmpty() || s2.isEmpty()) {
            return s1.compareTo(s2); // Comparaison lexicographique par défaut si l'une des chaînes est vide
        } else {
            try {
                Number n1 = numberFormat.parse(s1); // Convertir la chaîne s1 en nombre en utilisant le format numberFormat
                Number n2 = numberFormat.parse(s2); // Convertir la chaîne s2 en nombre en utilisant le format numberFormat
                return Double.compare(n1.doubleValue(), n2.doubleValue());
            } catch (Exception e) {
                return 0; // Si une exception est levée lors de la conversion en nombre renvoye 0
            }
        }
    }

}
