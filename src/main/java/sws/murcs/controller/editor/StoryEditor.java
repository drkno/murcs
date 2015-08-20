package sws.murcs.controller.editor;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import sws.murcs.controller.GenericPopup;
import sws.murcs.controller.NavigationManager;
import sws.murcs.controller.controls.SearchableComboBox;
import sws.murcs.controller.controls.md.MaterialDesignButton;
import sws.murcs.controller.controls.md.animations.FadeButtonOnHover;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.exceptions.CustomException;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.model.AcceptanceCondition;
import sws.murcs.model.Backlog;
import sws.murcs.model.EstimateType;
import sws.murcs.model.Model;
import sws.murcs.model.ModelType;
import sws.murcs.model.Person;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.Task;
import sws.murcs.model.helpers.DependenciesHelper;
import sws.murcs.model.helpers.DependencyTreeInfo;
import sws.murcs.model.helpers.UsageHelper;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.view.App;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An editor for the story model.
 */
public class StoryEditor extends GenericEditor<Story> {

    /**
     * The short name of the story.
     */
    @FXML
    private TextField shortNameTextField;

    /**
     * The description of the story.
     */
    @FXML
    private TextArea descriptionTextArea;

    /**
     * A choice box for the creator.
     */
    @FXML
    private ChoiceBox<Person> creatorChoiceBox;

    /**
     * A choice box for the estimation of a story.
     */
    @FXML
    private ChoiceBox<String> estimateChoiceBox;

    /**
     * A choice box for changing the story state.
     */
    @FXML
    private ChoiceBox<Story.StoryState> storyStateChoiceBox;

    /**
     * Drop down with dependencies that can be added to this story.
     */
    @FXML
    private ComboBox<Story> dependenciesDropDown;

    /**
     * Container that dependencies are added to when they are added.
     * Also the container for the tasks.
     */
    @FXML
    private VBox dependenciesContainer, taskContainer;

    /**
     * A map of dependencies and their respective nodes.
     */
    private Map<Story, Node> dependenciesMap;

    /**
     * A decorator to make the ComboBox searchable.
     * Done a little weirdly to ensure SceneBuilder still works.
     */
    private SearchableComboBox<Story> searchableComboBoxDecorator;

    /**
     * A table for displaying and updating acceptance conditions.
     */
    @FXML
    private TableView<AcceptanceCondition> acceptanceCriteriaTable;

    /**
     * The columns on the AC table.
     */
    @FXML
    private TableColumn<AcceptanceCondition, String> conditionColumn;

    /**
     * Buttons for increasing and decreasing the priority of an AC. Also the button for adding a new AC.
     */
    @FXML
    private Button increasePriorityButton, decreasePriorityButton, addACButton;

    /**
     * The TextField containing the text for the new condition.
     */
    @FXML
    private TextField addConditionTextField;

    /**
     * The FXMLLoader used to create new tasks.
     */
    private FXMLLoader taskLoader;

    /**
     * The thread used to create tasks that already exist in the story.
     */
    private Thread thread;

    /**
     * Whether or not the thread creating task GUIs should stop.
     */
    private boolean stop;

    @Override
    public final void loadObject() {
        if (thread != null && thread.isAlive()) {
            stop = true;
            try {
                thread.join();
            } catch (Throwable t) {
                ErrorReporter.get().reportError(t, "Failed to stop the loading tasks thread.");
            }
        }
        stop = false;
        Backlog backlog = (Backlog) UsageHelper.findUsages(getModel())
                .stream()
                .filter(model -> model instanceof Backlog)
                .findFirst()
                .orElse(null);

        estimateChoiceBox.getItems().clear();
        estimateChoiceBox.getItems().add(EstimateType.NOT_ESTIMATED);
        estimateChoiceBox.getItems().add(EstimateType.INFINITE);
        estimateChoiceBox.getItems().add(EstimateType.ZERO);
        if (backlog != null) {
            estimateChoiceBox.getItems().addAll(backlog.getEstimateType().getEstimates());
        }

        String modelShortName = getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNotEqual(modelShortName, viewShortName)) {
            shortNameTextField.setText(modelShortName);
        }

        String modelDescription = getModel().getDescription();
        String viewDescription = descriptionTextArea.getText();
        if (isNotEqual(modelDescription, viewDescription)) {
            descriptionTextArea.setText(modelDescription);
        }

        dependenciesDropDown.getItems().clear();
        dependenciesDropDown.getItems().addAll(PersistenceManager.getCurrent().getCurrentModel().getStories());
        dependenciesDropDown.getItems().remove(getModel());
        dependenciesDropDown.getItems().removeAll(getModel().getDependencies());

