package sws.murcs.controller.editor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import sws.murcs.controller.GenericPopup;
import sws.murcs.controller.controls.ModelProgressBar;
import sws.murcs.controller.controls.RemovableHyperlinkCell;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.exceptions.MultipleSprintsException;
import sws.murcs.exceptions.NotReadyException;
import sws.murcs.listeners.ChangeCallback;
import sws.murcs.model.Backlog;
import sws.murcs.model.EstimateType;
import sws.murcs.model.ModelType;
import sws.murcs.model.Organisation;
import sws.murcs.model.Release;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.Task;
import sws.murcs.model.TaskState;
import sws.murcs.model.Team;
import sws.murcs.model.helpers.UsageHelper;
import sws.murcs.model.persistence.PersistenceManager;

/**
 * The controller for editing sprints.
 */
public class SprintEditor extends GenericEditor<Sprint> {

    /**
     * Column containing story estimates.
     */
    @FXML
    private TableColumn<Story, String> estimateColumn;

    /**
     * Column containing story names and hyperlinks.
     */
    @FXML
    private TableColumn<Story, String> storyColumn;

    /**
     * Column containing story completeness.
     */
    @FXML
    private TableColumn<Story, Float> completenessColumn;

    /**
     * Table containing stories.
     */
    @FXML
    private TableView<Story> storiesTable;

    /**
     * The Button to navigate to the associated team.
     */
    @FXML
    private Button navigateToTeamButton;

    /**
     * The Button to navigate to the associated release.
     */
    @FXML
    private Button navigateToReleaseButton;

    /**
     * The button to navigate to the associated backlog.
     */
    @FXML
    private Button navigateToBacklogButton;

    /**
     * Text fields for the short and long name of the sprint.
     */
    @FXML
    private TextField shortNameTextField, longNameTextField;

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

    @Override
    public final void loadObject() {
        isLoaded = false;
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

        Platform.runLater(() -> {
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

            if (!isCreationWindow) {
                navigateToReleaseButton.setDisable(false);
                navigateToTeamButton.setDisable(false);
                navigateToBacklogButton.setDisable(false);
            }
        });

        //Update the sprint stories
        updateAllocatableStories();
        Platform.runLater(() -> {
            isLoaded = true;
        });
    }

