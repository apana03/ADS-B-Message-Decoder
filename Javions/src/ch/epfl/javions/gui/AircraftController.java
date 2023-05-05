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
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;


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


    private Group createIconAndTagGroup(ObservableAircraftState state) {
        Group iconAndTag = new Group(getSVG(state), labelGroup(state));
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

        WakeTurbulenceCategory wakeTurbulenceCategory = (aircraftData == null) ? WakeTurbulenceCategory.UNKNOWN
                : aircraftData.wakeTurbulenceCategory();

        SVGPath iconPath = new SVGPath();

        ObservableValue<AircraftIcon> icon = state.categoryProperty().map(c -> AircraftIcon.iconFor(
                typeDesignator,
                aircraftDescription,
                c.intValue(),
                wakeTurbulenceCategory
        ));

        iconPath.getStyleClass().add("aircraft");
        iconPath.contentProperty().bind(icon.map(AircraftIcon::svgPath));
        iconPath.fillProperty().bind(Bindings.createObjectBinding(() ->  ColorRamp.colorFromPlasma(state.altitudeProperty().get()),
                state.altitudeProperty()));
        iconPath.rotateProperty().bind(
                Bindings.createDoubleBinding(() ->
                                (icon.getValue().canRotate()) ? Units.convertTo(state.getTrackOrHeading(), Units.Angle.DEGREE) : 0,
                        state.trackOrHeadingProperty()));
        return iconPath;
    }

    private Group labelGroup(ObservableAircraftState state) {
        Text text = new Text();
        Rectangle rectangle = new Rectangle();

        rectangle.widthProperty().bind(
                text.layoutBoundsProperty().map(b -> b.getWidth() + 4));

        rectangle.heightProperty().bind(
                text.layoutBoundsProperty().map(b -> b.getHeight() + 4));

        text.textProperty().bind(
                Bindings.format("%s \n %s km/h\u2002%1.0f m",
                        aircraftIdentification(state),
                        velocity(state),
                        state.altitudeProperty()
                )
        );

        Group labelGroup = new Group(rectangle, text);
        labelGroup.getStyleClass().add("label");

        return labelGroup;
    }

    private String aircraftIdentification(ObservableAircraftState state) {
        return (state.getData().registration() == null) ?
                (state.getCallSign() == null) ? state.getAddress().string() : state.getCallSign().string()
                : state.getData().registration().string();
    }

    private Object velocity(ObservableAircraftState state) {
    return state.velocityProperty().map(v -> (v.doubleValue() != 0 || Double.isNaN(v.doubleValue())) ?
                (int) Units.convertTo(v.doubleValue(), Units.Speed.KILOMETER_PER_HOUR) : "?");
    }

    private String altitude(ObservableAircraftState state) {
        return state.altitudeProperty().map(v -> (v.doubleValue() != 0 || !Double.isNaN(v.doubleValue())) ?
                v.doubleValue() : "?").toString();
    }
}
