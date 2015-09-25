package sws.murcs.controller.controls;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javafx.scene.control.DatePicker;

/**
 * A class that helps you get the actual date out of a JavaFX
 * date picker, as the built in date doesn't update unless you
 * have pressed enter or picked the date using the popover.
 */
public class DatePickerHelper {
    /**
     * Gets the date PROPERLY from a JavaFX date picker.
     * @param picker The picker to get the date from.
     * @return The date selected in the picker.
     */
    public static LocalDate getDate(DatePicker picker) {
        try {
            return picker.getConverter().fromString(picker.getEditor().getText());
        } catch (DateTimeParseException e) {
            return picker.getValue();
        }
    }
}
