package ch.epfl.javions.gui;


import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.WakeTurbulenceCategory;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;


public final class AircraftController {
    private final Pane pane;
    private final MapParameters mapParameters;
    private ObservableSet<ObservableAircraftState> aircraftStates;
    private ObjectProperty<ObservableAircraftState> aircraftStateObjectProperty;


    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> aircraftStates,
                              ObjectProperty<ObservableAircraftState> aircraftStateObjectProperty) {
        this.mapParameters = mapParameters;
        this.aircraftStates = aircraftStates;
        this.aircraftStateObjectProperty = aircraftStateObjectProperty;
        pane = new Pane();
        pane.getStylesheets().add("/aircraft.css");
        pane.setPickOnBounds(false);

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
        Group iconAndTag = createIconAndTagGroup(state);
        pane.getChildren().add(iconAndTag);
    }


    private Group createIconAndTagGroup(ObservableAircraftState state){
        Group iconAndTag = new Group(getSVG(state));
        iconAndTag.layoutXProperty().bind(Bindings.createDoubleBinding(() ->
                WebMercator.x(mapParameters.getZoomValue(), state.getPosition().longitude()) - mapParameters.getMinXValue(),
                mapParameters.getZoom(),
                state.positionProperty(),
                mapParameters.getMinX()));

        iconAndTag.layoutYProperty().bind(Bindings.createDoubleBinding(() ->
                WebMercator.y(mapParameters.getZoomValue(), state.getPosition().latitude()) - mapParameters.getMinYValue(),
                mapParameters.getZoom(),
                state.positionProperty(),
                mapParameters.getMinY()));

        return iconAndTag;
    }

    private SVGPath getSVG(ObservableAircraftState state) {
        AircraftData aircraftData = state.getData();

        AircraftTypeDesignator typeDesignator = (aircraftData == null) ? new AircraftTypeDesignator("")
                : aircraftData.typeDesignator();

        AircraftDescription aircraftDescription = (aircraftData == null) ? new AircraftDescription("")
                : aircraftData.description();

        WakeTurbulenceCategory wakeTurbulenceCategory =  (aircraftData == null) ? WakeTurbulenceCategory.UNKNOWN
                : aircraftData.wakeTurbulenceCategory();

        SVGPath iconPath = new SVGPath();

        ObservableValue<AircraftIcon> icon = state.categoryProperty().map(c -> AircraftIcon.iconFor(
                typeDesignator,
                aircraftDescription,
                c.intValue(),
                wakeTurbulenceCategory
        ));

        iconPath.getStyleClass().add("aircraft");
        iconPath.contentProperty().bind(icon.map(AircraftIcon :: svgPath));
        iconPath.fillProperty().bind(Bindings.createObjectBinding(() -> ColorRamp.colorFromPlasma(state.getAltitude()),
                state.altitudeProperty()));
        iconPath.rotateProperty().bind(
                Bindings.createDoubleBinding( () ->
                        (icon.getValue().canRotate()) ? Units.convertTo( state.getTrackOrHeading() , Units.Angle.DEGREE) : 0,
                                icon));
        return iconPath;
    }
}
