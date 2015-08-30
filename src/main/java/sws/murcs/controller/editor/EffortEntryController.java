package sws.murcs.controller.editor;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
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
     * The action button.
     */
    @FXML
    private Button actionButton;

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
     * Indicates whether the form has errors.
     */
    private boolean hasErrors;

    /**
     * A callback for when the add/remove button is clicked.
     */
    private Consumer<EffortEntryController> action;

    /**
     * Initializes the editor.
     */
    @FXML
    private void initialize() {
        datePicker.setValue(LocalDate.now());

        datePicker.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && datePicker.getValue() != effort.getDate()) {
                effort.setDate(datePicker.getValue());
            }
        });

        descriptionTextArea.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && descriptionTextArea.getText() != null && !descriptionTextArea.getText().equals(effort.getDescription())) {
                effort.setDescription(descriptionTextArea.getText());
            }
        });

        personComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != oldValue && newValue != null && !newValue.equals(effort.getPerson())) {
                effort.setPerson((Person)newValue);
            }
        });

        timeTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            //If we have selected the form, no need to update
            if (newValue) {
                return;
            }

            try {
                float time = Float.parseFloat(timeTextField.getText());
                if (effort.getEffort() != time) {
                    effort.setEffort(time);
                }
            } catch (Exception e) {
                //TODO display an error.
            }
        });
    }

    /**
     * Called when the action button is clicked.
     * @param event The event
     */
    @FXML
    private void actionButtonClicked(ActionEvent event) {
        if (action == null) return;
        action.accept(this);
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

        personComboBox.setValue(effort.getPerson());
        datePicker.setValue(effort.getDate());
        descriptionTextArea.setText(effort.getDescription());
        timeTextField.setText("" + effort.getEffort());
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
     * Sets the action for when the button is clicked.
     * @param action The action.
     */
    public void setOnAction(Consumer<EffortEntryController> action) {
        this.action = action;
    }

    /**
     * Indicates whether the form is valid.
     * @return whether the form is valid.
     */
    public boolean hasErrors() {
        return hasErrors || effort.getDate() == null || effort.getPerson() == null;
    }

    /**
     * Gets the eligible workers.
     */
    private List<Person> getEligibleWorkers() {
        return effortController.getEligibleWorkers();
    }

    /**
     * Makes the action button look like an add button.
     */
    public void styleAsAddButton() {
        actionButton.getStyleClass().addAll("mdga-button");
        actionButton.getStyleClass().removeAll("mdrd-button");
        actionButton.setText("+");
    }

    /**
     * Makes the action button look like a remove button.
     */
    public void styleAsRemoveButton() {
        actionButton.getStyleClass().addAll("mdrd-button");
        actionButton.getStyleClass().removeAll("mdga-button");
        actionButton.setText("X");
    }
}
