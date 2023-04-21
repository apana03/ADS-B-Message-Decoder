package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.ObservableSet;

import java.util.HashMap;
import java.util.Map;

import static javafx.collections.FXCollections.observableSet;
import static javafx.collections.FXCollections.unmodifiableObservableSet;

public final class AircraftStateManager
{
    private Map<IcaoAddress, AircraftStateAccumulator> map;
    private ObservableSet<ObservableAircraftState> states;
    private ObservableSet<ObservableAircraftState> statesNonModifiable;
    private AircraftDatabase database;
    public AircraftStateManager(AircraftDatabase database){
        this.database = database;
        states = observableSet();
        statesNonModifiable = unmodifiableObservableSet(states);
        map = new HashMap<>();

    }
    public ObservableSet states(){
        return states;
    }
}
