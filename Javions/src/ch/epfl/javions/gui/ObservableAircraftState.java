package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.unmodifiableObservableList;

public final class ObservableAircraftState implements AircraftStateSetter
{
    /**
     * Inner record for the airborne position
     * @param position
     * @param altitude
     */
    public record AirbornePos(GeoPos position, double altitude){}
    private final IcaoAddress address;
    private final AircraftData data;
    private long lastTrajectoryTimeStamp = 0;
    public ObservableAircraftState(IcaoAddress address, AircraftData data){
        this.address = address;
        this.data = data;
    }

    /**
     * Declaration of the properties of the ObservableAircraftState
     * lastMessageTimeStampNs : the time stamp of the last message received
     * category : the category of the aircraft
     * callSign : the call sign of the aircraft
     * position : the position of the aircraft
     * trajectoryModifiable : the trajectory of the aircraft (modifiable)
     * altitude : the altitude of the aircraft
     * velocity : the velocity of the aircraft
     * trackOrHeading : the track or heading of the aircraft
     */
    private final LongProperty lastMessageTimeStampNs = new SimpleLongProperty();
    private final IntegerProperty category = new SimpleIntegerProperty();
    private final ObjectProperty<CallSign> callSign = new SimpleObjectProperty<>();
    private final ObjectProperty<GeoPos> position = new SimpleObjectProperty<>();
    private final ObservableList<AirbornePos> trajectoryModifiable = observableArrayList();
    private final ObservableList<AirbornePos> trajectoryNonModifiable = unmodifiableObservableList(trajectoryModifiable);
    private final DoubleProperty altitude = new SimpleDoubleProperty(Double.NaN);
    private final DoubleProperty velocity = new SimpleDoubleProperty(Double.NaN);
    private final DoubleProperty trackOrHeading = new SimpleDoubleProperty();
    public ReadOnlyLongProperty lastMessageTimeStampNsProperty(){
        return lastMessageTimeStampNs;
    }
    public ReadOnlyIntegerProperty categoryProperty(){
        return category;
    }
    public ReadOnlyObjectProperty<CallSign> callSignProperty(){
        return callSign;
    }
    public ReadOnlyObjectProperty<GeoPos> positionProperty(){
        return position;
    }
    public ObservableList<AirbornePos> trajectoryProperty(){
        return trajectoryNonModifiable;
    }
    public ReadOnlyDoubleProperty altitudeProperty(){
        return altitude;
    }
    public ReadOnlyDoubleProperty velocityProperty(){
        return velocity;
    }
    public ReadOnlyDoubleProperty trackOrHeadingProperty(){
        return trackOrHeading;
    }
    public IcaoAddress getAddress() {
        return this.address;
    }
    public AircraftData getData(){
        return this.data;
    }
    public long getLastMessageTimeStampNs(){
        return lastMessageTimeStampNs.get();
    }
    public int getCategory(){
        return category.get();
    }
    public CallSign getCallSign(){return callSign.get();}
    public GeoPos getPosition(){
        return position.get();
    }
    public List<AirbornePos> getTrajectory(){
        return trajectoryNonModifiable;
    }
    public double getAltitude(){
        return altitude.get();
    }
    public double getVelocity(){
        return velocity.get();
    }
    public double getTrackOrHeading(){
        return trackOrHeading.get();
    }
    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        lastMessageTimeStampNs.set(timeStampNs);
    }

    /**
     * Sets the category of the aircraft.
     * @param category the category value
     */

    @Override
    public void setCategory(int category) {
        this.category.set(category);
    }

    /**
     * Sets the call sign of the aircraft.
     * @param callSign the CallSign object representing the call sign
     */

    @Override
    public void setCallSign(CallSign callSign) {
        this.callSign.set(callSign);
    }

    /**
     * Sets the position of the aircraft.
     * If the position is different from the last one, it is added to the trajectory.
     * If the position is the same as the last one, the altitude of the last trajectory point is updated.
     * @param position
     *      the position
     */
    @Override
    public void setPosition(GeoPos position) {
        this.position.set(position);
        trajectoryModifiable.set(trajectoryModifiable.size() - 1, new AirbornePos(getPosition(), getAltitude()));
    }

    /**
     * Sets the altitude of the aircraft.
     * If the altitude is different from the last one, it is added to the trajectory.
     * If the altitude is the same as the last one, the position of the last trajectory point is updated.
     * If the last trajectory point is the same as the current position, the altitude of the last trajectory point is updated.
     * If the last trajectory point is different from the current position, a new trajectory point is added.
     * @param altitude
     *      the altitude
     */
    @Override
    public void setAltitude(double altitude) {
        this.altitude.set(altitude);
        updateTrajectory();
    }

    @Override
    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
    }

    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading.set(trackOrHeading);
    }

    private void updateTrajectory(){
        if( trajectoryModifiable.isEmpty() || !position.equals(trajectoryModifiable.get(trajectoryModifiable.size() - 1).position) )
        {
            trajectoryModifiable.add( new AirbornePos(getPosition(), getAltitude()));
            lastTrajectoryTimeStamp = lastMessageTimeStampNs.get();
        }else if( lastMessageTimeStampNs.get() == lastTrajectoryTimeStamp ){
            trajectoryModifiable.set(trajectoryModifiable.size() - 1, new AirbornePos(getPosition(), getAltitude()));
        }
    }

}
