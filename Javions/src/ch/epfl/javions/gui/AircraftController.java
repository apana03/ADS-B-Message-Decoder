package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.WakeTurbulenceCategory;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;

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

    /**
     * Constructor for the aircraft controller
     * Adds listeners to the aircraft states
     * Calls the method to construct the aircraft group
     * @see #constructAircraftGroup(ObservableAircraftState)
     * @param mapParameters
     * @param aircraftStates
     * @param aircraftStateObjectProperty
     */
    public AircraftController(MapParameters mapParameters,
                              ObservableSet<ObservableAircraftState> aircraftStates,
                              ObjectProperty<ObservableAircraftState> aircraftStateObjectProperty) {

        this.mapParameters = mapParameters;
        this.aircraftStateObjectProperty = aircraftStateObjectProperty;
        pane = new Pane();
        pane.getStylesheets().add("/aircraft.css");
        pane.setPickOnBounds(false);

        this.aircraftStates = aircraftStates;
        aircraftStates.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
                    if(change.wasAdded())
                        constructAircraftGroup(change.getElementAdded());
                    else
                        pane.getChildren().removeIf(children -> children.getId().equals(change.getElementRemoved().getAddress().string()));
                });

    }

    /**
     * @return the pane
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Constructs aircraft group
     * Sets bindings for the aircraft group
     * @see #createIconAndTagGroup(ObservableAircraftState)
     * @see #trajectoryGroup(ObservableAircraftState)
     * @param state
     */
    private void constructAircraftGroup(ObservableAircraftState state) {
        Group aircraftGroup = new Group();
        aircraftGroup.setId(state.getAddress().string());
        aircraftGroup.viewOrderProperty().bind(state.altitudeProperty().negate());
        aircraftGroup.getChildren().addAll(trajectoryGroup(state),createIconAndTagGroup(state));
        pane.getChildren().add(aircraftGroup);
    }

    /**
     * Creates icon and tag group
     * Sets bindings for the icon and tag group
     * @see #getSVG(ObservableAircraftState)
     * @see #labelGroup(ObservableAircraftState)
     * @param state
     * @return
     */
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


        iconAndTag.setOnMousePressed(e->aircraftStateObjectProperty.set(state));

        return iconAndTag;
    }

    /**
     * Computes the color of the plane depending on its altitude
     * Computes the icon path depending on the category of the aircraft
     * @param state
     * @return the icon path depending on the altitude and category of the aircraft
     */
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
        iconPath.fillProperty().bind(Bindings.createObjectBinding(() -> getPlaneColor(state.altitudeProperty().get()),
                state.altitudeProperty()));
        iconPath.rotateProperty().bind(
                Bindings.createDoubleBinding(() ->
                                (icon.getValue().canRotate()) ? Units.convertTo(state.getTrackOrHeading(), Units.Angle.DEGREE) : 0,
                        state.trackOrHeadingProperty()));
        return iconPath;
    }

    /**
     * Adds a label to the aircraft
     * Sets the bindings for the label
     * @param state
     * @return label group for a given aircraft
     */
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

    /**
     * Computes the aircraft trajectory
     * Sets the bindings for the trajectory
     * Adds listeners to the trajectory
     * Draws the trajectory
     * @see #redrawTrajectory(Group, ObservableAircraftState)
     * @param state
     * @return
     */
    private Group trajectoryGroup(ObservableAircraftState state) {
        Group trajectoryGroup = new Group();
        trajectoryGroup.getStyleClass().add("trajectory");
        trajectoryGroup.layoutXProperty().bind(mapParameters.getMinX().negate());
        trajectoryGroup.layoutYProperty().bind(mapParameters.getMinY().negate());
        trajectoryGroup.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> state == aircraftStateObjectProperty.get(),
                aircraftStateObjectProperty));

        InvalidationListener trajectoryChangeListener = z -> redrawTrajectory(trajectoryGroup, state);


        mapParameters.getZoom().addListener(trajectoryChangeListener);

        trajectoryGroup.visibleProperty().addListener((o, oV, nV) -> {
                if(nV) {
                    redrawTrajectory(trajectoryGroup,state);
                    state.trajectoryProperty().addListener(trajectoryChangeListener);
                }else {
                    state.trajectoryProperty().removeListener(trajectoryChangeListener);
                }
            });
        return trajectoryGroup;
    }

    /**
     * Redraws the trajectory
     * @see #getAllTrajectoryLines(List)
     * @param trajectoryGroup
     * @param state
     */
    private void redrawTrajectory(Group trajectoryGroup, ObservableAircraftState state) {
        trajectoryGroup.getChildren().clear();
        trajectoryGroup.getChildren().addAll(getAllTrajectoryLines(state.getTrajectory()));
    }

    /**
     * @param airbornePositions
     * @return a list of lines representing the trajectory
     */
    private List<Line> getAllTrajectoryLines(List<ObservableAircraftState.AirbornePos> airbornePositions){
        ArrayList<Line> lines = new ArrayList<>();
        for(int i = 1; i<airbornePositions.size(); i++) {
            if (airbornePositions.get(i - 1).position() == null) continue;
            lines.add(createLine(airbornePositions.get(i - 1), airbornePositions.get(i)));
        }
        return lines;
    }

    /**
     * Creates a line between two points and gives it a certain color depending on the altitude
     * @see #getPlaneColor(double)
     * @param pos1
     * @param pos2
     * @return
     */
    private Line createLine(ObservableAircraftState.AirbornePos pos1, ObservableAircraftState.AirbornePos pos2) {
        GeoPos firstPoint = pos1.position();
        GeoPos secondPoint = pos2.position();
        Line line = new Line(WebMercator.x(mapParameters.getZoomValue() , firstPoint.longitude()),
                WebMercator.y(mapParameters.getZoomValue() , firstPoint.latitude()),
                WebMercator.x(mapParameters.getZoomValue() , secondPoint.longitude()),
                WebMercator.y(mapParameters.getZoomValue() , secondPoint.latitude())) ;

        if(pos1.altitude() == pos2.altitude()){
            line.setStroke( getPlaneColor(pos1.altitude()));
        }else{
            Stop s1 = new Stop(0, getPlaneColor(pos1.altitude()));
            Stop s2 = new Stop(1, getPlaneColor(pos2.altitude()));
            line.setStroke( new LinearGradient(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY(),
                    true, NO_CYCLE, s1, s2));
        }
        return line;
    }

    /**
     * @param state
     * @return the aircraft identification
     */
    private ObservableValue<String> aircraftIdentification(ObservableAircraftState state) {
        return (state.getData() != null) ? new SimpleStringProperty(state.getData().registration().string()) :
                Bindings.when(state.callSignProperty().isNotNull())
                        .then(Bindings.convert(state.callSignProperty().map(CallSign::string)))
                        .otherwise(state.getAddress().string());
    }

    /**
     * @param state
     * @return velocity of the aircraft
     */
    private ObservableValue<String> velocity(ObservableAircraftState state) {
        return state.velocityProperty().map(v -> (v.doubleValue() != 0 || !Double.isNaN(v.doubleValue())) ?
                String.format("%.0f", Units.convertTo(v.doubleValue(), Units.Speed.KILOMETER_PER_HOUR)) : "?");
    }

    /**
     * @param state
     * @return altitude of the aircraft
     */
    private ObservableValue<String> altitude(ObservableAircraftState state) {
        return state.altitudeProperty().map(v -> (v.doubleValue() != 0 || !Double.isNaN(v.doubleValue())) ?
                String.format("%.0f",v.doubleValue()) : "?");
    }

    /**
     * @param altitude
     * @return color of the aircraft depending on the altitude
     */
    private Color getPlaneColor(double altitude){
        return ColorRamp.PLASMA.at(Math.pow(altitude/12000, 1d/3d));
    }
}
