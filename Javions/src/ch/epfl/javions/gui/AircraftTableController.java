package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.text.NumberFormat;
import java.util.function.Consumer;

public final class AircraftTableController
{
    private final ObservableSet<ObservableAircraftState> states;
    private final ObjectProperty<ObservableAircraftState> selectedState;
    private TableView<ObservableAircraftState> table;
    public AircraftTableController(ObservableSet<ObservableAircraftState> states, ObjectProperty<ObservableAircraftState> selectedState)
    {
        this.states = states;
        this.selectedState = selectedState;
        table = new TableView<ObservableAircraftState>();
        table.getStylesheets().add("/table.css");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        table.setTableMenuButtonVisible(true);
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
    private void constructAircraftColumns() {
        NumberFormat formatter = NumberFormat.getInstance();
        TableColumn<ObservableAircraftState, String> addressColumn = new TableColumn<>("OACI");
        addressColumn.setPrefWidth(60);
        addressColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getAddress()).map(IcaoAddress::string));
        TableColumn<ObservableAircraftState, String> callsignColumn = new TableColumn<>("Indicatif");
        callsignColumn.setCellValueFactory(cellData -> cellData.getValue().callSignProperty().map(CallSign::string));
        callsignColumn.setPrefWidth(70);
        TableColumn<ObservableAircraftState, String> immatriculationColumn = new TableColumn<>("Immatriculation");
        immatriculationColumn.setPrefWidth(90);
        immatriculationColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getData()).map(AircraftData::registration)
                        .map(AircraftRegistration::string));
        TableColumn<ObservableAircraftState, String> modelColumn = new TableColumn<>("Modèle");
        modelColumn.setPrefWidth(230);
        modelColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getData()).map(AircraftData::model));
        TableColumn<ObservableAircraftState, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setPrefWidth(50);
        typeColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getData()).map(AircraftData::typeDesignator)
                        .map(AircraftTypeDesignator::string));
        TableColumn<ObservableAircraftState, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setPrefWidth(70);
        descriptionColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getData()).map(AircraftData::description)
                        .map(AircraftDescription::string));
        TableColumn<ObservableAircraftState, String> longitudeColumn = new TableColumn<>("Longitude");
        longitudeColumn.getStyleClass().add("numeric");
        longitudeColumn.setPrefWidth(90);
        formatter.setMaximumFractionDigits(4);
        longitudeColumn.setCellValueFactory(cellData ->
                cellData.getValue().positionProperty().map(GeoPos::longitude).map(formatter::format));
        TableColumn<ObservableAircraftState, String> latitudeColumn = new TableColumn<>("Latitude");
        latitudeColumn.getStyleClass().add("numeric");
        latitudeColumn.setPrefWidth(90);
        latitudeColumn.setCellValueFactory(cellData ->
                cellData.getValue().positionProperty().map(GeoPos::latitude).map(formatter::format));
        TableColumn<ObservableAircraftState, String> altitudeColumn = new TableColumn<>("Altitude");
        altitudeColumn.getStyleClass().add("numeric");
        altitudeColumn.setPrefWidth(90);
        altitudeColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(formatter.format(cellData.getValue().altitudeProperty().doubleValue())));
        TableColumn<ObservableAircraftState, String> velocityColumn = new TableColumn<>("Vitesse (km/h)");
        velocityColumn.getStyleClass().add("numeric");
        velocityColumn.setPrefWidth(90);
        formatter.setMaximumFractionDigits(0);
        velocityColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(formatter.format(cellData.getValue().velocityProperty().doubleValue())));
        table.getColumns().addAll(addressColumn, callsignColumn, immatriculationColumn, modelColumn, typeColumn,
                descriptionColumn, longitudeColumn, latitudeColumn, altitudeColumn, velocityColumn);
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
