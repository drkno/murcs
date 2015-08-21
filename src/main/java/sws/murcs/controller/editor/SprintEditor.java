package sws.murcs.controller.editor;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;
import sws.murcs.controller.GenericPopup;
import sws.murcs.controller.NavigationManager;
import sws.murcs.controller.controls.md.MaterialDesignButton;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.exceptions.CustomException;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.exceptions.NotReadyException;
import sws.murcs.model.Backlog;
import sws.murcs.model.EstimateType;
import sws.murcs.model.Organisation;
import sws.murcs.model.Release;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.Team;
import sws.murcs.model.persistence.PersistenceManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The controller for editing sprints.
 */
public class SprintEditor extends GenericEditor<Sprint> {
    /**
     * Text fields for the short and long name of the sprint.
     */
    @FXML
    private TextField shortNameTextField, longNameTextField;

    /**
     * The container for stories that have been brought into this sprint.
     */
    @FXML
    private VBox storiesContainer;

    /**
     * The list of stories able to be brought into a sprint derived from the backlog.
     */
    @FXML
    private ComboBox<Story> storiesList;

    /**
     * A text area for the description of a sprint.
     */
    @FXML
    private TextArea descriptionTextArea;

    /**
     * A combo box for picking the team associated with the sprint.
     */
    @FXML
    private ComboBox<Team> teamComboBox;

    /**
     * A combobox for picking the backlog associated with a sprint.
     */
    @FXML
    private ComboBox<Backlog> backlogComboBox;

    /**
     * A combobox for picking the release associated with a sprint.
     */
    @FXML
    private ComboBox<Release> releaseComboBox;

    /**
     * DatePickers for the start and end date.
     */
    @FXML
    private DatePicker startDatePicker, endDatePicker;

    /**
     * The list of stories brought into this sprint.
     */
    private List<Story> allocatableStories;

    /**
     *
     */
    private Map<Story, Node> storyNodeIndex;

    @Override
    public final void loadObject() {
        Organisation organisation = PersistenceManager.getCurrent().getCurrentModel();
        Sprint sprint = getModel();
        //Update the start date picker
        startDatePicker.setValue(sprint.getStartDate());

        //Update the end date picker
        endDatePicker.setValue(sprint.getEndDate());

        //Fill the short name field
        shortNameTextField.setText(sprint.getShortName());

        //Fill the long name field
        longNameTextField.setText(sprint.getLongName());

        //Fill the description field
        descriptionTextArea.setText(sprint.getDescription());

        //Update the backlog combo box
        backlogComboBox.getItems().clear();
        backlogComboBox.getItems().addAll(organisation.getBacklogs());
        backlogComboBox.setValue(sprint.getBacklog());

        //Update the releases combo box
        releaseComboBox.getItems().clear();
        releaseComboBox.getItems().addAll(organisation.getReleases());
        releaseComboBox.setValue(sprint.getAssociatedRelease());

        //Update the team combo box
        teamComboBox.getItems().clear();
        teamComboBox.getItems().addAll(organisation.getTeams());
        teamComboBox.setValue(sprint.getTeam());

        //Update the sprint stories
        updateAllocatableStories();
    }

    /**
     * Updates the list of allocatable stories in a sprint.
     */
    private void updateAllocatableStories() {
        allocatableStories.clear();
        if (getModel().getBacklog() != null) {
            getModel().getBacklog().getAllStories().stream()
                    .filter(story -> story.getAcceptanceCriteria().size() > 0
                            && !(story.getEstimate().equals(EstimateType.NOT_ESTIMATED)
                            || story.getEstimate().equals(EstimateType.INFINITE)))
                    .forEach(allocatableStories::add);
            // Remove all the stories already in backlog
            getModel().getStories().stream().forEach(allocatableStories::remove);
        }

        storiesContainer.getChildren().clear();
        getModel().getStories().forEach(story -> {
            Node storyNode = generateStoryNode(story);
            storiesContainer.getChildren().add(storyNode);
            storyNodeIndex.put(story, storyNode);
        });
    }

