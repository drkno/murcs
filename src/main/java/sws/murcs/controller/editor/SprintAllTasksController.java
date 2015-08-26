package sws.murcs.controller.editor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import sws.murcs.controller.pipes.TaskEditorParent;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.Task;

import java.util.ArrayList;
import java.util.List;

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
        None
    }

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
        //Todo work out if I want to have this in here or not.
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
        stop = false;
        allTasks = new ArrayList<>();
        getModel().getStories().forEach(story -> allTasks.addAll(story.getTasks()));
        loadTasks();
    }

    @Override
    protected void saveChangesAndErrors() {

    }

    @FXML
    @Override
    public void initialize() {
        filteringChoiceBox.getItems().addAll(FilterBy.values());
        filteringChoiceBox.setValue(FilterBy.All);
        currentFilterBy = FilterBy.All;
        groupingChoiceBox.getItems().addAll(GroupBy.values());
        groupingChoiceBox.setValue(GroupBy.None);
        currentGroupBy = GroupBy.None;
        orderingChoiceBox.getItems().addAll(OrderBy.values());
        orderingChoiceBox.setValue(OrderBy.None);
        currentOrderBy = OrderBy.None;
        setChangeListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (FilterBy.class == newValue.getClass()) {
                    updateFilterBy((FilterBy) newValue);
                }
                else if (OrderBy.class == newValue.getClass()) {
                    updateOrderBy((OrderBy)newValue);
                }
                else if (GroupBy.class == newValue.getClass()) {
                    updateGroupBy((GroupBy) newValue);
                }
            }
        });
        filteringChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        groupingChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        orderingChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
    }

    private void updateGroupBy(GroupBy newValue) {
        if (currentGroupBy != newValue) {
            currentGroupBy = newValue;
        }
    }

    private void updateOrderBy(OrderBy newValue) {
        if (currentOrderBy != newValue) {
            currentOrderBy = newValue;
        }
    }

    private void updateFilterBy(FilterBy newValue) {
        if (currentFilterBy != newValue) {
            currentFilterBy = newValue;
        }
    }

    private void loadTasks() {
        tasksVBox.getChildren().clear();
        SprintAllTasksController foo = this;
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
        //super.dispose();
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
                        tasksVBox.getChildren().add(view);
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
    }
}
