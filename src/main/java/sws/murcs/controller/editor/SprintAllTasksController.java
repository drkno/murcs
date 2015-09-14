package sws.murcs.controller.editor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import sws.murcs.controller.pipes.TaskEditorParent;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.Task;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for the all tasks view on the sprint GUI.
 */
public class SprintAllTasksController extends GenericEditor<Sprint> implements TaskEditorParent {

    //region FiltrationEnums

    /**
     * The filtering options for the all tasks view.
     */
    private enum FilterBy {

        /**
         * Filters tasks shown to just be those that have people allocated to them.
         */
        Allocated,

        /**
         * Filters tasks shown to just be those that don't have people allocated to them.
         */
        Unallocated,

        /**
         * Removes all filters and shows all tasks.
         */
        All
    }

    /**
     * The possible ways of grouping tasks together.
     */
    private enum GroupBy {

        /**
         * Groups tasks into titledPanes based on the story that they belong to.
         */
        Story,

        /**
         * Removes all grouping.
         */
        None
    }

    /**
     * The orders by which to sort the tasks.
     */
    private enum OrderBy {

        /**
         * Sorts all tasks by name alphabetically.
         */
        Alphabetical,

        /**
         * Sorts all tasks by the estimate value, lower values come first.
         */
        Estimate,

        /**
         * Sorts all tasks by the state the task is in. Not Started, then In Progress, then Done.
         */
        State,

        /**
         * Randomises the order of the tasks. Because this is totally necessary.
         */
        Obfuscation
    }

    //endregion

    //region FXMLInjectedElements

    /**
     * The choice box that contains the options for filtering the tasks.
     */
    @FXML
    private ChoiceBox<FilterBy> filteringChoiceBox;

    /**
     * The choice box that contains the options for grouping the tasks together.
     */
    @FXML
    private ChoiceBox<GroupBy> groupingChoiceBox;

    /**
     * The choice box that contains the options for ordering the tasks.
     */
    @FXML
    private ChoiceBox<OrderBy> orderingChoiceBox;

    /**
     * The VBox that contains all of the tasks.
     */
    @FXML
    private VBox tasksVBox;

    /**
     * The scrollpane that contains the orderingChoiceBox.
     */
    @FXML
    private ScrollPane tasksScrollPane;

    /**
     * The main anchor pane for the all tasks view.
     */
    @FXML
    private AnchorPane mainView;

    //endregion

    //region TaskAndStoryCollections

    /**
     * A map of all the titledPane sections in the application. The story is used as the key so that we can
     * access the correct titledPane and insert tasks into it when necessary.
     */
    private Map<Story, TitledPane> storyContainers;

    /**
     * A map of all the tasks and task editors.
     */
    private Map<Task, TaskEditor> allTaskEditors;

    /**
     * The list of tasks that should be visible. Note: the only thing that changes visibility
     * is the filter by options.
     */
    private List<Task> visibleTasks;

    /**
     * The list of all the tasks that belong to the sprint.
     */
    private List<Task> allTasks;

    //endregion

    //region TaskLoadingFields

    /**
     * The thread used for loading tasks.
     */
    private Thread thread;

    /**
     * Whether or not the thread that is loading tasks should stop.
     */
    private boolean stop;

    //endregion

    //region CurrentFiltrationOptions

    /**
     * The current filterby option.
     */
    private FilterBy currentFilterBy;

    /**
     * The current groupBy option.
     */
    private GroupBy currentGroupBy;

    /**
     * The current orderBy option.
     */
    private OrderBy currentOrderBy;

    //endregion

    /**
     * Removes a task from it's assigned story and removes the editor from the view.
     * @param task the task to remove.
     */
    @Override
    public void removeTask(final Task task) {
        TaskEditor editor = allTaskEditors.get(task);
        if (currentGroupBy == GroupBy.Story) {
            ((VBox) storyContainers.get(editor.getStory()).getContent()).getChildren().remove(editor.getParent());
        }
        else {
            tasksVBox.getChildren().remove(editor.getParent());
        }
        editor.getStory().removeTask(task);
        allTaskEditors.remove(task);
        allTasks.remove(task);
    }

    @Override
    public List<Task> getTasks() {
        return allTasks;
    }

    @Override
    public Story getAssociatedStory(final Task task) {
        return getModel().getStories()
                .stream()
                .filter(story -> story.getTasks().contains(task))
                .findFirst()
                .orElseGet(() -> null);
    }

