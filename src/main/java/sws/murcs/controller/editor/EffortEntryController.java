package sws.murcs.controller.editor;

import java.time.LocalDate;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sws.murcs.model.Effort;
import sws.murcs.model.Person;

/**
 * A controller for editing effort entries
 */
public class EffortEntryController {
    /**
     * The text field containing time spent.
     */
    @FXML
    private TextField timeTextField;

    /**
     * The description text area.
     */
    @FXML
    private TextArea descriptionTextArea;

    /**
     * The date picker.
     */
    @FXML
    private DatePicker datePicker;

    /**
     * The choice box for deciding who logged the time.
     */
    @FXML
    private ComboBox personComboBox;

    /**
     * The root node of this editor.
     */
    private Parent root;

    /**
     * The associated effort controller.
     */
    private EffortController effortController;

    /**
     * The effort being edited by this controller.
     */
    private Effort effort;

    /**
     * Initializes the editor.
     */
    @FXML
    private void initialize() {
        datePicker.setValue(LocalDate.now());
    }

    /**
     * Sets the root node for this editor.
     * @param root The root node for this editor.
     */
    void setRoot(final Parent root) {
        this.root = root;
    }

    /**
     * Gets the root for this editor.
     * @return The root node for this editor.
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * Sets the effort this controller is editing.
     * @param effort The effort for this controller to edit.
     */
    public void setEffort(final Effort effort) {
        this.effort = effort;
    }

    /**
     * The effort this controller edits.
     * @return The effort.
     */
    public Effort getEffort() {
        return effort;
    }

    /**
     * Sets the effort controller.
     * @param controller The effort controller.
     */
    public void setEffortController(EffortController controller) {
        this.effortController = controller;

        personComboBox.getItems().clear();
        personComboBox.getItems().addAll(getEligibleWorkers());
    }

    /**
     * Indicates whether the form is valid.
     * @return whether the form is valid.
     */
    public boolean hasErrors() {
        return false;
    }

    /**
     * Gets the eligible workers.
     */
    private List<Person> getEligibleWorkers() {
        return effortController.getEligibleWorkers();
    }
}
