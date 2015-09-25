package sws.murcs.controller.editor;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import sws.murcs.controller.controls.popover.ArrowLocation;
import sws.murcs.controller.controls.popover.PopOver;
import sws.murcs.controller.pipes.PersonManagerControllerParent;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.internationalization.AutoLanguageFXMLLoader;
import sws.murcs.internationalization.InternationalizationHelper;
import sws.murcs.model.EffortEntry;
import sws.murcs.model.Person;
import sws.murcs.model.PersonMaintainer;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

/**
 * A controller for editing effort entries.
 */
public class EffortEntryController implements PersonManagerControllerParent {
    /**
     * The column labels. We have ids for them because we add and remove them.
     */
    @FXML
    private Label dateLabel, personLabel, timeLabel, personsLabel;

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
     * The action button.
     */
    @FXML
    private Button actionButton, editPeopleButton;

    /**
     * The main grid.
     */
    @FXML private GridPane mainGrid;

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
    private EffortEntry effortEntry;

    /**
     * Indicates whether the form has errors.
     */
    private SimpleBooleanProperty hasErrorsProperty = new SimpleBooleanProperty(true);

    /**
     * A callback for when the add/remove button is clicked.
     */
    private Consumer<EffortEntryController> action;

    /**
     * Add icon for button.
     */
    private ImageView addIcon = new ImageView(new Image("sws/murcs/icons/addWhite.png"));

    /**
     * Remove icon for button.
     */
    private ImageView removeIcon = new ImageView(new Image("sws/murcs/icons/removeWhite.png"));

    /**
     * Initializes the editor.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    @FXML
    private void initialize() {
        datePicker.setValue(LocalDate.now());

        datePicker.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) return;

            update();
            updateErrors();
        });

        descriptionTextArea.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) return;

            update();
            updateErrors();
        });

        timeTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) return;

            update();
            updateErrors();
        });

        //Update the errors as we type.
        descriptionTextArea.textProperty().addListener((observable, oldValue, newValue) ->
                updateErrors());
        timeTextField.textProperty().addListener((observable, oldValue, newValue) -> updateErrors());

        addIcon.setFitHeight(25);
        addIcon.setFitWidth(25);
        addIcon.setPreserveRatio(true);
        addIcon.setPickOnBounds(true);

        removeIcon.setFitHeight(25);
        removeIcon.setFitWidth(25);
        removeIcon.setPreserveRatio(true);
        removeIcon.setPickOnBounds(true);
    }

    /**
     * Updates the model object in memory.
     */
    private void update() {
        if (effortEntry == null) {
            return;
        }

        if (datePicker.getValue() != effortEntry.getDate()) {
            effortEntry.setDate(datePicker.getValue());
        }

        if (descriptionTextArea.getText() != null
                && !descriptionTextArea.getText().equals(effortEntry.getDescription())
                && !descriptionTextArea.getText().isEmpty()) {
            effortEntry.setDescription(descriptionTextArea.getText());
        }

        try {
            float time = Float.parseFloat(timeTextField.getText());
            if (effortEntry.getEffort() != time) {
                effortEntry.setEffort(time);
            }
        }
        finally {
            updatePeopleLabel();
        }
    }

    /**
     * Updates the errors on the form.
     */
    protected void updateErrors() {
        boolean notEdited = datePicker.getValue() == null
                && (descriptionTextArea.getText() == null || descriptionTextArea.getText().isEmpty())
                && effortEntry.getPeople().size() == 0
                && timeTextField.getText().equals("0.0");
        //If we haven't touched the form yet, don't highlight errors but set the flag.
        if (notEdited || effortEntry == null) {
            hasErrorsProperty.set(true);
            return;
        }

        boolean errors = false;

        if (datePicker.getValue() == null || effortEntry.getDate() == null) {
            datePicker.getStyleClass().add("error");
            errors = true;
        } else {
            datePicker.getStyleClass().removeAll("error");
        }

        if (descriptionTextArea.getText() == null || descriptionTextArea.getText().isEmpty()) {
            descriptionTextArea.getStyleClass().add("error");
            errors = true;
        } else {
            descriptionTextArea.getStyleClass().removeAll("error");
        }

        if (effortEntry.getPeople().size() == 0) {
            personsLabel.getStyleClass().add("error");
            errors = true;
        } else {
            personsLabel.getStyleClass().removeAll("error");
        }

        boolean validTime = true;
        try {
            float time = Float.parseFloat(timeTextField.getText());
            if (time < 0) {
                validTime = false;
            }
        }
        catch (Exception e) {
            validTime = false;
        }
        finally {
            if (!validTime) {
                timeTextField.getStyleClass().add("error");
                errors = true;
            } else {
                timeTextField.getStyleClass().removeAll("error");
            }
        }

        hasErrorsProperty.set(errors);
    }