    @Override
    public void changesMade() {
        Platform.runLater(() -> {
            tasksVBox.getChildren().clear();
            clearStoryContainers();
            if (currentOrderBy != OrderBy.Obfuscation) {
                sortAllTasks(currentOrderBy);
            }
            updateVisibleNodes(currentFilterBy);
            insertVisibleNodes();
            if (currentGroupBy == GroupBy.Story) {
                addStoryContainers();
            }
        });
    }

    @Override
    public void loadObject() {
        if (thread != null && thread.isAlive()) {
            stop = true;
            try {
                thread.join();
            } catch (Throwable t) {
                ErrorReporter.get().reportError(t, "Failed to stop the loading tasks thread.");
            }
        }
        stop = false;
        allTasks = new ArrayList<>();
        allTaskEditors = new HashMap<>();
        visibleTasks = new ArrayList<>();
        storyContainers = new HashMap<>();
        setDisableChoiceBoxes(true);
        getModel().getStories().forEach(story -> {
            generateStoryTitledPane(story);
            allTasks.addAll(story.getTasks());
        });
        if (currentGroupBy != null && currentOrderBy != null && currentFilterBy != null) {
            sortAllTasks(currentOrderBy);
            updateVisibleNodes(currentFilterBy);
        }
        else {
            visibleTasks.addAll(allTasks);
        }
        loadTasks();
    }

    /**
     * Disables or enables all the choiceboxes based on the value of disable.
     * @param disable whether or enable or disable the choiceboxes.
     */
    private void setDisableChoiceBoxes(final boolean disable) {
        filteringChoiceBox.setDisable(disable);
        groupingChoiceBox.setDisable(disable);
        orderingChoiceBox.setDisable(disable);
    }

    /**
     * Adds the current changelistener to all of the choiceboxes.
     */
    private void addListeners() {
        filteringChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        groupingChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        orderingChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
    }

    /**
     * Removes the current change listener from all the choiceboxes.
     */
    private void removeListeners() {
        filteringChoiceBox.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        groupingChoiceBox.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        orderingChoiceBox.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
    }

    /**
     * Generates a titledPane for the given story and maps it to the map of story containers.
     * @param story the story to generate the titledPane for.
     */
    private void generateStoryTitledPane(final Story story) {
        TitledPane pane = new TitledPane();
        pane.setText(story.getShortName());
        pane.setContent(new VBox());
        storyContainers.put(story, pane);
    }

    /**
     * Adds all the story containers to the taskVBox.
     */
    private void addStoryContainers() {
        storyContainers.keySet()
                .stream()
                .sorted((o1, o2) -> Integer.compare(getModel().getBacklog().getStoryPriority(o1),
                        getModel().getBacklog().getStoryPriority(o2)))
                .forEach(story -> tasksVBox.getChildren().add(storyContainers.get(story)));
    }

    /**
     * Clears all of the children of the each story container.
     */
    private void clearStoryContainers() {
        storyContainers.values().forEach(titledPane -> ((VBox) titledPane.getContent()).getChildren().clear());
    }

