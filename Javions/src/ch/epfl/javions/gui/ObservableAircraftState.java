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
    public record AirbornePos( GeoPos position, double altitude ) {}
    private static IcaoAddress address;
    private static AircraftData data;
    long lastTrajectoryTimeStamp = 0;
    public ObservableAircraftState(IcaoAddress address, AircraftData data){
        this.address = address;
        this.data = data;
    }
    private LongProperty lastMessageTimeStampNs = new SimpleLongProperty();
    private IntegerProperty category = new SimpleIntegerProperty();
    private ObjectProperty<CallSign> callSign = new SimpleObjectProperty<>();
    private ObjectProperty<GeoPos> position = new SimpleObjectProperty<>();
    private ObservableList<AirbornePos> trajectoryModifiable = observableArrayList();
    private ObservableList<AirbornePos> trajectoryNonModifiable = unmodifiableObservableList(trajectoryModifiable);
    private DoubleProperty altitude = new SimpleDoubleProperty();
    private DoubleProperty velocity = new SimpleDoubleProperty();
    private DoubleProperty trackOrHeading = new SimpleDoubleProperty();
    public static IcaoAddress getAddress() {
        return address;
    }
    public static AircraftData getData(){
        return data;
    }
    public ReadOnlyLongProperty lastMessageTimeStampNsProperty(){
        return lastMessageTimeStampNs;
    }
    public ReadOnlyIntegerProperty categoryProperty(){
        return category;
    }
    public ReadOnlyObjectProperty callSignProperty(){
        return callSign;
    }
    public ReadOnlyObjectProperty positionProperty(){
        return position;
    }
    public ObservableList trajectoryProperty(){
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
    public long getLastMessageTimeStampNs(){
        return lastMessageTimeStampNs.get();
    }
    public int getCategory(){
        return category.get();
    }
    public CallSign getCallSign(){
        return callSign.get();
    }
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

    @Override
    public void setCategory(int category) {
        this.category.set(category);
    }

    @Override
    public void setCallSign(CallSign callSign) {
        this.callSign.set(callSign);
    }

    @Override
    public void setPosition(GeoPos position) {
        if( trajectoryModifiable.isEmpty() || !position.equals(trajectoryModifiable.get(trajectoryModifiable.size() - 1).position) )
        {
            trajectoryModifiable.add( new AirbornePos(position, altitude.get()));
            lastTrajectoryTimeStamp = lastMessageTimeStampNs.get();
        }
        this.position.set(position);
    }

    @Override
    public void setAltitude(double altitude) {
        if( lastMessageTimeStampNs.get() == lastTrajectoryTimeStamp )
        {
            trajectoryModifiable.set(trajectoryModifiable.size() - 1, new AirbornePos(position.get(), altitude));
        }
        this.altitude.set(altitude);
    }

    @Override
    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
    }

    @Override
    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading.set(trackOrHeading);
    }

}
