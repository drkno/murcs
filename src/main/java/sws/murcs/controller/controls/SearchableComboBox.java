package sws.murcs.controller.controls;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import java.util.Optional;

/**
 * A searchable/autocomplete ComboBox to make finding items more achievable.
 * @param <T> type of the ComboBox.
 */
public class SearchableComboBox<T> {
    /**
     * The ComboBox this affects.
     */
    private ComboBox<T> comboBox;

    /**
     * Data contained within the ComboBox.
     */
    private ObservableList<T> data;

    /**
     * Event called when a keyboard key is pressed.
     */
    private EventHandler<KeyEvent> keyEvent;

    /**
     * Event called when a mouse click occurs.
     */
    private EventHandler<MouseEvent> mouseEvent;

    /**
     * Converter to convert string's into generics.
     */
    private StringConverter<T> converter;

    /**
     * Instantiates a new searchable ComboBox.
     * @param aComboBox the ComboBox this affects.
     */
    public SearchableComboBox(final ComboBox<T> aComboBox) {
        comboBox = aComboBox;
        data = comboBox.getItems();
        comboBox.setEditable(true);
        keyEvent = createKeyEventHandler();
        comboBox.setOnKeyReleased(keyEvent);
        mouseEvent = createMouseEventHandler();
        comboBox.getEditor().setOnMouseClicked(mouseEvent);
        converter = createStringConverter();
        comboBox.setConverter(converter);
        comboBox.setTooltip(new Tooltip(comboBox.getPromptText()));
    }

    /**
     * Disposes of the current SearchableComboBox.
     */
    public final void dispose() {
        comboBox.setOnKeyPressed(null);
        keyEvent = null;
        comboBox.getEditor().setOnMouseClicked(null);
        mouseEvent = null;
        comboBox.setConverter(null);
        converter = null;
    }

    /**
     * Creates a new string converter to convert from a string to a generic.
     * @return string converter.
     */
    private StringConverter<T> createStringConverter() {
        return new StringConverter<T>() {
            @Override
            public String toString(final T object) {
                if (object == null) {
                    return "";
                }
                return object.toString();
            }

            @Override
            public T fromString(final String string) {
                Optional stream = data.stream().filter(i -> i.toString().equalsIgnoreCase(string)).findFirst();
                if (stream.isPresent()) {
                    return (T) stream.get();
                }
                return null;
            }
        };
    }

    /**
     * Creates a new mouse click event handler.
     * @return new mouse click event handler.
     */
    private EventHandler<MouseEvent> createMouseEventHandler() {
        return event -> {
            if (!comboBox.isShowing()) {
                comboBox.show();
            }
        };
    }

    /**
     * Creates a handler for key press events.
     * @return new key event handler.
     */
    private EventHandler<KeyEvent> createKeyEventHandler() {
        return event -> {
            if (!comboBox.isShowing()) {
                comboBox.show();
            }

            String text = comboBox.getEditor().getText();
            KeyCode code = event.getCode();
            if (code == KeyCode.UP || code == KeyCode.DOWN || code == KeyCode.ENTER) {
                positionCursor(text.length());
                return;
            }
            else if ((code == KeyCode.TAB || code == KeyCode.ENTER || code == KeyCode.CONTROL)
                    && comboBox.getItems().size() > 0) {
                comboBox.getEditor().setText(comboBox.getItems().get(0).toString());
                event.consume();
                return;
            }

            if (code == KeyCode.RIGHT || code == KeyCode.LEFT
                    || event.isControlDown() || code == KeyCode.HOME
                    || code == KeyCode.END) {
                return;
            }

            ObservableList list = FXCollections.observableArrayList();
            String typedInput = text.toLowerCase();
            data.forEach(d -> {
                if (d.toString().toLowerCase().contains(typedInput)) {
                    list.add(d);
                }
            });

            comboBox.setItems(list);

            if (list.size() == 1 && code != KeyCode.DELETE) {
                text = list.get(0).toString();
                if (text.toLowerCase().startsWith(typedInput)) {
                    int pos = comboBox.getEditor().getCaretPosition();
                    if (code == KeyCode.BACK_SPACE) {
                        pos -= 1;
                    }
                    comboBox.getEditor().setText(text);
                    comboBox.getEditor().selectRange(pos, text.length());
                }
            }
        };
    }

    /**
     * Moves the text cursor to a position.
     * @param position position of the cursor.
     */
    private void positionCursor(final int position) {
        comboBox.getEditor().positionCaret(position);
    }
}
