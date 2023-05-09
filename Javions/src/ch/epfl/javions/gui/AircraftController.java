package ch.epfl.javions.gui;


import ch.epfl.javions.GeoPos;
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
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.paint.CycleMethod.NO_CYCLE;


public final class AircraftController {
    private final Pane pane;
    private final MapParameters mapParameters;
    private final ObservableSet<ObservableAircraftState> aircraftStates;
    private final ObjectProperty<ObservableAircraftState> aircraftStateObjectProperty;


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
                    if(change.wasAdded())
                        constructAircraftGroup(change.getElementAdded());
                    else
                        pane.getChildren().removeIf(children -> children.equals(change.getElementRemoved()));
                });



        mapParameters.getZoom().addListener(b -> {
            List<Node> children = pane.getChildren();
            Group wantedGroup = new Group();
            ObservableAircraftState currentState = aircraftStateObjectProperty.get();
            if (currentState ==null)
                return;
            for(Node node : children){
                if(node.idProperty().get().equals(currentState.getAddress().string())) {
                    wantedGroup = (Group) node;
                    break;
                }
            }
            Group trajectoryGroup = (Group)wantedGroup.getChildren().get(1);
            trajectoryGroup.getChildren().clear();
            trajectoryGroup.getChildren().addAll(drawlines(currentState.getTrajectory()));
        });
    }

    public Pane pane() {
        return pane;
    }
    private void constructAircraftGroup(ObservableAircraftState state) {
        Group aircraftGroup = new Group();
        aircraftGroup.setId(state.getAddress().string());
        aircraftGroup.viewOrderProperty().bind(state.altitudeProperty().negate());
        aircraftGroup.getChildren().addAll(createIconAndTagGroup(state),trajectoryGroup(state));
        pane.getChildren().add(aircraftGroup);
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


        iconAndTag.setOnMousePressed(e ->{
            aircraftStateObjectProperty.set(state);
        });
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
        iconPath.fillProperty().bind(Bindings.createObjectBinding(() -> ColorRamp.colorFromPlasma(state.altitudeProperty().get()),
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
                Bindings.format("%s \n %s km/h\u2002%s m",
                        aircraftIdentification(state),
                        velocity(state),
                        altitude(state)
                )
        );
        Group labelGroup = new Group(rectangle, text);
        labelGroup.getStyleClass().add("label");
        labelGroup.visibleProperty().bind(
                aircraftStateObjectProperty.isEqualTo(state)
                        .or(mapParameters.getZoom().greaterThanOrEqualTo(11))
        );

        return labelGroup;
    }

    private Group trajectoryGroup(ObservableAircraftState state) {
        Group trajectoryGroup = new Group();
        trajectoryGroup.getStyleClass().add("trajectory");
        trajectoryGroup.layoutXProperty().bind(mapParameters.getMinX().negate());
        trajectoryGroup.layoutYProperty().bind(mapParameters.getMinY().negate());
        trajectoryGroup.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> state == aircraftStateObjectProperty.get(),
                aircraftStateObjectProperty));
        trajectoryGroup.visibleProperty().addListener((o, oV, nV) -> {
            trajectoryGroup.getChildren().clear();
            trajectoryGroup.getChildren().addAll(drawlines(state.getTrajectory()));
            });
        return trajectoryGroup;
    }

    private List<Line> drawlines(List<ObservableAircraftState.AirbornePos> airbornePositions){
        ArrayList<Line> lines = new ArrayList<>();
        for(int i = 1; i<airbornePositions.size(); i++) {
            if (airbornePositions.get(i - 1).position() == null) continue;
            lines.add(createLine(airbornePositions.get(i - 1), airbornePositions.get(i)));
        }
        return lines;
    }

    private Line createLine(ObservableAircraftState.AirbornePos pos1, ObservableAircraftState.AirbornePos pos2) {
        int zoom = mapParameters.getZoomValue();
        GeoPos firstPoint = pos1.position();
        GeoPos secondPoint = pos2.position();
        Line line = new Line(
                WebMercator.x(zoom, firstPoint.longitude()),
                WebMercator.y(zoom, firstPoint.latitude()),
                WebMercator.x(zoom, secondPoint.longitude()),
                WebMercator.y(zoom, secondPoint.latitude())
        ) ;

        if(pos1.altitude() == pos2.altitude()){
            line.setStroke(ColorRamp.colorFromPlasma(pos1.altitude()));
        }else{
            Stop s1 = new Stop(0,ColorRamp.colorFromPlasma(pos1.altitude()));
            Stop s2 = new Stop(1, ColorRamp.colorFromPlasma(pos2.altitude()));
            line.setStroke( new LinearGradient(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY(),
                    true, NO_CYCLE, s1, s2));
        }
        return line;
    }

    private String aircraftIdentification(ObservableAircraftState state) {
        return (state.getData() == null) ?
                (state.getCallSign() == null) ? state.getAddress().string() : state.getCallSign().string()
                : state.getData().registration().string();
    }

    private Object velocity(ObservableAircraftState state) {
        return state.velocityProperty().map(v -> (v.doubleValue() != 0 || !Double.isNaN(v.doubleValue())) ?
                (int) Units.convertTo(v.doubleValue(), Units.Speed.KILOMETER_PER_HOUR) : "?");
    }

    private Object altitude(ObservableAircraftState state) {
        return state.altitudeProperty().map(v -> (v.doubleValue() != 0 || !Double.isNaN(v.doubleValue())) ?
                String.format("%1.0f",v.doubleValue()) : "?");
    }
}
