package ch.epfl.javions.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public final class StatusLineController {
    private final BorderPane borderPane;
    private final IntegerProperty aircraftCountProperty;
    private final LongProperty messageCountProperty;

    public StatusLineController(){
        borderPane = new BorderPane();
        aircraftCountProperty = new SimpleIntegerProperty();
        messageCountProperty = new SimpleLongProperty();
        borderPane.getStylesheets().add("status.css");
        borderPane.setLeft(aircraftCountText());
        borderPane.setRight(messageCountText());
    }

    public Pane pane(){
        return borderPane;
    }

    public IntegerProperty getAircraftCountProperty(){
        return aircraftCountProperty;
    }

    public LongProperty getMessageCountProperty(){
        return messageCountProperty;
    }

    private Text aircraftCountText(){
        Text aircraftCountText = new Text();
        aircraftCountText.textProperty().bind(Bindings.format("Aéronefs visibles : %d", aircraftCountProperty));
        return aircraftCountText;
    }

    private Text messageCountText(){
        Text messageCountText = new Text();
        messageCountText.textProperty().bind(Bindings.format("Messages reçus : %d", messageCountProperty));
        return messageCountText;
    }
}
