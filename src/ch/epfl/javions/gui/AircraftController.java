package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.WakeTurbulenceCategory;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import java.util.Iterator;
import java.util.List;

import static ch.epfl.javions.WebMercator.y;
import static javafx.scene.paint.CycleMethod.NO_CYCLE;

public final class AircraftController {
    public static final int ALTITUDE_MAXIMAL = 12000;
    public static final int RECTANGLE_FACTOR = 4;
    public static final int LABEL_ZOOM = 11;
    public static final CycleMethod CYCLE = NO_CYCLE;
    public static final int START_X = 0;
    public static final int START_Y = 0;
    public static final int END_X = 1;
    public static final int END_Y = 0;
    private final ObservableSet<ObservableAircraftState> aircraftStates;
    private final Pane aircraftPane;
    private final MapParameters mapParameters;
    private final ObjectProperty<ObservableAircraftState> stateObjectProperty;

    /**
     * Construction of an AircraftController object.
     *
     * @param parameters        map parameters used for the aircraft controller.
     * @param states            set of observable aircraft states.
     * @param aircraftState     object property representing the actual aircraft state.
     */
    public AircraftController(MapParameters parameters, ObservableSet<ObservableAircraftState> states,
                              ObjectProperty<ObservableAircraftState> aircraftState) {
        this.mapParameters = parameters;
        this.aircraftStates = states;
        this.stateObjectProperty = aircraftState;
        this.aircraftPane = new Pane();
        aircraftPane.setPickOnBounds(false);
        this.aircraftPane.getStylesheets().add("aircraft.css");
        addedAircraft();
    }

    /**
     * Returns the Pane containing the aircraft on the map.
     *
     * @return The Pane containing the aircraft on the map.
     */


    public Pane pane() {
        return aircraftPane;
    }

    /**
     * Adds the aircraft to the aircraft pane.
     * This method is called whenever there is a change concerning the aircraft states, by using the listeners to
     handle the addition and removal of annotated aircraft based on the aircraft state changements.
     */
    private void addedAircraft() {
        aircraftStates.addListener((SetChangeListener<ObservableAircraftState>) change -> {
            // add the annotated aircraft
            if (change.wasAdded()) {
                ObservableAircraftState state = change.getElementAdded();
                Group annotatedAircraft = new Group(iconLabel(state));
                annotatedAircraft.setId(state.getIcaoAddress().string());
                annotatedAircraft.getChildren().addAll(trajectory(state),labelAndIconGroup(state));
                annotatedAircraft.viewOrderProperty().bind(state.altitudeProperty().negate());
                aircraftPane.getChildren().add(annotatedAircraft);
            }
            //removes the aircraft
            if (change.wasRemoved()) {
                ObservableAircraftState state = change.getElementRemoved();
                aircraftPane.getChildren().removeIf(
                        aircraft -> aircraft.getId().equals(state.getIcaoAddress().string()));
            }
        });
    }

    /**
     * Creates and returns a group that contains the label and icon for the specified aircraft state.
     *
     * @param aircraftState The observable aircraft state.
     * @return The group containing the label and icon for the aircraft state.
     */
    private Group labelAndIconGroup(ObservableAircraftState aircraftState) {
        Group labelAndIconGroup = new Group();
        labelAndIconGroup.getChildren().addAll(label(aircraftState), icon(aircraftState));
        labelAndIconGroup.layoutXProperty().bind(Bindings.createDoubleBinding(() -> {
            double longitude = aircraftState.getPosition().longitude();
            double longitudeInPixel = WebMercator.x(mapParameters.getZoom(), longitude);
            return longitudeInPixel - mapParameters.getMinX();
        },mapParameters.minX(), mapParameters.zoom(), aircraftState.positionProperty()));
        labelAndIconGroup.layoutYProperty().bind(Bindings.createDoubleBinding(() -> {
            double latitude = aircraftState.getPosition().latitude();
            double latitudeInPixel = y(mapParameters.getZoom(), latitude);
            return latitudeInPixel - mapParameters.getMinY();
        },mapParameters.zoom(), mapParameters.minY(), aircraftState.positionProperty()));
        return labelAndIconGroup;
    }

    /**
     * Creates an SVGPath object representing the aircraft icon for the state entered .
     *
     * @param state The aircraft state given
     * @return The SVGPath object representing the icon of the aircrafte
     */

    private SVGPath icon(ObservableAircraftState state) {
        SVGPath svgPath = new SVGPath();
        svgPath.getStyleClass().add("aircraft");

        AircraftIcon aircraftIcon;
        if (state.getData() != null)
            aircraftIcon = AircraftIcon.iconFor(state.getData().typeDesignator(),
                    state.getData().description(),
                    state.getCategory(), state.getData().wakeTurbulenceCategory());
        else
            aircraftIcon = AircraftIcon.iconFor(new AircraftTypeDesignator(""),
                    new AircraftDescription(""), state.getCategory(), WakeTurbulenceCategory.of(""));


        svgPath.contentProperty().bind(new SimpleStringProperty(aircraftIcon.svgPath()));

        svgPath.rotateProperty().bind(Bindings.createDoubleBinding(() ->
                        aircraftIcon.canRotate()
                                ? Units.convertTo(state.getTrackOrHeading(), Units.Angle.DEGREE)
                                : 0d,
                state.trackOrHeadingProperty())
        );
        double stateAltitude=state.getAltitude();

        svgPath.fillProperty().bind(Bindings.createObjectBinding(() ->
                        ColorRamp.PLASMA.at(colorFonction(stateAltitude))
                , state.altitudeProperty()));

        svgPath.setOnMouseClicked(click -> stateObjectProperty.set(state));

        return svgPath;
    }

