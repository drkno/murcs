package sws.murcs.controller.editor;

import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.model.EffortEntry;
import sws.murcs.model.Person;
import sws.murcs.model.Task;

/**
 * Controller for logging effort on a task.
 */
public class EffortController {
    /**
     * The VBox that contains all effort entries and the vBox
     * containing the creation form.
     */
    @FXML
    private VBox effortsVBox, contentVBox;

    /**
     * The task associated with this popover.
     */
    private Task task;

    /**
     * The eligible workers.
     */
    private List<Person> eligibleWorkers;

    /**
     * The creation effort editor.
     */
    private EffortEntryController createController;

    /**
     * The loader for effort entries.
     */
    private FXMLLoader effortEntryControllerLoader =
            new FXMLLoader(EffortController.class.getResource("/sws/murcs/EffortEntry.fxml"));

    /**
     * Sets up the effort controller.
     * @param pTask The task associated with this popover
     * @param people The list of people
     */
    public void setUp(final Task pTask, final List<Person> people) {
        task = pTask;
        setUp(people);
    }

    /**
     * Sets up the effort controller.
     * @param parent The parent editor
     * @param people A list of people allowed to log time for this task
     */
    public void setUp(final TaskEditor parent, final List<Person> people) {
        task = parent.getTask();
        setUp(people);
    }

    /**
     * Sets up the effort controller.
     * @param people A list of people allowed to log time for this task
     */
    private void setUp(final List<Person> people) {
        eligibleWorkers = people;

        createController = newEffortEntryController();
        createController.setEffortEntry(new EffortEntry());
        createController.styleAsAddButton();
        createController.setOnAction(this::add);
        createController.getHasErrorsProperty().addListener((observable, oldValue, newValue) -> {
            createController.setActionDisabled(newValue);
        });

        contentVBox.getChildren().add(1, createController.getRoot());

        for (EffortEntry e : task.getEffort()) {
            EffortEntryController controller = newEffortEntryController();
            controller.setEffortEntry(e);

            effortsVBox.getChildren().add(0, controller.getRoot());
        }
    }

    /**
     * Creates a new effort entry controller.
     * @return The new effort entry controller.
     */
    private EffortEntryController newEffortEntryController() {
        try {
            effortEntryControllerLoader.setRoot(null);
            effortEntryControllerLoader.setController(null);
            Parent root = effortEntryControllerLoader.load();
            EffortEntryController controller = effortEntryControllerLoader.getController();
            controller.setEffortController(this);
            controller.setRoot(root);
            controller.styleAsRemoveButton();
            controller.setOnAction(this::remove);

            return controller;
        }
        catch (IOException e) {
            ErrorReporter.get().reportError(e, "Couldn't load EffortEntryController :'(");
        }
        return null;
    }

    /**
     * Gets eligible workers for this task.
     * @return The eligible workers.
     */
    public final List<Person> getEligibleWorkers() {
        return eligibleWorkers;
    }

    /**
     * A method handling creation of new effort entries.
     * @param addController The controller that has created the new effort entry
     */
    private void add(final EffortEntryController addController) {
        if (addController.getHasErrorsProperty().get()) return;

        task.logEffort(addController.getEffortEntry());

        EffortEntryController controller = newEffortEntryController();
        controller.setEffortEntry(addController.getEffortEntry());

        //Clear the creation controller.
        addController.setEffortEntry(new EffortEntry());

        effortsVBox.getChildren().add(0, controller.getRoot());
    }

    /**
     * Removes effort from the task.
     * @param removeController The controller of the task being removed
     */
    private void remove(final EffortEntryController removeController) {
        task.unlogEffort(removeController.getEffortEntry());
        effortsVBox.getChildren().remove(removeController.getRoot());
    }
}
