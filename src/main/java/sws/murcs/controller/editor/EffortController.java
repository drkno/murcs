package sws.murcs.controller.editor;

import java.time.LocalDate;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import sws.murcs.model.Effort;
import sws.murcs.model.Person;

/**
 * Controller for logging effort on a task.
 */
public class EffortController {

    /**
     * The person choice box.
     */
    @FXML
    private ComboBox personChoiceBox;

    /**
     * The date picker for setting when this effort was spent.
     */
    @FXML
    private DatePicker datePicker;

    /**
     * Effort and description text fields.
     */
    @FXML
    private TextField effortTextField, descriptionTextField;

    /**
     * The VBox that contains all effort entries.
     */
    @FXML
    private VBox effortsVBox;

    /**
     * The parent editor of this editor.
     */
    private TaskEditor parentEditor;

    /**
     * The list of people who are allowed to log time for this task.
     */
    private List<Person> eligibleWorkers;

    private Effort effort;

    /**
     * Sets up the effort controller.
     * @param parent The parent editor
     * @param people A list of people allowed to log time for this task
     */
    public void setUp(final TaskEditor parent, final List<Person> people) {
        effort = new Effort();

        parentEditor = parent;
        datePicker.setValue(LocalDate.now());
        eligibleWorkers = people;
    }

    /**
     * Called when the "Add" button is clicked.
     * @param event The event that called this method
     */
    @FXML
    private void addButtonClick(final ActionEvent event) {
        //parentEditor.getTask().logEffort(effort);
    }
}