    /**
     * Called when the action button is clicked.
     * @param event The event
     */
    @FXML
    private void actionButtonClicked(final ActionEvent event) {
        update();
        updateErrors();

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
     * @param effortEntry The effort for this controller to edit.
     */
    public void setEffortEntry(final EffortEntry effortEntry) {
        this.effortEntry = effortEntry;

        datePicker.setValue(effortEntry.getDate());
        descriptionTextArea.setText(effortEntry.getDescription());
        timeTextField.setText(Float.toString(effortEntry.getSetEffort()));
        updatePeopleLabel();

        Platform.runLater(this::updateErrors);
    }

    /**
     * The effort this controller edits.
     * @return The effort.
     */
    public EffortEntry getEffortEntry() {
        return effortEntry;
    }

    /**
     * Sets the effort controller.
     * @param controller The effort controller.
     */
    public void setEffortController(final EffortController controller) {
        this.effortController = controller;
    }

    /**
     * Sets the action for when the button is clicked.
     * @param action The action.
     */
    public void setOnAction(final Consumer<EffortEntryController> action) {
        this.action = action;
    }

    /**
     * Disables or enables the action button.
     * @param disableAction Whether the action button should be disabled.
     */
    public void setActionDisabled(final boolean disableAction) {
        actionButton.setDisable(disableAction);
    }

    /**
     * Indicates whether the form is valid.
     * @return whether the form is valid.
     */
    public SimpleBooleanProperty getHasErrorsProperty() {
        return hasErrorsProperty;
    }

    /**
     * Gets the eligible workers.
     * @return The eligible workers
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
        actionButton.setGraphic(addIcon);

        mainGrid.getRowConstraints().get(0).setPrefHeight(Control.USE_COMPUTED_SIZE);
        mainGrid.getChildren().addAll(dateLabel, personLabel, timeLabel);
    }

    /**
     * Makes the action button look like a remove button.
     */
    public void styleAsRemoveButton() {
        actionButton.getStyleClass().addAll("mdrd-button");
        actionButton.getStyleClass().removeAll("mdga-button");
        actionButton.setGraphic(removeIcon);

        mainGrid.getRowConstraints().get(0).setPrefHeight(0);
        mainGrid.getChildren().removeAll(dateLabel, personLabel, timeLabel);
    }

    @Override
    public void addPerson(final Person person) {
        effortEntry.addPerson(person);
        updatePeopleLabel();
    }

    /**
     * Updates the people label to have the correct names on it.
     */
    public void updatePeopleLabel() {
        if (effortEntry != null) {
            personsLabel.setText(effortEntry.getPeople().size() > 0 ? effortEntry.getPeopleAsString() : InternationalizationHelper.tryGet("NoPeople"));
        }
    }

    @Override
    public void removePerson(final Person person) {
        effortEntry.removePerson(person);
        updatePeopleLabel();
    }

    @Override
    public PersonMaintainer getMaintainer() {
        return effortEntry;
    }

    /**
     * The event called when you want to edit the people who are having the effort logged against them.
     * @param event the click on the edit button.
     */
    @FXML
    private void editPeopleButtonClicked(final ActionEvent event) {
        FXMLLoader loader = new AutoLanguageFXMLLoader();
        loader.setLocation(TaskEditor.class.getResource("/sws/murcs/PersonManagerPopOver.fxml"));

        try {
            Parent parent = loader.load();
            PopOver peoplePopOver = new PopOver(parent);
            PersonManagerController controller = loader.getController();
            controller.setUp(this, getEligibleWorkers());
            peoplePopOver.hideOnEscapeProperty().setValue(true);
            peoplePopOver.showingProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    updatePeopleLabel();
                }
            });
            peoplePopOver.arrowLocationProperty().setValue(ArrowLocation.RIGHT_CENTER);
            peoplePopOver.show(editPeopleButton);
        }
        catch (IOException e) {
            ErrorReporter.get().reportError(e, "Could not create an people popover");
        }
    }
}
