package sws.murcs.controller.editor;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sws.murcs.exceptions.CustomException;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Backlog;
import sws.murcs.model.Person;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.Skill;
import sws.murcs.model.Story;
import sws.murcs.model.persistence.PersistenceManager;

import java.util.List;
import java.util.Observable;
import java.util.stream.Collectors;

/**
 * Controller for the model creator popup window.
 * Since there should only be one instance of this PopUp
 */
public class BacklogEditor extends GenericEditor<Backlog> {

    /**
     *
     */
    @FXML
    private TextField shortNameTextField, longNameTextField, priorityTextField;

    /**
     *
     */
    @FXML
    private TextArea descriptionTextArea;

    /**
     *
     */
    @FXML
    private ChoiceBox<Person> poChoiceBox;

    /**
     *
     */
    @FXML
    private ChoiceBox<Story> storyPicker;

    /**
     *
     */
    @FXML
    private TableView<Story> storyTable;

    /**
     *
     */
    @FXML
    private TableColumn<Story, String> storyColumn;

    /**
     *
     */
    @FXML
    private TableColumn<Story, Object> deleteColumn;

    /**
     *
     */
    @FXML
    private Label labelErrorMessage;

    /**
     * An observable list of backlog stories.
     */
    private ObservableList<Story> observableStories;

    /**
     *
     */
    private ObservableObjectValue<Story> selectedStory;