    @FXML
    @Override
    public void initialize() {
        mainView.getStyleClass().add("root");
        filteringChoiceBox.getItems().addAll(FilterBy.values());
        groupingChoiceBox.getItems().addAll(GroupBy.values());
        orderingChoiceBox.getItems().addAll(OrderBy.values());
        filteringChoiceBox.setValue(FilterBy.All);
        currentFilterBy = FilterBy.All;
        orderingChoiceBox.setValue(OrderBy.Obfuscation);
        currentOrderBy = OrderBy.Obfuscation;
        groupingChoiceBox.setValue(GroupBy.None);
        currentGroupBy = GroupBy.None;
        setChangeListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (FilterBy.class == newValue.getClass()) {
                    updateFilterBy((FilterBy) newValue);
                }
                else if (OrderBy.class == newValue.getClass()) {
                    updateOrderBy((OrderBy) newValue);
                }
                else if (GroupBy.class == newValue.getClass()) {
                    updateGroupBy((GroupBy) newValue);
                }
            }
        });
        addListeners();
    }

    //region UpdateFiltrationOptions

    /**
     * Updates the tasks to be grouped by the new groupBy value.
     * @param newValue the new value to group the tasks by.
     */
    private void updateGroupBy(final GroupBy newValue) {
        if (currentGroupBy != newValue) {
            currentGroupBy = newValue;
            if (isLoaded) {
                tasksVBox.getChildren().clear();
                clearStoryContainers();
                if (newValue == GroupBy.Story) {
                    addStoryContainers();
                    allTasks.forEach(task -> {
                        if (visibleTasks.contains(task)) {
                            addTaskNode(allTaskEditors.get(task).getParent(),
                                    allTaskEditors.get(task).getStory(), null);
                        }
                    });
                }
                else {
                    allTasks.forEach(task -> {
                        if (visibleTasks.contains(task)) {
                            addTaskNode(allTaskEditors.get(task).getParent(),
                                    allTaskEditors.get(task).getStory(), null);
                        }
                    });
                }
            }
        }
    }

    /**
     * Updates the way the tasks are ordered by the new order by value and inserts all the visible tasks into the GUI.
     * @param newValue the new way of ordering the tasks.
     */
    private void updateOrderBy(final OrderBy newValue) {
        if (currentOrderBy != newValue) {
            currentOrderBy = newValue;
            if (isLoaded) {
                tasksVBox.getChildren().clear();
                clearStoryContainers();
                sortAllTasks(newValue);
                for (Task task : allTasks) {
                    if (visibleTasks.contains(task)) {
                        addTaskNode(allTaskEditors.get(task).getParent(), allTaskEditors.get(task).getStory(), null);
                    }
                }

                if (currentGroupBy == GroupBy.Story) {
                    addStoryContainers();
                }
            }
        }
    }

    /**
     * Sorts all the task by the given new order by value. Note: this doesn't insert task editors into the GUI
     * it just updates the order of the list allTasks.
     * @param newValue the way to sort the tasks.
     */
    private void sortAllTasks(final OrderBy newValue) {
        if (newValue == OrderBy.Alphabetical) {
            allTasks.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
        }
        else if (newValue == OrderBy.Estimate) {
            allTasks.sort((o1, o2) -> Float.compare(o1.getCurrentEstimate(), o2.getCurrentEstimate()));
        }
        else if (newValue == OrderBy.State) {
            allTasks.sort((o1, o2) -> o1.getState().compareTo(o2.getState()));
        }
        else {
            Collections.shuffle(allTasks);
        }
    }

    /**
     * Updates all the tasks that should be visible based on the new filter by option and then inserts
     * all the visible tasks into the all tasks view. Makes sure the new filer option is different and
     * also adds all the story containers if they are necessary.
     * @param newValue the new method of filtering tasks.
     */
    private void updateFilterBy(final FilterBy newValue) {
        if (currentFilterBy != newValue) {
            currentFilterBy = newValue;
            if (isLoaded) {
                tasksVBox.getChildren().clear();
                clearStoryContainers();
                updateVisibleNodes(newValue);
                insertVisibleNodes();

                if (currentGroupBy == GroupBy.Story) {
                    addStoryContainers();
                }
            }
        }
    }

    /**
     * Updates the visible tasks and inserts them into the GUI. Does not makes sure the story containers are
     * visible if they should be, also does not check the filter value has changed and doesn't clear the story
     * containers or tasksVBox.
     * @param newValue the value to filter the visible tasks by.
     */
    private void updateVisibleNodes(final FilterBy newValue) {
        visibleTasks.clear();
        if (newValue == FilterBy.Allocated) {
            allTasks.forEach(task -> {
                if (task.isAllocated()) {
                    visibleTasks.add(task);
                }
            });
        }
        else if (newValue == FilterBy.Unallocated) {
            allTasks.forEach(task -> {
                if (!task.isAllocated()) {
                    visibleTasks.add(task);
                }
            });
        }
        else {
            visibleTasks.addAll(allTasks);
        }
    }

    /**
     * Inserts all of the visible nodes into the GUI.
     */
    private void insertVisibleNodes() {
        visibleTasks.forEach(task -> addTaskNode(allTaskEditors.get(task).getParent(),
                allTaskEditors.get(task).getStory(), null));
    }

    //endregion

    /**
     * Starts the thread for loading all the tasks.
     */
    private void loadTasks() {
        tasksVBox.getChildren().clear();
        TaskLoadingTask taskThread = new TaskLoadingTask();
        taskThread.setEditor(this);
        taskThread.setTasks(allTasks);
        thread = new Thread(taskThread);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void dispose() {
        if (thread != null && thread.isAlive()) {
            stop = true;
            try {
                thread.join();
            } catch (Throwable t) {
                ErrorReporter.get().reportError(t, "Failed to stop the loading tasks thread.");
            }
        }
        super.dispose();
    }

    @Override
    protected void saveChangesAndErrors() {
    }

    /**
     * The task used to load all the tasks into the editor.
     */
    private class TaskLoadingTask extends javafx.concurrent.Task {

        /**
         * The tasks to load.
         */
        private List<Task> tasks;

        /**
         * The parent editor that the task editors belong to.
         */
        private TaskEditorParent editor;

        /**
         * The current sprint, this is used to determine whether or not to add editors into the view.
         */
        private Sprint currentSprint = getModel();

        /**
         * The loader that is used to load all of the tasks.
         */
        private FXMLLoader threadTaskLoader = new AutoLanguageFXMLLoader(getClass().getResource("/sws/murcs/TaskEditor.fxml"));

        /**
         * Sets the list of tasks to load.
         * @param newTasks the tasks to load.
         */
        protected void setTasks(final List<Task> newTasks) {
            tasks = newTasks;
        }

        /**
         * The editor that all the tasks editors generated belong to.
         * @param parent the parent editor of all the task editors generated.
         */
        protected void setEditor(final TaskEditorParent parent) {
            editor = parent;
        }

        @Override
        protected Task call() throws Exception {
            if (currentGroupBy == GroupBy.Story) {
                Platform.runLater(() -> {
                    if (!getModel().equals(currentSprint)) {
                        return;
                    }
                    addStoryContainers();
                });
            }
            for (Task task : tasks) {
                if (stop) {
                    break;
                }
                try {
                    threadTaskLoader.setRoot(null);
                    TaskEditor controller = new TaskEditor();
                    threadTaskLoader.setController(controller);
                    Parent view = threadTaskLoader.load();
                    controller.configure(task, false, view, editor);
                    Platform.runLater(() -> {
                        if (!getModel().equals(currentSprint)) {
                            return;
                        }
                        if (visibleTasks.contains(task)) {
                            addTaskNode(view, controller.getStory(), controller);
                        }
                        else {
                            allTaskEditors.put(task, controller);
                        }
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
            Platform.runLater(() -> {
                isLoaded = true;
                if (!getModel().equals(currentSprint)) {
                    return;
                }
                setDisableChoiceBoxes(false);
            });
        }
    }

    /**
     * Adds a given task node to the GUI.
     * @param view The node to add to the GUI
     * @param linkedStory The story it's linked with in case it's being inserted into a story container.
     * @param editor The editor that the node being inserted is linked to.
     */
    private void addTaskNode(final Node view, final Story linkedStory, final TaskEditor editor) {
        if (currentGroupBy != GroupBy.Story) {
            //So if it's coming from the initial loading.
            if (editor != null) {
                allTaskEditors.put(editor.getTask(), editor);
            }
            if (!tasksVBox.getChildren().contains(view)) {
                tasksVBox.getChildren().add(view);
            }
        }
        else if (linkedStory != null) {
            if (editor != null) {
                allTaskEditors.put(editor.getTask(), editor);
            }
            ((VBox) storyContainers.get(linkedStory).getContent()).getChildren().add(view);
        }
    }

    /**
     * Updates all the data in the editors appropriately.
     */
    public void updateEditors() {
        List<Task> tempTasks = new ArrayList<>();
        getModel().getStories().forEach(story -> tempTasks.addAll(story.getTasks()));
        tempTasks.retainAll(allTasks);
        if (allTasks.size() != tempTasks.size()) {
            loadObject();
        }
        else {
            final boolean[] popoverOpen = {false};
            allTaskEditors.values().forEach(editor -> {
                if (editor.isPopOverOpen()) {
                    popoverOpen[0] = true;
                }
                editor.update();
            });
            if (!popoverOpen[0]) {
                changesMade();
            }
        }
    }

    /**
     * Gets all of the stories that are currently in the editor.
     * @return the stories in the editor.
     */
    public Collection<Story> currentStories() {
        return storyContainers.keySet().stream().collect(Collectors.toList());
    }
}