    /**
     * Updates the list of allocatable stories in a sprint.
     */
    private void updateAllocatableStories() {
        Platform.runLater(() -> {
            allocatableStories.clear();
            if (getModel().getBacklog() != null) {
                getModel().getBacklog().getAllStories().stream()
                        .filter(story -> story.getAcceptanceCriteria().size() > 0
                                && !(story.getEstimate().equals(EstimateType.NOT_ESTIMATED)
                                || story.getEstimate().equals(EstimateType.INFINITE))
                                && UsageHelper.findBy(ModelType.Sprint,
                                s -> ((Sprint) s).getStories().contains(story)) == null)
                                .forEach(allocatableStories::add);
                // Remove all the stories already in the sprint
                getModel().getStories().stream().forEach(allocatableStories::remove);
            }

            storiesTable.setItems(FXCollections.observableArrayList(getModel().getStories()));
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
            }   catch (DuplicateObjectException e) {
                addFormError(shortNameTextField, "{NameExistsError1} {Story} {NameExistsError2}");
            }
            catch (InvalidParameterException e) {
                addFormError(shortNameTextField, "{ShortNameEmptyError}");
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
                addFormError(teamComboBox, "{TeamNullError}");
            }
        }

        //Save the backlog
        if (isNullOrNotEqual(sprint.getBacklog(), backlogComboBox.getValue())) {
            if (backlogComboBox.getValue() != null) {
                if (storiesTable.getItems().size() > 0) {
                    GenericPopup popup = new GenericPopup();
                    popup.setMessageText("{ConfirmChangeSprintBacklog}");
                    popup.setTitleText("{ConfirmChangeSprintBacklogTitle}");
                    popup.addYesNoButtons(() -> {
                        sprint.setBacklog(backlogComboBox.getValue());
                        storiesList.getItems().clear();
                        storiesList.getItems().addAll(sprint.getBacklog().getAllStories());
                        allocatableStories.clear();
                        allocatableStories.addAll(sprint.getBacklog().getAllStories());
                        updateAllocatableStories();
                        popup.close();
                    }, "danger-will-robinson", "everything-is-fine");
                    popup.show();
                }
                else {
                    sprint.setBacklog(backlogComboBox.getValue());
                    storiesList.getItems().clear();
                    storiesList.getItems().addAll(sprint.getBacklog().getAllStories());
                    allocatableStories.clear();
                    allocatableStories.addAll(sprint.getBacklog().getAllStories());
                    updateAllocatableStories();
                }
            }
            else {
                addFormError(backlogComboBox, "{BacklogNullError}");
            }
        }

        // Save the stories
        Story selectedStory = storiesList.getValue();
        if (selectedStory != null) {
            try {
                getModel().addStory(selectedStory);
                storiesTable.getItems().add(selectedStory);
                Platform.runLater(() -> {
                    storiesList.getSelectionModel().clearSelection();
                    allocatableStories.remove(selectedStory);
                });
            } catch (NotReadyException e) {
                GenericPopup popup = new GenericPopup();
                popup.setMessageText("{SetStoryState1}"
                        + selectedStory.getShortName()
                        + " {SetStoryState2}");
                popup.setTitleText("{ChangeStoryStateTitle}");
                popup.addYesNoButtons(() -> {
                    try {
                        selectedStory.setStoryState(Story.StoryState.Ready);
                        getModel().addStory(selectedStory);
                        storiesTable.getItems().addAll(selectedStory);
                        Platform.runLater(() -> {
                            storiesList.getSelectionModel().clearSelection();
                            allocatableStories.remove(selectedStory);
                        });
                    }
                    catch (NotReadyException e1) {
                        ErrorReporter.get().reportError(e1, "A story was added to a sprint when it was not ready.");
                    }
                    catch (MultipleSprintsException e1) {
                        GenericPopup mpopup = new GenericPopup();
                        popup.setTitleText("{StoryInSprintTitle}");
                        mpopup.setMessageText("{StoryInSprintTitle}. {PleaseRemove}");
                        mpopup.show();
                    }
                    finally {
                        popup.close();
                    }
                }, "danger-will-robinson", "everything-is-fine");
                popup.show();
            }
            catch (MultipleSprintsException e) {
                GenericPopup popup = new GenericPopup();
                popup.setTitleText("{StoryInSprintTitle}");
                popup.setMessageText("{StoryInSprintTitle}. {PleaseRemove}");
                popup.show();
            }
        }

        //Save the release
        if (isNullOrNotEqual(sprint.getAssociatedRelease(), sprint.getAssociatedRelease())) {
            if (releaseComboBox.getValue() != null) {
                try {
                    sprint.setAssociatedRelease(releaseComboBox.getValue());
                } catch (InvalidParameterException e) {
                    addFormError(releaseComboBox, "{ReleaseNullError}");
                }
            } else {
                addFormError(releaseComboBox, "{ReleaseNullError}");
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
            addFormError(startDatePicker, "StartBeforeEndError");
            hasProblems = true;
        }

        if (release != null && endDate != null && endDate.isAfter(release.getReleaseDate())) {
            addFormError(endDatePicker, "{EndsAfterReleaseError}");
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
                    addFormError(startDatePicker, "{NoStartDateError}");
                }
            }
            else {
                addFormError(startDatePicker, "{NoStartDateError}");
            }
        }