    @Override
    protected final void saveChangesAndErrors() {
        clearErrors();
        Sprint sprint = getModel();

        //Try and save the short name
        if (isNullOrNotEqual(sprint.getShortName(), shortNameTextField.getText())) {
            try {
                sprint.setShortName(shortNameTextField.getText());
            } catch (CustomException e) {
                addFormError(shortNameTextField, e.getMessage());
            }
        }

        //Save the long name
        if (isNullOrNotEqual(sprint.getLongName(), longNameTextField.getText())) {
            sprint.setLongName(longNameTextField.getText());
        }

        //Save the description
        if (isNullOrNotEqual(sprint.getDescription(), descriptionTextArea.getText())) {
            sprint.setDescription(descriptionTextArea.getText());
        }

        //Save the team
        if (isNullOrNotEqual(sprint.getTeam(), teamComboBox.getValue())) {
            if (teamComboBox.getValue() != null) {
                sprint.setTeam(teamComboBox.getValue());
            } else {
                addFormError(teamComboBox, "You must select a team to associate with the sprint");
            }
        }

        //Save the backlog
        if (isNullOrNotEqual(sprint.getBacklog(), backlogComboBox.getValue())) {
            if (backlogComboBox.getValue() != null) {
                if (storiesContainer.getChildren().size() > 0) {
                    GenericPopup popup = new GenericPopup();
                    popup.setMessageText("Do you really want to change the Sprint Backlog? "
                            + "All added stories will be cleared");
                    popup.setTitleText("Change Sprint Backlog");
                    popup.addYesNoButtons(() -> {
                        sprint.setBacklog(backlogComboBox.getValue());
                        storiesList.getItems().clear();
                        storiesList.getItems().addAll(sprint.getBacklog().getAllStories());
                        allocatableStories.clear();
                        allocatableStories.addAll(sprint.getBacklog().getAllStories());
                        storyNodeIndex.clear();
                        updateAllocatableStories();
                        popup.close();
                    }, "danger-will-robinson", "dont-panic");
                    popup.show();
                }
                else {
                    sprint.setBacklog(backlogComboBox.getValue());
                    storiesList.getItems().clear();
                    storiesList.getItems().addAll(sprint.getBacklog().getAllStories());
                    allocatableStories.clear();
                    allocatableStories.addAll(sprint.getBacklog().getAllStories());
                    storyNodeIndex.clear();
                    updateAllocatableStories();
                }
            }
            else {
                addFormError(backlogComboBox, "You must select a backlog for this sprint");
            }
        }

        // Save the stories
        Story selectedStory = storiesList.getValue();
        if (selectedStory != null) {
            try {
                getModel().addStory(selectedStory);
                Node skillNode = generateStoryNode(selectedStory);
                storiesContainer.getChildren().add(skillNode);
                storyNodeIndex.put(selectedStory, skillNode);
                Platform.runLater(() -> {
                    storiesList.getSelectionModel().clearSelection();
                    allocatableStories.remove(selectedStory);
                });
            } catch (NotReadyException e) {
                GenericPopup popup = new GenericPopup();
                popup.setMessageText("Do you want to set Story"
                        + selectedStory.getShortName()
                        + " to be Ready so that it can be added to this sprint?");
                popup.setTitleText("Change Story State");
                popup.addYesNoButtons(() -> {
                    try {
                        selectedStory.setStoryState(Story.StoryState.Ready);
                        getModel().addStory(selectedStory);
                        Node skillNode = generateStoryNode(selectedStory);
                        storiesContainer.getChildren().add(skillNode);
                        storyNodeIndex.put(selectedStory, skillNode);
                        Platform.runLater(() -> {
                            storiesList.getSelectionModel().clearSelection();
                            allocatableStories.remove(selectedStory);
                        });
                    }
                    catch (NotReadyException e1) {
                        ErrorReporter.get().reportError(e1, "Stuff turned to custard. Yum.");
                    }
                    finally {
                        popup.close();
                    }
                }, "danger-will-robinson", "dont-panic");
                popup.show();
            }
        }

        //Save the release
        if (isNullOrNotEqual(sprint.getAssociatedRelease(), sprint.getAssociatedRelease())) {
            if (releaseComboBox.getValue() != null) {
                try {
                    sprint.setAssociatedRelease(releaseComboBox.getValue());
                } catch (InvalidParameterException e) {
                    addFormError(releaseComboBox, e.getMessage());
                }
            } else {
                addFormError(releaseComboBox, "You must select a release for this sprint");
            }
        }

        //Save dates
        saveDates();
    }

    /**
     * Save the start and end date for the sprint.
     */
    private void saveDates() {
        boolean hasProblems = false;
        Sprint sprint = getModel();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        Release release = releaseComboBox.getValue();

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            addFormError(startDatePicker, "Start date must be before end date");
            addFormError(endDatePicker);
            hasProblems = true;
        }