        dependenciesMap.clear();
        dependenciesContainer.getChildren().clear();
        getModel().getDependencies().forEach(dependency -> {
            Node dependencyNode = generateStoryNode(dependency);
            dependenciesContainer.getChildren().add(dependencyNode);
            dependenciesMap.put(dependency, dependencyNode);
        });

        taskContainer.getChildren().clear();
        StoryEditor foo = this;
        javafx.concurrent.Task<Void> taskThread = new javafx.concurrent.Task<Void>() {
            private Story model = getModel();
            private FXMLLoader threadTaskLoader = new FXMLLoader(getClass().getResource("/sws/murcs/TaskEditor.fxml"));

            @Override
            protected Void call() throws Exception {
                for (Task task : model.getTasks()) {
                    if (stop) {
                        break;
                    }
                    try {
                        //Do not try and make this call injectTask as it doesn't work, I've tried.
                        threadTaskLoader.setRoot(null);
                        TaskEditor controller = new TaskEditor();
                        threadTaskLoader.setController(controller);
                        Parent view = threadTaskLoader.load();
                        controller.configure(task, false, view, foo);
                        Platform.runLater(() -> {
                            if (!getModel().equals(model)) {
                                return;
                            }
                            taskContainer.getChildren().add(view);
                        });
                    }
                    catch (Exception e) {
                        ErrorReporter.get().reportError(e, "Unable to create new task");
                    }
                }
                return null;
            }

            @Override
            protected void succeeded() {
                isLoaded = true;
            }
        };
        thread = new Thread(taskThread);
        thread.setDaemon(true);
        thread.start();

        // Enable or disable whether you can change the creator
        if (getIsCreationWindow()) {
            Person modelCreator = getModel().getCreator();
            creatorChoiceBox.getItems().clear();
            creatorChoiceBox.getItems().addAll(PersistenceManager.getCurrent().getCurrentModel().getPeople());
            if (modelCreator != null) {
                creatorChoiceBox.getSelectionModel().select(modelCreator);
            }
        }
        else {
            creatorChoiceBox.getItems().clear();
            creatorChoiceBox.getItems().add(getModel().getCreator());
            creatorChoiceBox.setDisable(true);
        }