    @FXML
    @Override
    public final void initialize() {
        setChangeListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                saveChanges();
            }
        });

        shortNameTextField.focusedProperty().addListener(getChangeListener());
        longNameTextField.focusedProperty().addListener(getChangeListener());
        descriptionTextArea.focusedProperty().addListener(getChangeListener());
        poChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        storyPicker.getSelectionModel().selectedItemProperty().addListener(getChangeListener());

        observableStories = FXCollections.observableArrayList();
        storyTable.setItems(observableStories);
        selectedStory = storyTable.getSelectionModel().selectedItemProperty();
        deleteColumn.setCellFactory(param -> new RemoveButtonCell());
        storyColumn.setCellValueFactory(param -> {
            Story story = param.getValue();
            Integer storyPriority = getModel().getStoryPriority(story);
            if (storyPriority != null) {
                return new SimpleStringProperty(String.valueOf(storyPriority + 1)
                        + ". "
                        + story.getShortName());
            } else {
                return new SimpleStringProperty(story.getShortName());
            }
        });

        setErrorCallback(message -> {
            if (message.getClass() == String.class) {
                labelErrorMessage.setText(message);
            }
        });
    }

    /**
     *
     * @param event the button clicked event
     */
    @FXML
    private void prioritiseUp(final ActionEvent event) {
        Story story = storyTable.getSelectionModel().getSelectedItem();
        if (story != null) {
            Integer storyPriority = getModel().getStoryPriority(story);
            if (storyPriority == null) {
                storyPriority = getModel().getMaxStoryPriority() + 1;
            }
            if (storyPriority == 0) {
                return;
            }
            try {
                getModel().modifyStoryPriority(story, storyPriority - 1);
            } catch (CustomException e) {
                labelErrorMessage.setText(e.getMessage());
            }
            updateStoryTable();
        }
    }

    /**
     *
     * @param event the button clicked event
     */
    @FXML
    private void prioritiseDown(final ActionEvent event) {
        Story story = storyTable.getSelectionModel().getSelectedItem();
        if (story != null) {
            Integer storyPriority = getModel().getStoryPriority(story);
            if (storyPriority == null) {
                return;
            }
            else if (storyPriority + 1 >= getModel().getMaxStoryPriority()) {
                storyPriority = null;
            }
            else {
                storyPriority++;
            }
            try {
                getModel().modifyStoryPriority(story, storyPriority);
            } catch (CustomException e) {
                labelErrorMessage.setText(e.getMessage());
            }
            updateStoryTable();
        }
    }

    /**
     *
     * @param event the button clicked event
     */
    @FXML
    private void addStory(final ActionEvent event) {
        try {
            Story selectedStory = storyPicker.getSelectionModel().getSelectedItem();
            Integer priority = null;
            String priorityString = priorityTextField.getText().trim();
            if (priorityString.matches("\\d+")) {
                priority = Integer.parseInt(priorityString) - 1;
            } else if (!priorityString.isEmpty()) {
                throw new CustomException("Priority is not a number");
            }
            if (selectedStory != null) {
                getModel().addStory(selectedStory, priority);
            }
            updateAvailableStories();
            updateStoryTable();
        } catch (CustomException e) {
            labelErrorMessage.setText(e.getMessage());
        }
    }


    @Override
    public final void loadObject() {
        String modelShortName = getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNotEqual(modelShortName, viewShortName)) {
            shortNameTextField.setText(modelShortName);
        }

        String modelLongName = getModel().getLongName();
        String viewLongName = longNameTextField.getText();
        if (isNotEqual(modelLongName, viewLongName)) {
            longNameTextField.setText(modelLongName);
        }

        String modelDescription = getModel().getDescription();
        String viewDescription = descriptionTextArea.getText();
        if (isNotEqual(modelDescription, viewDescription)) {
            descriptionTextArea.setText(modelDescription);
        }

        updateAssignedPO();
        updateAvailableStories();
    }

    /**
     * Update the assigned PO.
     */
    private void updateAssignedPO() {
        RelationalModel relationalModel = PersistenceManager.getCurrent().getCurrentModel();

        Person productOwner = getModel().getAssignedPO();

        // Add all the people with the PO skill to the list of POs
        List<Person> productOwners = relationalModel.getPeople()
                .stream()
                .filter(p -> p.canBeRole(Skill.PO_NAME))
                .collect(Collectors.toList());

        // Remove listener while editing the product owner picker
        poChoiceBox.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        poChoiceBox.getItems().clear();
        poChoiceBox.getItems().addAll(productOwners);
        if (poChoiceBox != null) {
            poChoiceBox.getSelectionModel().select(productOwner);
        }
        poChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
    }

    /**
     *
     */
    private void updateAvailableStories() {
        RelationalModel relationalModel = PersistenceManager.getCurrent().getCurrentModel();
        List<Story> stories = relationalModel.getStories()
                .stream()
                .filter(story -> !getModel().getAllStories().contains(story))
                .collect(Collectors.toList());
        // Remove listener while editing the story picker
        storyPicker.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        storyPicker.getItems().clear();
        storyPicker.getItems().addAll(stories);
        if (storyPicker != null) {
            storyPicker.getSelectionModel().selectFirst();
        }
        storyPicker.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
    }

    /**
     *
     */
    private void updateStoryTable() {
        observableStories.setAll(getModel().getAllStories());
        if (selectedStory.get() != null) {
            storyTable.getSelectionModel().select(selectedStory.get());
        }
        else {
            storyTable.getSelectionModel().selectFirst();
        }
    }

    @Override
    public final void dispose() {
        shortNameTextField.focusedProperty().removeListener(getChangeListener());
        longNameTextField.focusedProperty().removeListener(getChangeListener());
        poChoiceBox.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        descriptionTextArea.focusedProperty().removeListener(getChangeListener());
        //storyPicker.setItems(null);
        setChangeListener(null);
        UndoRedoManager.removeChangeListener(this);
        setModel(null);
        setErrorCallback(null);
    }

    @Override
    protected final void saveChangesWithException() throws Exception {
        String modelShortName = getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNullOrNotEqual(modelShortName, viewShortName)) {
            getModel().setShortName(viewShortName);
        }

        String modelLongName = getModel().getLongName();
        String viewLongName = longNameTextField.getText();
        if (isNullOrNotEqual(modelLongName, viewLongName)) {
            getModel().setLongName(viewLongName);
        }

        String modelDescription = getModel().getDescription();
        String viewDescription = descriptionTextArea.getText();
        if (isNullOrNotEqual(modelDescription, viewDescription)) {
            getModel().setDescription(viewDescription);
        }

        Person modelProductOwner = getModel().getAssignedPO();
        Person viewProductOwner = poChoiceBox.getValue();
        if (isNullOrNotEqual(modelProductOwner, viewProductOwner)) {
            getModel().setAssignedPO(viewProductOwner);
            updateAssignedPO();
        }
    }

    /**
     * A TableView cell that contains a link to the team it represents.
     */
    private class RemoveButtonCell extends TableCell<Story, Object> {
        @Override
        protected void updateItem(final Object unused, final boolean empty) {
            super.updateItem(unused, empty);
            Story story = (Story) getTableRow().getItem();
            if (story == null) {
                setGraphic(null);
            }
            else {
                Button button = new Button("X");
                button.setOnAction(event -> {
                    getModel().removeStory(story);
                    updateStoryTable();
                    updateAvailableStories();
                });
                setGraphic(button);
            }
        }
    }
}