    private Group label(ObservableAircraftState stateData) {
        Rectangle rectangle = new Rectangle();
        Text text = new Text();

        text.textProperty().bind(Bindings.createStringBinding(() -> (
                        stateData.getData() != null ? stateData.getData().registration().string() :
                                stateData.getCallSign() != null ? stateData.getCallSign().string() :
                                        stateData.getIcaoAddress().string())  + "\n" +
                        (stateData.velocityProperty() != null ? String.format("%.0f",
                                Units.convertTo(stateData.getVelocity(),Units.Speed.KILOMETER_PER_HOUR)) : "?") + "\u2002Km/h "+
                        (stateData.altitudeProperty() != null ? String.format("%.0f",
                                stateData.getAltitude()): "?") + "\u2002m",
                stateData.altitudeProperty(), stateData.velocityProperty(), stateData.callSignProperty()));

        rectangle.widthProperty().bind(text.layoutBoundsProperty().map(bounds -> bounds.getWidth() + RECTANGLE_FACTOR));
        rectangle.heightProperty().bind(text.layoutBoundsProperty().map(bounds->bounds.getHeight() + RECTANGLE_FACTOR));

        Group label = new Group(rectangle, text);
        label.visibleProperty().bind(Bindings.createBooleanBinding(() ->
                        mapParameters.getZoom() >= LABEL_ZOOM || stateData.equals(stateObjectProperty.get()),
                stateObjectProperty, mapParameters.zoom()));

        label.getStyleClass().add("label");
        return label;
    }

    private Group iconLabel(ObservableAircraftState state) {
        Group iconLabel = new Group(icon(state), label(state));
        iconLabel.layoutXProperty().bind(Bindings.createDoubleBinding(() ->
                        WebMercator.x(mapParameters.getZoom(), state.getPosition().longitude()) - mapParameters.getMinX(),
                mapParameters.zoom(), mapParameters.minX(), state.positionProperty()));

        iconLabel.layoutYProperty().bind(Bindings.createDoubleBinding(() ->
                        y(mapParameters.getZoom(), state.getPosition().latitude()) - mapParameters.getMinY(),
                mapParameters.zoom(), mapParameters.minY(), state.positionProperty()));

        state.positionProperty().addListener((observable, oldValue, newValue) ->
                state.setPosition(newValue));
        return iconLabel;
    }



    private Group trajectory(ObservableAircraftState state) {
        Group trajectoryGroup = new Group();
        trajectoryGroup.getStyleClass().add("trajectory");
        InvalidationListener trajectoryList= change-> drawTrajectory(trajectoryGroup,state);

        InvalidationListener zoomListener = change-> drawTrajectory(trajectoryGroup,state);
        trajectoryGroup.visibleProperty().addListener((observableValue,oldValue, newValue)-> {
            if (newValue) {
                state.getTrajectory().addListener(trajectoryList);
                mapParameters.zoom().addListener(zoomListener);
                drawTrajectory(trajectoryGroup,state);
            } else {
                state.getTrajectory().removeListener(trajectoryList);
                mapParameters.zoom().removeListener(zoomListener);
                trajectoryGroup.getChildren().clear();
            }
        });
        trajectoryGroup.visibleProperty().bind(stateObjectProperty.isEqualTo(state));
        return trajectoryGroup;
    }
    private void drawTrajectory(Group groupTraject, ObservableAircraftState aircraftState) {
        groupTraject.getChildren().clear();
        List<ObservableAircraftState.AirbornePos> trajectory = aircraftState.getTrajectory();
        Iterator<ObservableAircraftState.AirbornePos> iterator = trajectory.iterator();
        ObservableAircraftState.AirbornePos previousPos;
        ObservableAircraftState.AirbornePos currentPos;

        if (iterator.hasNext()) {
            previousPos=iterator.next() ;
            while(iterator.hasNext()) {
                currentPos = iterator.next();

                Line line=createLines(currentPos,previousPos);
                colorTrajectory(previousPos, currentPos, line);

                previousPos = currentPos;
                groupTraject.getChildren().addAll(line);
            }
        }
    }
    private Line createLines(ObservableAircraftState.AirbornePos posCurrent, ObservableAircraftState.AirbornePos pos){
        Line line = new Line(WebMercator.x(mapParameters.getZoom(), pos.position().longitude()),
                WebMercator.y(mapParameters.getZoom(), pos.position().latitude()),
                WebMercator.x(mapParameters.getZoom(), posCurrent.position().longitude()),
                WebMercator.y(mapParameters.getZoom(), posCurrent.position().latitude()));
        line.layoutXProperty().bind(mapParameters.minX().negate());
        line.layoutYProperty().bind(mapParameters.minY().negate());
        return line;
    }

    private double colorFonction(double altitude) {
        return Math.cbrt(altitude / ALTITUDE_MAXIMAL);

    }
    private void colorTrajectory(ObservableAircraftState.AirbornePos position,
                                 ObservableAircraftState.AirbornePos nextPosition, Line line){
        if (position.altitude() == nextPosition.altitude()) {
            line.setStroke(ColorRamp.PLASMA.at(colorFonction(nextPosition.altitude())));
        } else {
            Stop[] stops = new Stop[]{
                    new Stop(0, ColorRamp.PLASMA.at(colorFonction(position.altitude()))),
                    new Stop(1, ColorRamp.PLASMA.at(colorFonction(nextPosition.altitude())))
            };
            LinearGradient lineGradient = new LinearGradient(START_X, START_Y, END_X, END_Y,
                    true, CYCLE, stops);
            line.setStroke(lineGradient);
        }
    }

}