        storyStateChoiceBox.getSelectionModel().select(getModel().getStoryState());
        if (!getIsCreationWindow()) {
            creatorChoiceBox.getSelectionModel().select(getModel().getCreator());
        }
        updateEstimation();
        updateAcceptanceCriteria();
        super.clearErrors();
        if (!getIsCreationWindow()) {
            super.setupSaveChangesButton();
        }
        else {
            shortNameTextField.requestFocus();
        }
    }

    /**
     * Updates the estimation on choicebox.
     */
    private void updateEstimation() {
        String currentEstimation = getModel().getEstimate();
        Backlog backlog = (Backlog) UsageHelper.findUsages(getModel())
                .stream()
                .filter(model -> model instanceof Backlog)
                .findFirst()
                .orElse(null);

        if (backlog == null  || getModel().getAcceptanceCriteria().size() == 0) {
            estimateChoiceBox.setValue(EstimateType.NOT_ESTIMATED);
            estimateChoiceBox.setDisable(true);
        }
        else {
            estimateChoiceBox.setDisable(false);
            estimateChoiceBox.setValue(currentEstimation);
        }
    }

    /**
     * Updates the list of acceptance criteria in the Table.
     */
    private void updateAcceptanceCriteria() {
        //store selection
        AcceptanceCondition selected = acceptanceCriteriaTable.getSelectionModel().getSelectedItem();

        //Load the acceptance conditions
        acceptanceCriteriaTable.getItems().clear();
        acceptanceCriteriaTable.getItems().addAll(getModel().getAcceptanceCriteria());

        //restore selection
        acceptanceCriteriaTable.getSelectionModel().select(selected);
        refreshPriorityButtons();

        //Update the story state because otherwise we might have a ready story with no ACs
        updateStoryState();
    }

    /**
     * Refreshes the priority buttons so they have the correct enable state.
     */
    private void refreshPriorityButtons() {
        //Enable both buttons, we'll turn them off if we have to
        increasePriorityButton.setDisable(false);
        decreasePriorityButton.setDisable(false);

        AcceptanceCondition selected = acceptanceCriteriaTable.getSelectionModel().getSelectedItem();

        //If nothing is selected then both buttons should be disabled
        if (selected == null || getModel().getAcceptanceCriteria().size() == 0) {
            increasePriorityButton.setDisable(true);
            decreasePriorityButton.setDisable(true);
            return;
        }


        // and this is the first item priority wise, we can't increase its priority
        if (selected == getModel().getAcceptanceCriteria().get(0)) {
            increasePriorityButton.setDisable(true);
        }

        //If this is the last item, we can't go down
        if (selected == getModel().getAcceptanceCriteria().get(getModel().getAcceptanceCriteria().size() - 1)) {
            decreasePriorityButton.setDisable(true);
        }
    }

    @FXML
    @Override
    public final void initialize() {
        dependenciesContainer.getStylesheets().add(
                getClass().getResource("/sws/murcs/styles/materialDesign/dependencies.css").toExternalForm());

        setChangeListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue != oldValue) {
                saveChanges();
            }
        });
        searchableComboBoxDecorator = new SearchableComboBox(dependenciesDropDown);
        dependenciesMap = new HashMap<>();

        shortNameTextField.focusedProperty().addListener(getChangeListener());
        descriptionTextArea.focusedProperty().addListener(getChangeListener());
        creatorChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        estimateChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        storyStateChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        dependenciesDropDown.valueProperty().addListener(getChangeListener());

        acceptanceCriteriaTable.getSelectionModel().selectedItemProperty().addListener(c -> refreshPriorityButtons());
        conditionColumn.setCellFactory(param -> new AcceptanceConditionCell());
        conditionColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getCondition()));

        //Add all the story states to the choice box
        storyStateChoiceBox.getItems().clear();
        storyStateChoiceBox.getItems().addAll(Story.StoryState.values());
    }

    @Override
    public final void dispose() {
        if (thread != null && thread.isAlive()) {
            stop = true;
            try {
                thread.join();
            } catch (Throwable t) {
                ErrorReporter.get().reportError(t, "Failed to stop the thread loading tasks.");
            }
        }
        shortNameTextField.focusedProperty().removeListener(getChangeListener());
        descriptionTextArea.focusedProperty().removeListener(getChangeListener());
        creatorChoiceBox.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        estimateChoiceBox.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        storyStateChoiceBox.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        dependenciesDropDown.valueProperty().removeListener(getChangeListener());
        searchableComboBoxDecorator.dispose();
        searchableComboBoxDecorator = null;
        dependenciesMap = null;
        taskContainer.getChildren().clear();
        super.dispose();
    }

    @Override
    protected final void saveChangesAndErrors() {
        String modelShortName = getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNullOrNotEqual(modelShortName, viewShortName)) {
            try {
                getModel().setShortName(viewShortName);
            } catch (CustomException e) {
                addFormError(shortNameTextField, e.getMessage());
            }
        }

        String modelDescription = getModel().getDescription();
        String viewDescription = descriptionTextArea.getText();
        if (isNullOrNotEqual(modelDescription, viewDescription)) {
            getModel().setDescription(viewDescription);
        }

        if (estimateChoiceBox.getValue() != null
                && isNotEqual(getModel().getEstimate(), estimateChoiceBox.getValue())) {
            // Updates the story state as this gets changed if you set the estimate to Not Estimated
            if ((estimateChoiceBox.getValue().equals(EstimateType.NOT_ESTIMATED)
                    || estimateChoiceBox.getValue().equals(EstimateType.INFINITE))
                    && UsageHelper.findUsages(getModel()).stream().anyMatch(m -> m instanceof Sprint)) {
                List<Sprint> sprintsWithStory = UsageHelper.findUsages(getModel()).stream()
                        .filter(m -> ModelType.getModelType(m).equals(ModelType.Sprint))
                        .map(m -> (Sprint) m)
                        .collect(Collectors.toList());
                List<String> collect = sprintsWithStory.stream()
                        .map(Model::toString)
                        .collect(Collectors.toList());
                String[] sprintNames = collect.toArray(new String[collect.size()]);
                String actualEstimate = estimateChoiceBox.getValue();
                estimateChoiceBox.setValue(getModel().getEstimate());
                GenericPopup popup = new GenericPopup();
                popup.setMessageText("Do you really want to set the Estimate of "
                        + getModel().toString()
                        + String.format(" to %s?\n", actualEstimate)
                        + "This will set the Story State to None.\n\n"
                        + "The following Sprints will be affected:\n\t"
                        + String.join("\n\t", sprintNames));
                popup.setTitleText("Change Story State");
                popup.setWindowTitle("Are you sure?");
                popup.addYesNoButtons(() -> {
                    getModel().setEstimate(actualEstimate);
                    estimateChoiceBox.setValue(actualEstimate);
                    sprintsWithStory.forEach(sprint -> sprint.removeStory(getModel()));
                    assert getModel().getStoryState().equals(Story.StoryState.None);
                    storyStateChoiceBox.setValue(getModel().getStoryState());
                    popup.close();
                });
                popup.show();
            } else {
                getModel().setEstimate(estimateChoiceBox.getValue());
            }
        }

        updateStoryState();

        if (getIsCreationWindow()) {
            Person viewCreator = (Person) creatorChoiceBox.getValue();
            if (viewCreator != null) {
                getModel().setCreator(viewCreator);
            } else {
                addFormError(creatorChoiceBox, "Creator cannot be empty");
            }
        }

        if (estimateChoiceBox.getValue() != null && getModel().getEstimate() != estimateChoiceBox.getValue()) {
            String estimate = (String) estimateChoiceBox.getValue();
            if (!getModel().getEstimate().equals(estimate)) {
                getModel().setEstimate(estimate);
            }
            // Updates the story state as this gets changed if you set the estimate to Not Estimated
            storyStateChoiceBox.setValue(getModel().getStoryState());
        }

        Story selectedStory = dependenciesDropDown.getValue();
        if (selectedStory != null) {
            try {
                Platform.runLater(() -> {
                    dependenciesDropDown.getSelectionModel().clearSelection();
                });
                getModel().addDependency(selectedStory);
                Node dependencyNode = generateStoryNode(selectedStory);
                dependenciesContainer.getChildren().add(dependencyNode);
                dependenciesMap.put(selectedStory, dependencyNode);
                Platform.runLater(() -> {
                    searchableComboBoxDecorator.remove(selectedStory);
                });
            } catch (CustomException e) {
                addFormError(dependenciesDropDown, e.getMessage());
            }
        }
    }

    /**
     * Checks to see if the current story state is valid and
     * displays an error if it isn't.
     */
    private void updateStoryState() {
        Story.StoryState state = storyStateChoiceBox.getSelectionModel().getSelectedItem();
        Story model = getModel();
        boolean hasErrors = false;

        if (state == Story.StoryState.Ready) {
            if (getModel().getAcceptanceCriteria().size() == 0) {
                addFormError(storyStateChoiceBox, "The story must have at least one AC to set the state to Ready");
                hasErrors = true;
            }
            if (UsageHelper.findUsages(model).stream().noneMatch(m -> m instanceof Backlog)) {
                addFormError(storyStateChoiceBox, "The story must be part of a backlog to set the state to Ready");
                hasErrors = true;
            }
            if (model.getEstimate().equals(EstimateType.NOT_ESTIMATED)
                    || model.getEstimate().equals(EstimateType.INFINITE)) {
                addFormError(storyStateChoiceBox, "The story must be estimated to set the state to Ready");
                hasErrors = true;
            }
        }
        else if (state == Story.StoryState.None) {
            hasErrors = true; // So that the story state is not set.
            if (UsageHelper.findUsages(model).stream().anyMatch(m -> m instanceof Sprint)) {
                List<Sprint> sprintsWithStory = UsageHelper.findUsages(getModel()).stream()
                        .filter(m -> ModelType.getModelType(m).equals(ModelType.Sprint))
                        .map(m -> (Sprint) m)
                        .collect(Collectors.toList());
                List<String> collect = sprintsWithStory.stream()
                        .map(Model::toString)
                        .collect(Collectors.toList());
                String[] sprintNames = collect.toArray(new String[collect.size()]);
                storyStateChoiceBox.setValue(getModel().getStoryState());
                GenericPopup popup = new GenericPopup();
                popup.setMessageText("Do you really want to set the State of "
                        + getModel().toString()
                        + String.format(" to %s?\n\n", state)
                        + "The following Sprints will be affected:\n\t"
                        + String.join("\n\t", sprintNames));
                popup.setTitleText("Change Story State");
                popup.setWindowTitle("Are you sure?");
                popup.addYesNoButtons(() -> {
                    sprintsWithStory.forEach(sprint -> sprint.removeStory(getModel()));
                    getModel().setStoryState(Story.StoryState.None);
                    storyStateChoiceBox.setValue(Story.StoryState.None);
                    popup.close();
                });
                popup.show();
            }
        }

        if (!hasErrors && state != null) {
            Story.StoryState newState = storyStateChoiceBox.getSelectionModel().getSelectedItem();
            if (!newState.equals(getModel().getStoryState())) {
                getModel().setStoryState(newState);
            }
        }
    }

    /**
     * Generate a new node for a story dependency.
     * @param newDependency story to generate a node for.
     * @return a JavaFX node representing the dependency.
     */
    private Node generateStoryNode(final Story newDependency) {
        MaterialDesignButton removeButton = new MaterialDesignButton("X");
        removeButton.getStyleClass().add("mdr-button");
        removeButton.getStyleClass().add("mdrd-button");
        removeButton.setOnAction(event -> {
            if (!isCreationWindow) {
                GenericPopup popup = new GenericPopup(App.getAppController().getWindow());
                popup.setMessageText("Are you sure you want to remove the dependency "
                        + newDependency.getShortName() + "?");
                popup.setTitleText("Remove Dependency");
                popup.setWindowTitle("Are you sure?");
                popup.addYesNoButtons(() -> {
                    searchableComboBoxDecorator.add(newDependency);
                    Node dependencyNode = dependenciesMap.get(newDependency);
                    dependenciesContainer.getChildren().remove(dependencyNode);
                    dependenciesMap.remove(newDependency);
                    getModel().removeDependency(newDependency);
                    popup.close();
                }, "danger-will-robinson", "dont-panic");
                popup.show();
            }
            else {
                searchableComboBoxDecorator.add(newDependency);
                Node dependencyNode = dependenciesMap.get(newDependency);
                dependenciesContainer.getChildren().remove(dependencyNode);
                dependenciesMap.remove(newDependency);
                getModel().removeDependency(newDependency);
            }
        });

        GridPane pane = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.SOMETIMES);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHgrow(Priority.ALWAYS);

        ColumnConstraints column3 = new ColumnConstraints();
        column3.setHgrow(Priority.NEVER);

        pane.getColumnConstraints().add(column1);
        pane.getColumnConstraints().add(column2);
        pane.getColumnConstraints().add(column3);

        if (getIsCreationWindow()) {
            Label nameText = new Label(newDependency.toString());
            pane.add(nameText, 0, 0);
        }
        else {
            Hyperlink nameLink = new Hyperlink(newDependency.toString());
            nameLink.setOnAction(a -> NavigationManager.navigateTo(newDependency));
            pane.add(nameLink, 0, 0);
        }
        DependencyTreeInfo treeInfo = DependenciesHelper.dependenciesTreeInformation(newDependency);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        ObservableList<Node> children = hBox.getChildren();
        children.add(new Label("["));
        Label storiesLabel = new Label(Integer.toString(treeInfo.getCount()));
        storiesLabel.setTooltip(
                new Tooltip("The number of stories that this story depends on in total (including itself)."));
        storiesLabel.getStyleClass().add("story-depends-on");
        children.add(storiesLabel);
        HBox.setHgrow(storiesLabel, Priority.ALWAYS);

        children.add(new Label(", "));
        Label immediateLabel = new Label(Integer.toString(treeInfo.getImmediateDepth()));
        immediateLabel.setTooltip(new Tooltip("The number of stories this story immediately depends on."));
        immediateLabel.getStyleClass().add("story-depends-direct");
        children.add(immediateLabel);
        HBox.setHgrow(immediateLabel, Priority.ALWAYS);

        children.add(new Label(", "));
        Label deepLabel = new Label(Integer.toString(treeInfo.getMaxDepth()));
        deepLabel.setTooltip(new Tooltip("The maximum number of stories this story transitively depends on."));
        deepLabel.getStyleClass().add("story-depends-deep");
        children.add(deepLabel);
        HBox.setHgrow(deepLabel, Priority.ALWAYS);
        children.add(new Label("] "));

        hBox.setOnMouseEntered(event -> transitionText(hBox, storiesLabel, Integer.toString(treeInfo.getCount())
                        + " stories", immediateLabel, Integer.toString(treeInfo.getImmediateDepth()) + " direct",
                deepLabel, Integer.toString(treeInfo.getMaxDepth()) + " deep"));

        hBox.setOnMouseExited(event -> transitionText(hBox, storiesLabel, Integer.toString(treeInfo.getCount()),
                immediateLabel, Integer.toString(treeInfo.getImmediateDepth()),
                deepLabel, Integer.toString(treeInfo.getMaxDepth())));

        pane.add(hBox, 1, 0);
        pane.add(removeButton, 2, 0);
        GridPane.setMargin(removeButton, new Insets(1, 1, 1, 0));
        FadeButtonOnHover fadeButtonOnHover = new FadeButtonOnHover(removeButton, pane);
        fadeButtonOnHover.setupEffect();

        return pane;
    }

    /**
     * Performs a transition to new text on the dependencies detail text.
     * @param itemsContainer container of the details labels.
     * @param storiesLabel the label to set to storiesText.
     * @param storiesText the new text for storiesLabel.
     * @param immediateLabel the label to set to immediateText.
     * @param immediateText the new text for immediateLabel.
     * @param deepLabel the label to set to deepText.
     * @param deepText the new text for deepLabel.
     */
    private void transitionText(final Node itemsContainer, final Label storiesLabel, final String storiesText,
                                final Label immediateLabel, final String immediateText,
                                final Label deepLabel, final String deepText) {
        final Duration transitionTime = Duration.seconds(0.15);
        FadeTransition fadeOut = new FadeTransition(transitionTime, itemsContainer);
        fadeOut.setFromValue(itemsContainer.getOpacity());
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(evt -> {
            storiesLabel.setText(storiesText);
            immediateLabel.setText(immediateText);
            deepLabel.setText(deepText);
            fadeOut.setFromValue(itemsContainer.getOpacity());
            fadeOut.setToValue(1);
            fadeOut.setOnFinished(null);
            fadeOut.play();
        });
        fadeOut.play();
    }

    /**
     * Called when the "Add Condition" button is clicked. Adds the Acceptance Condition
     * created by the user
     * @param event The event information
     */
    @FXML
    protected final void addConditionButtonClicked(final ActionEvent event) {
        String conditionText = addConditionTextField.getText();

        //Create a new condition
        AcceptanceCondition newCondition = new AcceptanceCondition();
        try {
            newCondition.setCondition(conditionText);
        } catch (CustomException e) {
            addFormError(addACButton, e.getMessage());
            return;
        }

        //Add the new condition to the model
        getModel().addAcceptanceCondition(newCondition);

        //Clear the acceptance condition box
        addConditionTextField.setText("");

        //Make sure that the table gets updated
        updateAcceptanceCriteria();

        //Select the item we just created
        acceptanceCriteriaTable.getSelectionModel().select(newCondition);
    }

    /**
     * Decreases the priority of a selected row in the table.
     * @param event the event information
     */
    @FXML
    protected final void increasePriorityClicked(final ActionEvent event) {
        //Get the selected item and move it up one place
        AcceptanceCondition condition = acceptanceCriteriaTable.getSelectionModel().getSelectedItem();
        moveCondition(condition, -1);
    }

    /**
     * Increases the priority of a selected row in the table.
     * @param event the event information
     */
    @FXML
    protected final void decreasePriorityClicked(final ActionEvent event) {
        //Get the selected item and move it down one place
        AcceptanceCondition condition = acceptanceCriteriaTable.getSelectionModel().getSelectedItem();
        moveCondition(condition, 1);
    }

    /**
     * Moves a condition down the list of Acceptance Criteria by a specified number of places (the number of
     * places wraps).
     * @param condition The condition to move
     * @param places The number of places to move it.
     */
    public final void moveCondition(final AcceptanceCondition condition, final int places) {
        //Get the current index of the AC
        int index = getModel().getAcceptanceCriteria().indexOf(condition);

        //If the item is not in the list, return
        if (index == -1) {
            return;
        }

        index += places;

        //Wrap the index.
        while (index < 0) {
            index += getModel().getAcceptanceCriteria().size();
        }
        while (index >= getModel().getAcceptanceCriteria().size()) {
            index -= getModel().getAcceptanceCriteria().size();
        }

        //Reposition the item to our calculated index in the model
        getModel().repositionCondition(condition, index);

        //Update the ACs in the table
        updateAcceptanceCriteria();
    }

    /**
     * Injects a task editor tied to the given task.
     * @param task The task to display
     * @param creationBox Whether or not this is a creation box
     */
    private void injectTaskEditor(final Task task, final boolean creationBox) {
        try {
            taskLoader.setRoot(null);
            TaskEditor controller = new TaskEditor();
            taskLoader.setController(controller);
            if (taskLoader == null) {
                return;
            }
            Parent view = taskLoader.load();
            controller.configure(task, creationBox, view, this);
            Platform.runLater(() -> {
                taskContainer.getChildren().add(view);
            });
        } catch (Exception e) {
            ErrorReporter.get().reportError(e, "Unable to create new task");
        }
    }

    /**
     * Is called when you click the 'Create Task' button and inserts a new task
     * fxml into the task container.
     * @param event The event that caused the function to be called
     */
    @FXML
    private void createTaskClick(final ActionEvent event) {
        Task task = new Task();
        if (taskLoader == null) {
            taskLoader = new FXMLLoader(getClass().getResource("/sws/murcs/TaskEditor.fxml"));
        }
        injectTaskEditor(task, true);
    }

    /**
     * Adds a task to this story.
     * @param task The task to add
     */
    protected final void addTask(final Task task) {
        try {
            getModel().addTask(task);
        }
        catch (DuplicateObjectException e) {
            addFormError(taskContainer, e.getMessage());
        }
    }

    /**
     * Removes a task from this story.
     * @param task The task to remove
     */
    protected final void removeTask(final Task task) {
        if (getModel().getTasks().contains(task)) {
            getModel().removeTask(task);
        }
    }

    /**
     * Removes the editor of a task.
     * @param view The parent node of the task editor
     */
    protected final void removeTaskEditor(final Parent view) {
        taskContainer.getChildren().remove(view);
    }

    /**
     * A cell representing an acceptance condition in the table of conditions.
     */
    private class AcceptanceConditionCell extends TableCell<AcceptanceCondition, String> {
        /**
         * The editable acceptance condition description text field.
         */
        private TextArea textArea = new TextArea();
        /**
         * The acceptance condition description text field.
         */
        private Text textLabel = new Text();

        /**
         * The listener on the text field.
         */
        ChangeListener<String> listener;

        @Override
        public void startEdit() {
            super.startEdit();
            if (!isEmpty()) {
                clearErrors();
                setGraphic(createCell(true));
                textArea.requestFocus();
            }
        }

        @Override
        public void commitEdit(final String newValue) {
            super.commitEdit(newValue);
            if (!isEmpty()) {
                try {
                    AcceptanceCondition acceptanceCondition = (AcceptanceCondition) getTableRow().getItem();
                    acceptanceCondition.setCondition(newValue);
                    textLabel.setText(acceptanceCondition.getCondition());
                    textArea.setText(acceptanceCondition.getCondition());
                    setGraphic(createCell(false));
                    clearErrors();
                } catch (CustomException e) {
                    clearErrors();
                    addFormError(textArea, e.getMessage());
                }

            }
        }

        @Override
        public void cancelEdit() {
            if (!isEmpty()) {
                super.cancelEdit();
                AcceptanceCondition acceptanceCondition = (AcceptanceCondition) getTableRow().getItem();
                textLabel.setText(acceptanceCondition.getCondition());
                textArea.setText(acceptanceCondition.getCondition());
                setGraphic(createCell(false));
            }
        }

        @Override
        protected void updateItem(final String newCondition, final boolean empty) {
            super.updateItem(newCondition, empty);
            textArea.setText(newCondition);
            textLabel.setText(newCondition);

            if (newCondition == null || empty) {
                setText(null);
                setGraphic(null);
                return;
            } else if (isEditing()) {
                setGraphic(createCell(true));
            } else {
                setGraphic(createCell(false));
            }

            setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) { // makes sure it is a double click event to start editing
                    startEdit();
                }
            });
            selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    commitEdit(textArea.getText());
                }
            });
            textArea.setOnKeyPressed(t -> {
                if (t.getCode() == KeyCode.ENTER) {
                    commitEdit(textArea.getText().trim());
                }
                if (t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });
        }

        /**
         * Creates a node to be used as the cell graphic.
         * @param isEdit True if the cell should be editable
         * @return The create cell node
         */
        @SuppressWarnings("checkstyle:magicnumber")
        private Node createCell(final Boolean isEdit) {
            Node node;
            if (isEdit) {
                textArea.setWrapText(true);
                Platform.runLater(() -> {
                    ScrollPane scrollPane = (ScrollPane) textArea.lookup(".scroll-pane");
                    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                });

                if (listener == null) {
                    listener = (observable, oldValue, newValue) -> {
                        Text text = new Text("Test Height"); // this is necessary to get the height of one row of text
                        text.setFont(textArea.getFont());
                        text.setWrappingWidth(textArea.getWidth() - 7.0 - 7.0 - 4.0); // values sources from Modena.css
                        Double height = text.getLayoutBounds().getHeight(); // the height of one row of text
                        text.setText(newValue);
                        textArea.setPrefRowCount((int) ((text.getLayoutBounds().getHeight() / height) + 0.05));
                    };
                    textArea.textProperty().addListener(listener);
                    widthProperty().addListener((observable, oldValue, newValue) -> {
                        Text text = new Text("Test Height");
                        text.setFont(textArea.getFont());
                        text.setWrappingWidth(textArea.getWidth() - 7.0 - 7.0 - 4.0); // values sources from Modena.css
                        Double height = text.getLayoutBounds().getHeight();
                        text.setText(textArea.getText());
                        textArea.setPrefRowCount((int) ((text.getLayoutBounds().getHeight() / height) + 0.05));
                    });
                    textArea.focusedProperty().addListener((observable, oldValue, newValue) -> {
                        if (!newValue) {
                            commitEdit(textArea.getText());
                        }
                    });
                }
                Text text = new Text("Test Height");
                text.setFont(textArea.getFont());
                text.setWrappingWidth(textArea.getWidth() - 7.0 - 7.0 - 4.0); // values sources from Modena.css
                Double height = text.getLayoutBounds().getHeight();
                textArea.setText(textArea.getText().trim());
                text.setText(textArea.getText());
                textArea.setPrefRowCount((int) ((text.getLayoutBounds().getHeight() / height) + 0.05));
                node = textArea;
            }
            else {
                textLabel.setWrappingWidth(getWidth() - 30.0 - 14.0); // 30 - width of button, 14 - get rid of padding
                widthProperty().addListener((observable, oldValue, newValue) -> {
                    textLabel.setWrappingWidth(getWidth() - 30.0 - 14.0);
                });
                node = textLabel;
            }
            AcceptanceCondition acceptanceCondition = (AcceptanceCondition) getTableRow().getItem();
            Button button = new Button("X");
            button.getStyleClass().add("mdr-button");
            button.getStyleClass().add("mdrd-button");
            button.setOnAction(event -> {
                if (UsageHelper.findUsages(getModel()).stream().anyMatch(m -> m instanceof Sprint)
                        && getModel().getAcceptanceCriteria().size() <= 1) {
                    List<Sprint> sprintsWithStory = UsageHelper.findUsages(getModel()).stream()
                            .filter(m -> ModelType.getModelType(m).equals(ModelType.Sprint))
                            .map(m -> (Sprint) m)
                            .collect(Collectors.toList());
                    List<String> collect = sprintsWithStory.stream()
                            .map(Model::toString)
                            .collect(Collectors.toList());
                    String[] sprintNames = collect.toArray(new String[collect.size()]);
                    storyStateChoiceBox.setValue(getModel().getStoryState());
                    GenericPopup popup = new GenericPopup();
                    popup.setMessageText("Do you really want to remove the last Acceptance Criteria from "
                            + getModel().toString()
                            + " ?\n"
                            + "This will set the Story Estimate to Not Estimated and the Story State to None.\n\n"
                            + "The following Sprints will be affected:\n\t"
                            + String.join("\n\t", sprintNames));
                    popup.setTitleText("Change Story State");
                    popup.setWindowTitle("Are you sure?");
                    popup.addYesNoButtons(() -> {
                        getModel().setEstimate(EstimateType.NOT_ESTIMATED);
                        estimateChoiceBox.setValue(EstimateType.NOT_ESTIMATED);
                        sprintsWithStory.forEach(sprint -> sprint.removeStory(getModel()));
                        getModel().removeAcceptanceCondition(acceptanceCondition);
                        updateAcceptanceCriteria();
                        updateEstimation();
                        storyStateChoiceBox.setValue(Story.StoryState.None);
                        getModel().setStoryState(Story.StoryState.None);
                        popup.close();
                    }, "danger-will-robinson", "dont-panic");

                    popup.show();
                } else {
                    getModel().removeAcceptanceCondition(acceptanceCondition);
                    updateAcceptanceCriteria();
                    updateEstimation();
                }
            });
            FadeButtonOnHover fadeButtonOnHover = new FadeButtonOnHover(button, getTableRow());
            fadeButtonOnHover.setupEffect();
            GridPane conditionCell = new GridPane();
            conditionCell.add(node, 0, 0);
            conditionCell.add(button, 1, 0);
            conditionCell.setMinHeight(30.0);
            conditionCell.getColumnConstraints().add(0, new ColumnConstraints(USE_COMPUTED_SIZE,
                    USE_COMPUTED_SIZE, USE_COMPUTED_SIZE,
                    Priority.SOMETIMES, HPos.LEFT, true));
            conditionCell.setAlignment(Pos.CENTER);
            conditionCell.getColumnConstraints().add(1, new ColumnConstraints(30, 30, 30, Priority.NEVER,
                    HPos.CENTER, true));
            return conditionCell;
        }
    }


}
