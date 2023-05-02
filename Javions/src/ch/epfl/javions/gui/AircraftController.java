package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;


public final class AircraftController {
    private Pane pane;
    private MapParameters mapParameters;
    private ObservableSet<ObservableAircraftState> aircraftStates;
    private ObjectProperty<ObservableAircraftState> aircraftStateObjectProperty;


    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> aircraftStates,
                              ObjectProperty<ObservableAircraftState> aircraftStateObjectProperty) {
        this.mapParameters = mapParameters;
        this.aircraftStates = aircraftStates;
        this.aircraftStateObjectProperty = aircraftStateObjectProperty;
        pane = new Pane();
        pane.getStylesheets().add("aircraft.css");

        for(ObservableAircraftState state : aircraftStates){
            constructAircraftGroup(state);
        }

        //listeners
        aircraftStates.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
                    constructAircraftGroup(change.getElementAdded());
                    pane.getChildren().removeIf(children -> children.equals(change.getElementRemoved()));
                });
    }

    public Pane pane() {
        return pane;
    }

    private void constructAircraftGroup(ObservableAircraftState state) {
        Group aircraftGroup = new Group();
        aircraftGroup.setId(state.getAddress().string());
        aircraftGroup.viewOrderProperty().bind(state.altitudeProperty().negate());
        Group iconAndTag = new Group(getSVG(state));

    }


    private Group createIconAndTagGroup(ObservableAircraftState state){
        Group iconAndTag = new Group(getSVG(state));
        GeoPos position = state.getPosition();
        iconAndTag.layoutXProperty().bind(Bindings.createDoubleBinding(() ->
                WebMercator.x(mapParameters.getZoomValue(), position.longitude()) - mapParameters.getMinXValue())
        );
        iconAndTag.layoutYProperty().bind(Bindings.createDoubleBinding(() ->
                WebMercator.y(mapParameters.getZoomValue(), position.latitude()) - mapParameters.getMinYValue())
        );
        return iconAndTag;
    }

    private SVGPath getSVG(ObservableAircraftState state) {
        SVGPath iconPath = new SVGPath();
        AircraftIcon icon = AircraftIcon.iconFor(state.getData().typeDesignator(), state.getData().description(),
                state.getCategory(), state.getData().wakeTurbulenceCategory());
        iconPath.getStyleClass().add("aircraft");
        iconPath.setContent(icon.svgPath());
        iconPath.setFill(ColorRamp.colorFromPlasma(state.getAltitude()));
        iconPath.rotateProperty().bind(Bindings.createDoubleBinding(state.trackOrHeadingProperty()::get));
        return iconPath;
    }




}
