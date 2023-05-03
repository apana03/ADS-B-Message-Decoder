package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.CallSign;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableSet;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import java.util.function.Consumer;

public final class AircraftTableController
{
    ObservableSet<ObservableAircraftState> states;
    ObjectProperty<ObservableAircraftState> selectedState;
    TableView table;
    public AircraftTableController(ObservableSet<ObservableAircraftState> states, ObjectProperty<ObservableAircraftState> selectedState)
    {
        this.states = states;
        this.selectedState = selectedState;
        table = new TableView();
        table.getStylesheets().add("/table.css");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        table.setTableMenuButtonVisible(true);
    }
    public TableView pane()
    {
        TableColumn<ObservableAircraftState, String> addressColumn = new TableColumn<>("OACI");
        addressColumn.setPrefWidth(60);
        addressColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getAddress().string()));
        TableColumn<ObservableAircraftState, String> callsignColumn = new TableColumn<>("Indicatif");
        callsignColumn.setPrefWidth(70);
        //callsignColumn.setCellValueFactory(cellData -> cellData.getValue().callSignProperty().map(CallSign::string));
        TableColumn<ObservableAircraftState, String> immatriculationColumn = new TableColumn<>("Immatriculation");
        immatriculationColumn.setPrefWidth(90);
        immatriculationColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getData().registration().string()));
        TableColumn<ObservableAircraftState, String> modelColumn = new TableColumn<>("Modèle");
        modelColumn.setPrefWidth(230);
        modelColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getData().model()));
        TableColumn<ObservableAircraftState, String> altitudeColumn = new TableColumn<>("Altitude");
        return table;
    }
    public void setOnDoubleClick(Consumer<ObservableAircraftState> consumer){
        table.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2 && event.getButton() == javafx.scene.input.MouseButton.PRIMARY){
                ObservableAircraftState state = (ObservableAircraftState) table.getSelectionModel().getSelectedItem();
                if(state != null){
                    consumer.accept(state);
                }
            }
        });
    }
}
