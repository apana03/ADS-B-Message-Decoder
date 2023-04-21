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
        if( !map.containsKey(address) ){
            map.put(address, new AircraftStateAccumulator(new ObservableAircraftState(address, database.get(address))));
        }
        map.get(address).update(message);
        if( ((ObservableAircraftState) map.get(address).stateSetter()).getPosition() != null )
            states.add((ObservableAircraftState) map.get(address).stateSetter());
    }
    public void purge(){
        for( ObservableAircraftState state : states )
            if(lastProcessedTimeStamp - state.getLastMessageTimeStampNs() > minuteInNs){
                states.remove(state);
            }
    }
    public ObservableSet<ObservableAircraftState> states(){
        return statesNonModifiable;
    }
}