        //Save the end date
        if (isNullOrNotEqual(sprint.getEndDate(), endDatePicker.getValue())) {
            if (endDatePicker.getValue() != null) {
                try {
                    sprint.setEndDate(endDatePicker.getValue());
                } catch (InvalidParameterException e) {
                    addFormError(endDatePicker, "{NoEndDateError}");
                }
            }
            else {
                addFormError(endDatePicker, "{NoEndDateError}");
            }
        }
    }

    @FXML
    @Override
    @SuppressWarnings("checkstyle:magicnumber")
    protected final void initialize() {
        setChangeListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue != oldValue && isLoaded) {
                saveChanges();
            }
        });

        allocatableStories = FXCollections.observableArrayList();
        storiesList.setItems((ObservableList<Story>) allocatableStories);
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

        navigateToBacklogButton.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (backlogComboBox.getSelectionModel().getSelectedItem() != null) {
                Backlog backlog = backlogComboBox.getSelectionModel().getSelectedItem();
                if (e.isControlDown()) {
                    getNavigationManager().navigateToNewTab(backlog);
                } else {
                    getNavigationManager().navigateTo(backlog);
                }
            }
        });
        navigateToReleaseButton.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (releaseComboBox.getSelectionModel().getSelectedItem() != null) {
                Release release = releaseComboBox.getSelectionModel().getSelectedItem();
                if (e.isControlDown()) {
                    getNavigationManager().navigateToNewTab(release);
                } else {
                    getNavigationManager().navigateTo(release);
                }
            }
        });
        navigateToTeamButton.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (teamComboBox.getSelectionModel().getSelectedItem() != null) {
                Team team = teamComboBox.getSelectionModel().getSelectedItem();
                if (e.isControlDown()) {
                    getNavigationManager().navigateToNewTab(team);
                } else {
                    getNavigationManager().navigateTo(team);
                }
            }
        });
        navigateToReleaseButton.setDisable(true);
        navigateToTeamButton.setDisable(true);
        navigateToBacklogButton.setDisable(true);
        storyColumn.setCellValueFactory(param -> {
            SimpleStringProperty property = new SimpleStringProperty();
            property.set(param.getValue().getShortName());
            return property;
        });
        List<ChangeCallback<Story>> callbacks = new ArrayList<>();
        callbacks.add(this::removeStory);
        storyColumn.setCellFactory(param -> new RemovableHyperlinkCell(this, callbacks));
        storyColumn.prefWidthProperty().bind(
                storiesTable.widthProperty().subtract(estimateColumn.widthProperty())
                        .subtract(completenessColumn.widthProperty()).subtract(10));
        completenessColumn.setCellValueFactory(param -> {
            float done = 0, total = 0;
            for (Task task : param.getValue().getTasks()) {
                if (task.getState() == TaskState.Done) {
                    done += task.getCurrentEstimate();
                }
                total += task.getCurrentEstimate();
            }
            return new SimpleObjectProperty<>(done / total);
        });
        completenessColumn.setCellFactory(param -> new TableCell<Story, Float>() {
            @Override
            protected void updateItem(final Float completeness, final boolean empty) {
                super.updateItem(completeness, empty);
                setText(null);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                ModelProgressBar modelProgressBar = new ModelProgressBar(true);
                modelProgressBar.setStory((Story) getTableRow().getItem());
                setGraphic(modelProgressBar);
            }
        });
        estimateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getEstimate()));
        estimateColumn.setComparator((o1, o2) -> {
            EstimateType type = getModel().getBacklog().getEstimateType();
            return Integer.compare(type.getSortIndex(o1), type.getSortIndex(o2));
        });
    }

    /**
     * Removes a story from the sprint.
     * @param story the story to remove.
     */
    private void removeStory(final Story story) {
        GenericPopup popup = new GenericPopup();
        popup.setMessageText("{AreYouSureRemove} "
                + story.getShortName() + " {From} "
                + getModel().getShortName() + "?");
        popup.setTitleText("{AreYouSure}");
        popup.addYesNoButtons(() -> {
            allocatableStories.add(story);
            storiesTable.getItems().remove(story);
            getModel().removeStory(story);
            popup.close();
        }, "danger-will-robinson", "everything-is-fine");
        popup.show();
    }
}
