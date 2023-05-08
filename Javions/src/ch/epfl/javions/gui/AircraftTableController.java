package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.text.NumberFormat;
import java.util.function.Consumer;
import java.util.function.Function;

public final class AircraftTableController
{
    private final ObservableSet<ObservableAircraftState> states;
    private final ObjectProperty<ObservableAircraftState> selectedState;
    private TableView<ObservableAircraftState> table;
    public AircraftTableController(ObservableSet<ObservableAircraftState> states, ObjectProperty<ObservableAircraftState> selectedState)
    {
        this.states = states;
        this.selectedState = selectedState;
        table = new TableView<>();
        table.getStylesheets().add("/table.css");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        table.setTableMenuButtonVisible(true);

        //listeners
        selectedState.addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                table.getSelectionModel().select(newValue);
                if(!oldValue.equals(newValue)) {
                    table.scrollTo(newValue);
                }
            }
        });
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                selectedState.set(newValue);
            }
        });
        states.addListener((SetChangeListener<ObservableAircraftState>)
                change -> {
                    if(change.wasAdded()) {
                        table.getItems().add(change.getElementAdded());
                        table.sort();
                    }
                    else {
                        table.getItems().removeIf(children -> children.equals(change.getElementRemoved()));
                    }
                });
        constructAircraftColumns();
    }

    /**
     * How can I rewrite this as a private method which constructs every type of column?
     * What parameters should i put in the method?
     */
    private void constructAircraftColumns() {
        createAndAddTextColumns();
        createAndAddNumericColumns();
    }
    public TableColumn<ObservableAircraftState, String> generateTextColumn(String name, int width,
                                                                           Function<ObservableAircraftState,
                                                                                   ObservableValue<String>> function)
    {
        TableColumn<ObservableAircraftState, String> column = new TableColumn<>(name);
        column.setPrefWidth(width);
        column.setCellValueFactory(cellData -> function.apply(cellData.getValue()));
        return column;
    }
    public TableColumn<ObservableAircraftState, String> generateNumericColumn(String name, int width,
                                                                              Function<ObservableAircraftState,
                                                                                      ObservableValue<String>> function)
    {
        TableColumn<ObservableAircraftState, String> column = new TableColumn<>(name);
        column.getStyleClass().add("numeric");
        column.setPrefWidth(90);
        column.setCellValueFactory(cellData -> function.apply(cellData.getValue()));
        return column;
    }
    public void createAndAddTextColumns(){
        TableColumn<ObservableAircraftState, String> addressColumn = generateTextColumn("OACI", 60,
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getAddress()).map(IcaoAddress::string));
        TableColumn<ObservableAircraftState, String> callSignColumn = generateTextColumn("Indicatif", 70,
                cellData -> cellData.callSignProperty().map(CallSign::string));
        TableColumn<ObservableAircraftState, String> registrationColumn = generateTextColumn("Immatriculation",
                90, cellData -> new ReadOnlyObjectWrapper<>(cellData.getData()).map(AircraftData::registration)
                        .map(AircraftRegistration::string));
        TableColumn<ObservableAircraftState, String> modelColumn = generateTextColumn("Modèle", 230,
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getData()).map(AircraftData::model));
        TableColumn<ObservableAircraftState, String> typeColumn =generateTextColumn("Type", 50,
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getData()).map(AircraftData::typeDesignator)
                        .map(AircraftTypeDesignator::string));
        TableColumn<ObservableAircraftState, String> descriptionColumn = generateTextColumn("Description", 70,
                cellData -> new ReadOnlyObjectWrapper<>(cellData.getData()).map(AircraftData::description)
                        .map(AircraftDescription::string));
        table.getColumns().addAll(addressColumn, callSignColumn, registrationColumn, modelColumn, typeColumn,
                descriptionColumn);
    }
    public void createAndAddNumericColumns(){
        NumberFormat formatter = NumberFormat.getInstance();
        NumberFormat velocityFormatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(4);
        TableColumn<ObservableAircraftState, String> longitudeColumn = generateNumericColumn("Longitude", 90,
                cellData -> cellData.positionProperty().map(k -> Units.convertTo(k.longitude(), Units.Angle.DEGREE))
                        .map(formatter::format));
        TableColumn<ObservableAircraftState, String> latitudeColumn = generateNumericColumn("Latitude", 90,
                cellData -> cellData.positionProperty().map(k -> Units.convertTo(k.latitude(), Units.Angle.DEGREE))
                        .map(formatter::format));
        TableColumn<ObservableAircraftState, String> altitudeColumn = generateNumericColumn("Altitude (m)", 90,
                cellData -> cellData.altitudeProperty().map(formatter::format));
        TableColumn<ObservableAircraftState, String> velocityColumn = generateNumericColumn("Vitesse (km/h)", 90,
                cellData -> cellData.velocityProperty().map(velocityFormatter::format));
        table.getColumns().addAll(longitudeColumn, latitudeColumn, altitudeColumn, velocityColumn);
    }
    public TableView<ObservableAircraftState> pane()
    {
        return table;
    }
    public void setOnDoubleClick(Consumer<ObservableAircraftState> consumer){
        table.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2 && event.getButton() == javafx.scene.input.MouseButton.PRIMARY){
                ObservableAircraftState state = table.getSelectionModel().getSelectedItem();
                if(state != null){
                    consumer.accept(state);
                }
            }
        });
    }
}
