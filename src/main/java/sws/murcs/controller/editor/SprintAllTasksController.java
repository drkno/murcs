package sws.murcs.controller.editor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import sws.murcs.controller.pipes.TaskEditorParent;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.Task;

import java.util.*;
import java.util.stream.Collectors;

public class SprintAllTasksController extends GenericEditor<Sprint> implements TaskEditorParent {

    enum FilterBy {
        Allocated,
        Unallocated,
        All
    }

    enum GroupBy {
        Story,
        None
    }

    enum OrderBy {
        Alphabetical,
        Estimate,
        None
    }

    private Map<Story, TitledPane> storyContainers;

    private Map<Task, TaskEditor> allTaskEditors;

    private List<Task> visibleTasks;

    private List<Task> allTasks;

    @FXML
    private ChoiceBox<FilterBy> filteringChoiceBox;

    @FXML
    private ChoiceBox<GroupBy> groupingChoiceBox;

    @FXML
    private ChoiceBox<OrderBy> orderingChoiceBox;

    @FXML
    private VBox tasksVBox;

    private Thread thread;

    private boolean stop;

    private FilterBy currentFilterBy;

    private GroupBy currentGroupBy;

    private OrderBy currentOrderBy;

    @Override
    public void removeTask(Task task) {
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
    public void removeTaskEditor(Parent view) {
    }

    @Override
    public void addTask(Task task) {
        //Todo work out if I want to have task creation in here at all.
    }

    @Override
    public List<Task> getTasks() {
        return allTasks;
    }

    @Override
    public Story getAssociatedStory(Task task) {
        return getModel().getStories().stream().filter(story -> story.getTasks().contains(task)).findFirst().orElseGet(() -> null);
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
        removeListeners();
        filteringChoiceBox.setValue(FilterBy.All);
        currentFilterBy = FilterBy.All;
        groupingChoiceBox.setValue(GroupBy.None);
        currentGroupBy = GroupBy.None;
        orderingChoiceBox.setValue(OrderBy.None);
        currentOrderBy = OrderBy.None;
        addListeners();
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
        loadTasks();
    }

    private void setDisableChoiceBoxes(boolean disable) {
        filteringChoiceBox.setDisable(disable);
        groupingChoiceBox.setDisable(disable);
        orderingChoiceBox.setDisable(disable);
    }

    private void addListeners() {
        filteringChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        groupingChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        orderingChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
    }

    private void removeListeners() {
        filteringChoiceBox.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        groupingChoiceBox.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        orderingChoiceBox.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
    }

    private void generateStoryTitledPane(Story story) {
        TitledPane pane = new TitledPane();
        pane.setText(story.getShortName());
        pane.setContent(new VBox());
        storyContainers.put(story, pane);
    }

    @Override
    protected void saveChangesAndErrors() {

    }

    @FXML
    @Override
    public void initialize() {
        filteringChoiceBox.getItems().addAll(FilterBy.values());
        groupingChoiceBox.getItems().addAll(GroupBy.values());
        orderingChoiceBox.getItems().addAll(OrderBy.values());
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
    }

    private void updateGroupBy(GroupBy newValue) {
        if (currentGroupBy != newValue) {
            currentGroupBy = newValue;
            if (isLoaded) {
                tasksVBox.getChildren().clear();
                clearStoryContainers();
                if (newValue == GroupBy.Story) {
                    tasksVBox.getChildren().addAll(storyContainers.values());
                    allTasks.forEach(task -> {
                        if (visibleTasks.contains(task)) {
                            addTaskNode(allTaskEditors.get(task).getParent(), allTaskEditors.get(task).getStory(), null);
                        }
                    });
                }
                else {
                    allTasks.forEach(task -> {
                        if (visibleTasks.contains(task)) {
                            addTaskNode(allTaskEditors.get(task).getParent(), allTaskEditors.get(task).getStory(), null);
                        }
                    });
                }
            }
        }
    }

    private void updateOrderBy(OrderBy newValue) {
        if (currentOrderBy != newValue) {
            currentOrderBy = newValue;
            if (isLoaded) {
                tasksVBox.getChildren().clear();
                clearStoryContainers();
                if (newValue == OrderBy.Alphabetical) {
                    allTasks.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
                }
                else if (newValue == OrderBy.Estimate) {
                    allTasks.sort((o1, o2) -> {
                        if (o1.getEstimate() > o2.getEstimate()) return 1;
                        if (o1.getEstimate() < o2.getEstimate()) return -1;
                        return 0;
                    });
                }
                else {
                    Collections.shuffle(allTasks);
                }
                for (Task task : allTasks) {
                    if (visibleTasks.contains(task)) {
                        addTaskNode(allTaskEditors.get(task).getParent(), allTaskEditors.get(task).getStory(), null);
                    }
                }

                if (currentGroupBy == GroupBy.Story) {
                    tasksVBox.getChildren().addAll(storyContainers.values());
                }
            }
        }
    }

    private void clearStoryContainers() {
        storyContainers.values().forEach(titledPane -> ((VBox) titledPane.getContent()).getChildren().clear());
    }

    private void updateFilterBy(FilterBy newValue) {
        if (currentFilterBy != newValue) {
            currentFilterBy = newValue;
            if (isLoaded) {
                tasksVBox.getChildren().clear();
                clearStoryContainers();
                visibleTasks.clear();
                if (newValue == FilterBy.Allocated) {
                    allTasks.forEach(task -> {
                        if (task.isAllocated()) {
                            visibleTasks.add(task);
                            addTaskNode(allTaskEditors.get(task).getParent(), allTaskEditors.get(task).getStory(), null);
                        }
                        else {
                            visibleTasks.remove(task);
                        }
                    });
                }
                else if (newValue == FilterBy.Unallocated) {
                    allTasks.forEach(task -> {
                        if (!task.isAllocated()) {
                            visibleTasks.add(task);
                            addTaskNode(allTaskEditors.get(task).getParent(), allTaskEditors.get(task).getStory(), null);
                        }
                        else {
                            visibleTasks.remove(task);
                        }
                    });
                }
                else {
                    visibleTasks.clear();
                    visibleTasks.addAll(allTasks);
                    allTasks.forEach(task -> addTaskNode(allTaskEditors.get(task).getParent(), allTaskEditors.get(task).getStory(), null));
                }

                if (currentGroupBy == GroupBy.Story) {
                    tasksVBox.getChildren().addAll(storyContainers.values());
                }
            }
        }
    }

    private void loadTasks() {
        tasksVBox.getChildren().clear();
        TaskLoadingTask<Void> taskThread = new TaskLoadingTask();
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

    private class TaskLoadingTask<T> extends javafx.concurrent.Task {
        private List<Task> tasks;
        private TaskEditorParent editor;
        private Sprint currentSprint = getModel();
        private FXMLLoader threadTaskLoader = new FXMLLoader(getClass().getResource("/sws/murcs/TaskEditor.fxml"));

        protected void setTasks(List newTasks) {
            tasks = newTasks;
        }

        protected void setEditor(TaskEditorParent parent) {
            editor = parent;
        }

        @Override
        protected T call() throws Exception {
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
                        addTaskNode(view, null, controller);
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
                setDisableChoiceBoxes(false);
            });
        }
    }

    private void addTaskNode(Node view, Story linkedStory, TaskEditor editor) {
        if (currentGroupBy != GroupBy.Story) {
            //So if it's coming from the initial loading.
            if (editor != null) {
                allTaskEditors.put(editor.getTask(), editor);
                visibleTasks.add(editor.getTask());
            }
            if (!tasksVBox.getChildren().contains(view)) {
                tasksVBox.getChildren().add(view);
            }
        }
        else {
            if (linkedStory != null) {
                ((VBox) storyContainers.get(linkedStory).getContent()).getChildren().add(view);
            }
        }
    }
}
