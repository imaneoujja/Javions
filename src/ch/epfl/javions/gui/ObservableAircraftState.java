package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.unmodifiableObservableList;

/**
 *  @author Salma El Yadouni (340859)

 * This class represents the state of an aircraft with various properties that can be observed and modified
 * The implemntation of the interface AircraftStateSetter will give us the possibility to sett different properties of
 * aircraft state
 */

public final class ObservableAircraftState implements AircraftStateSetter {

    private final IcaoAddress icaoAddress;
    //the ICAO address of the aircraft.
    private final AircraftData data;
    //contains all the fixed characteristics of this aircraft, sourced from the Mictronics database
    private final LongProperty lastMessageTimeStampNs;
    //contains the timestamp of the last message received from the aircraft, in nanoseconds.
    private final IntegerProperty category;
    // contains the aircraft category
    private final ObjectProperty<CallSign> callSign;
    // the property that contains the aircraft indicatif.
    private final ObjectProperty<GeoPos> position;
    // the property that contains the position of the aircraft on the Earth surface.

    private final DoubleProperty altitude;
    // the property that contains the aircraft altitude ,in meters.

    private final ObservableList<AirbornePos> trajectory;
    //  contains an observable and modifiable list of the aircraft trajectory.

    private final ObservableList<AirbornePos> unmodifiableTrajectory;
    // contains an unmodifiable but observable view of the previous list.

    private final DoubleProperty velocity;
    // the property that contains the aircraft velocity, in meters/second.

    private final DoubleProperty trackOrHeading;
    // the property that contains the track or heading of the aircraft, in radians.



    /**
     * Constructs a new ObservableAircraftState object with the given ICAO address and aircraft data.
     *
     * @param icaoAddress the ICAO address of the aircraft
     * @param data        the aircraft data
     */
    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData data) {
        this.icaoAddress = icaoAddress;
        this.trajectory = observableArrayList();
        this.unmodifiableTrajectory = unmodifiableObservableList(trajectory);
        this.data = data;
        this.lastMessageTimeStampNs = new SimpleLongProperty();
        this.category = new SimpleIntegerProperty();
        this.callSign = new SimpleObjectProperty<>();
        this.position = new SimpleObjectProperty<>();
        this.altitude = new SimpleDoubleProperty();
        this.velocity = new SimpleDoubleProperty();
        this.trackOrHeading = new SimpleDoubleProperty();
    }

    /**
     * Returns the ICAO address of the aircraft.
     *
     * @return the ICAO address of the aircraft
     */
    public IcaoAddress getIcaoAddress() {
        return icaoAddress;
    }

    /**
     * Returns the aircraft data.
     *
     * @return the aircraft data
     */
    public AircraftData getData() {
        return data;
    }

    /**
     * Returns the last message timestamp in nanoseconds.
     *
     * @return the last message timestamp in nanoseconds
     */
    public long getLastMessageTimeStampNs() {
        return lastMessageTimeStampNs.get();
    }

    /**
     * Returns the read-only property for the last message timestamp in nanoseconds.
     *
     * @return the read-only property for the last message timestamp in nanoseconds
     */
    public ReadOnlyLongProperty lastMessageTimeStampNsProperty() {
        return lastMessageTimeStampNs;
    }

    /**
     * Returns the category of the aircraft.
     *
     * @return the category of the aircraft
     */
    public int getCategory() {
        return category.get();
    }

    /**
     * Returns the read-only property for the category of the aircraft.
     *
     * @return the read-only property for the category of the aircraft
     */
    public ReadOnlyIntegerProperty categoryProperty() {
        return category;
    }

    /**
     * Returns the call sign of the aircraft.
     *
     * @return the call sign of the aircraft
     */
    public CallSign getCallSign() {
        return callSign.get();
    }

    /**
     * Returns the read-only property for the call sign of the aircraft.
     *
     * @return the read-only property for the call sign of the aircraft
     */
    public ReadOnlyObjectProperty<CallSign> callSignProperty() {
        return callSign;
    }


    /**
     * Returns the read-only property for the geographic position of the aircraft.
     *
     * @return the read-only property for the geographic position of the aircraft
     */
    public ReadOnlyObjectProperty<GeoPos> positionProperty() {
        return position;
    }

    /**
     * Returns the geographic position of the aircraft.
     *
     * @return the geographic position of the aircraft
     */


    public GeoPos getPosition() {return position.get();}

    /**
     * Returns the altitude of the aircraft.
     *
     * @return the altitude of the aircraft
     */
    public double getAltitude() {
        return altitude.get();
    }

    /**
     * Returns the read-only property for the altitude of the aircraft.
     *
     * @return the read-only property for the altitude of the aircraft
     */
    public ReadOnlyDoubleProperty altitudeProperty() {
        return altitude;
    }

    /**
     * Returns the trajectory of the aircraft.
     *
     * @return the trajectory of the aircraft
     */
    public ObservableList<AirbornePos> getTrajectory() {
        return unmodifiableTrajectory;
    }

    /**
     * Returns the velocity of the aircraft.
     *
     * @return the velocity of the aircraft
     */
    public double getVelocity() {
        return velocity.get();
    }

    /**
     * Returns the read-only property for the velocity of the aircraft.
     *
     * @return the read-only property for the velocity of the aircraft
     */
    public ReadOnlyDoubleProperty velocityProperty() {
        return velocity;
    }

    /**
     * Returns the track or heading of the aircraft.
     *
     * @return the track or heading of the aircraft
     */
    public double getTrackOrHeading() {
        return trackOrHeading.get();
    }

    /**
     * Returns the read-only property for the track or heading of the aircraft.
     *
     * @return the read-only property for the track or heading of the aircraft
     */
    public ReadOnlyDoubleProperty trackOrHeadingProperty() {
        return trackOrHeading;
    }

    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        lastMessageTimeStampNs.setValue(timeStampNs);
    }

    @Override
    public void setCategory(int category) {
        this.category.setValue(category);
    }

    @Override
    public void setCallSign(CallSign callSign) {
        this.callSign.setValue(callSign);
    }

    @Override
    public void setPosition(GeoPos position) {
        if (!Double.isNaN(getAltitude()))
            trajectory.add(new AirbornePos(position, getAltitude()));
        this.position.set(position);
    }

    /**
     * Sets the altitude of the aircraft.
     * If the aircraft has a valid position, it adds a new AirbornePos to the trajectory with the given altitude and timestamp.
     *
     * @param altitude the altitude of the aircraft
     */
    public void setAltitude(double altitude) {
        if (getPosition() != null) {
            AirbornePos lastPosition = trajectory.get(trajectory.size() - 1);
            AirbornePos positionUpdated = new AirbornePos(getPosition(), altitude);
            if (!getTrajectory().isEmpty()) {
                trajectory.add(positionUpdated);
            } else if((lastPosition.equals(positionUpdated))){
                trajectory.set(trajectory.size() - 1, new AirbornePos(getPosition(), altitude));
            }
        }
        this.altitude.setValue(altitude);
    }
    @Override
    public void setVelocity(double velocity) {
        this.velocity.setValue(velocity);
    }

    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading.setValue(trackOrHeading);
    }

    /**
     * This record represents a position in the air with a geographic position, altitude, and timestamp.
     */
    public record AirbornePos(GeoPos position, double altitude) {}
}