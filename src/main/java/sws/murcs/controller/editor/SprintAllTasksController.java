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

    @Override
    public void removeTask(Task task) {

    }

    @Override
    public void removeTaskEditor(Parent view) {

    }

    @Override
    public void addTask(Task task) {

    }

    @Override
    public List<Task> getTasks() {
        return null;
    }

    @Override
    public Story getAssociatedStory() {
        return null;
    }

    enum Filters {
        Allocated,
        Unallocated,
        All
    }

    enum Groups {
        Story,
        None
    }

    enum Orders {
        Alphabetical,
        None
    }

    private List<Task> allTasks;

    @FXML
    private ChoiceBox filteringChoiceBox, groupingChoiceBox, orderingChoiceBox;

    @FXML
    private VBox taskVBox;

    private Sprint sprint;

    private Thread thread;

    private boolean stop;

    @Override
    public void loadObject() {
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
        filteringChoiceBox.getItems().addAll(Filters.values());
        filteringChoiceBox.setValue(Filters.All);
        groupingChoiceBox.getItems().addAll(Groups.values());
        groupingChoiceBox.setValue(Groups.None);
        orderingChoiceBox.getItems().addAll(Orders.values());
        orderingChoiceBox.setValue(Orders.None);
    }

    public void setUpController(Sprint associatedSprint) {
        sprint = associatedSprint;
        loadObject();
    }

    private void loadTasks() {
        SprintAllTasksController foo = this;
        javafx.concurrent.Task<Void> taskThread = new javafx.concurrent.Task<Void>() {
            private List<Task> tasks = allTasks;
            private FXMLLoader threadTaskLoader = new FXMLLoader(getClass().getResource("/sws/murcs/TaskEditor.fxml"));

            @Override
            protected Void call() throws Exception {
                for (Task task : tasks) {
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
                            if (!getModel().equals(tasks)) {
                                return;
                            }
                            taskVBox.getChildren().add(view);
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
    }
}
