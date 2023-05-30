package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static javafx.collections.FXCollections.observableSet;
import static javafx.collections.FXCollections.unmodifiableObservableSet;

public final class AircraftStateManager
{
    private final static long minuteInNs = (long) 6e+10;
    private Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> map;
    private ObservableSet<ObservableAircraftState> states;
    private ObservableSet<ObservableAircraftState> statesNonModifiable;
    private AircraftDatabase database;
    private long lastProcessedTimeStamp;

    /**
     * Constructor for the AircraftStateManager
     * @param database
     */
    public AircraftStateManager(AircraftDatabase database){
        this.database = database;
        states = observableSet();
        statesNonModifiable = unmodifiableObservableSet(states);
        map = new HashMap<>();
    }

    /**
     * Updates the state of the aircraft with the message
     * @param message
     * @throws IOException
     */
    public void updateWithMessage(Message message) throws IOException{
        IcaoAddress address = message.icaoAddress();
        lastProcessedTimeStamp = message.timeStampNs();
        map.putIfAbsent(address,
                new AircraftStateAccumulator<>(new ObservableAircraftState(address, database.get(address))));
        map.get(address).update(message);
        if(map.get(address).stateSetter().getPosition() != null){
            states.add(map.get(address).stateSetter());
        }
    }

    /**
     * Purges the states that are too old
     * @throws IOException
     */
    public void purge(){
        states.removeIf(observableAircraftState ->
                lastProcessedTimeStamp - observableAircraftState.getLastMessageTimeStampNs() > minuteInNs);
    }
    public ObservableSet<ObservableAircraftState> states(){
        return statesNonModifiable;
    }
}