        if (release != null && endDate != null && endDate.isAfter(release.getReleaseDate())) {
            addFormError(endDatePicker, "The sprint must end before its associated release");
            hasProblems = true;
        }

        if (hasProblems) {
            return;
        }

        //Save the start date
        if (isNullOrNotEqual(sprint.getStartDate(), startDatePicker.getValue())) {
            if (startDatePicker.getValue() != null) {
                try {
                    sprint.setStartDate(startDatePicker.getValue());
                } catch (InvalidParameterException e) {
                    addFormError(startDatePicker, e.getMessage());
                }
            }
            else {
                addFormError(startDatePicker, "You must specify a start date for the sprint");
            }
        }

        //Save the end date
        if (isNullOrNotEqual(sprint.getEndDate(), endDatePicker.getValue())) {
            if (endDatePicker.getValue() != null) {
                try {
                    sprint.setEndDate(endDatePicker.getValue());
                } catch (InvalidParameterException e) {
                    addFormError(endDatePicker, e.getMessage());
                }
            }
            else {
                addFormError(endDatePicker, "You must specify an end date for the sprint");
            }
        }
    }

    @FXML
    @Override
    protected final void initialize() {
        setChangeListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                saveChanges();
            }
        });

        allocatableStories = FXCollections.observableArrayList();
        storiesList.setItems((ObservableList<Story>) allocatableStories);
        storyNodeIndex = new HashMap<>();
        storiesList.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        shortNameTextField.focusedProperty().addListener(getChangeListener());
        longNameTextField.focusedProperty().addListener(getChangeListener());
        descriptionTextArea.focusedProperty().addListener(getChangeListener());

        backlogComboBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        teamComboBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        releaseComboBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());

        startDatePicker.focusedProperty().addListener(getChangeListener());
        endDatePicker.focusedProperty().addListener(getChangeListener());
        // Setup release ComboBox so that it includes dates in release
        releaseComboBox.setConverter(new StringConverter<Release>() {
            @Override
            public String toString(final Release object) {
                if (object != null) {
                    return String.format("%s (%s)", object.toString(),
                            object.getReleaseDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
                }
                return null;
            }

            @Override
            public Release fromString(final String string) {
                return null;
            }
        });
        releaseComboBox.setCellFactory(new Callback<ListView<Release>, ListCell<Release>>() {
            public ListCell<Release> call(final ListView<Release> param) {
                final ListCell<Release> cell = new ListCell<Release>() {
                    @Override
                    public void updateItem(final Release item, final boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(String.format("%s (%s)", item.toString(),
                                    item.getReleaseDate()
                                            .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))));
                        } else {
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        });
    }

    /**
     * Generates a node for a story.
     * @param story The story
     * @return the node representing the story
     */
    private Node generateStoryNode(final Story story) {
        MaterialDesignButton removeButton = new MaterialDesignButton("X");
        removeButton.getStyleClass().add("mdr-button");
        removeButton.getStyleClass().add("mdrd-button");
        removeButton.setOnAction(event -> {
            GenericPopup popup = new GenericPopup();
            popup.setMessageText("Are you sure you want to remove "
                    + story.getShortName() + " from "
                    + getModel().getShortName() + "?");
            popup.setTitleText("Remove Story from Sprint");
            popup.addYesNoButtons(() -> {
                allocatableStories.add(story);
                Node storyNode = storyNodeIndex.get(story);
                storiesContainer.getChildren().remove(storyNode);
                storyNodeIndex.remove(story);
                getModel().removeStory(story);
                popup.close();
            }, "danger-will-robinson", "dont-panic");
            popup.show();
        });

        GridPane pane = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS);
        column1.fillWidthProperty().setValue(true);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHgrow(Priority.SOMETIMES);

        pane.getColumnConstraints().add(column1);
        pane.getColumnConstraints().add(column2);

        if (getIsCreationWindow()) {
            Text nameText = new Text(story.toString());
            pane.add(nameText, 0, 0);
        }
        else {
            Hyperlink nameLink = new Hyperlink(story.toString());
            nameLink.setOnAction(a -> NavigationManager.navigateTo(story));
            pane.add(nameLink, 0, 0);
        }
        pane.add(removeButton, 1, 0);
        GridPane.setMargin(removeButton, new Insets(1, 1, 1, 0));

        return pane;
    }
}
