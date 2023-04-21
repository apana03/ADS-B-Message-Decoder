package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

public final class ObservableAircraftState implements AircraftStateSetter
{
    private static IcaoAddress address;
    private static AircraftData data;
    public ObservableAircraftState(IcaoAddress address, AircraftData data){
        this.address = address;
        this.data = data;
    }
    ObjectProperty<Long> lastMessageTimeStampNs = new SimpleObjectProperty<>();
    ObjectProperty<Integer> category = new SimpleObjectProperty<>();
    ObjectProperty<CallSign> callSign = new SimpleObjectProperty<>();
    ObjectProperty<GeoPos> position = new SimpleObjectProperty<>();
    ObservableList<Pair<GeoPos, Double>> trajectory = new SimpleListProperty<>();
    ObjectProperty<Double> altitude = new SimpleObjectProperty<>();
    ObjectProperty<Double> velocity = new SimpleObjectProperty<>();
    ObjectProperty<Double> trackOrHeading = new SimpleObjectProperty<>();
    public static IcaoAddress getAddress() {
        return address;
    }
    public static AircraftData getData(){
        return data;
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
        this.position.set(position);
    }

    @Override
    public void setAltitude(double altitude) {
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
