package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AirbornePositionMessage;
import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static javafx.collections.FXCollections.observableSet;
import static javafx.collections.FXCollections.unmodifiableObservableSet;

public final class AircraftStateManager
{
    private final static long minuteInNs = (long) 6e+10;
    private Map<IcaoAddress, AircraftStateAccumulator> map;
    private ObservableSet<ObservableAircraftState> states;
    private ObservableSet<ObservableAircraftState> statesNonModifiable;
    private AircraftDatabase database;
    private long lastProcessedTimeStamp;
    public AircraftStateManager(AircraftDatabase database){
        this.database = database;
        states = observableSet();
        statesNonModifiable = unmodifiableObservableSet(states);
        map = new HashMap<>();
    }
    public void updateWithMessage(Message message) throws IOException{
        IcaoAddress address = message.icaoAddress();
        lastProcessedTimeStamp = message.timeStampNs();
        ObservableAircraftState observableAircraftState = new ObservableAircraftState(address, database.get(address));
        map.putIfAbsent(address, new AircraftStateAccumulator(observableAircraftState));
        map.get(address).update(message);
        if(observableAircraftState.getPosition() != null)
            states.add(observableAircraftState);
    }
    public void purge(){
        states.removeIf(observableAircraftState ->
                lastProcessedTimeStamp - observableAircraftState.getLastMessageTimeStampNs() > minuteInNs);
    }
    public ObservableSet<ObservableAircraftState> states(){
        return statesNonModifiable;
    }
}
