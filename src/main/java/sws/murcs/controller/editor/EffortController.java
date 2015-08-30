package sws.murcs.controller.editor;

import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.model.Effort;
import sws.murcs.model.Person;

/**
 * Controller for logging effort on a task.
 */
public class EffortController {
    /**
     * The VBox that contains all effort entries.
     */
    @FXML
    private VBox effortsVBox, contentVBox;

    /**
     * The parent editor of this editor.
     */
    private TaskEditor parentEditor;

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
     * @param parent The parent editor
     * @param people A list of people allowed to log time for this task
     */
    public void setUp(final TaskEditor parent, final List<Person> people) {
        parentEditor = parent;
        eligibleWorkers = people;

        createController = newEffortEntryController();
        createController.setEffort(new Effort());
        createController.styleAsAddButton();
        createController.setOnAction(this::add);
        createController.getHasErrorsProperty().addListener((observable, oldValue, newValue) -> {
            createController.setActionDisabled(newValue);
        });

        contentVBox.getChildren().add(1, createController.getRoot());

        for (Effort e : parentEditor.getTask().getEffort()) {
            EffortEntryController controller = newEffortEntryController();
            controller.setEffort(e);

            effortsVBox.getChildren().add(1, controller.getRoot());
        }
    }

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
            ErrorReporter.get().reportErrorSecretly(e, "Couldn't load EffortEntryController :'(");
        }
        return null;
    }

    /**
     * Gets eligible workers for this task.
     * @return The eligible workers.
     */
    public List<Person> getEligibleWorkers() {
        return eligibleWorkers;
    }

    /**
     * A method handling creation of new effort entries
     * @param addController The controller that has created the new effort entry
     */
    private void add(final EffortEntryController addController) {
        if (addController.getHasErrorsProperty().get()) return;

        parentEditor.getTask().logEffort(addController.getEffort());

        EffortEntryController controller = newEffortEntryController();
        controller.setEffort(addController.getEffort());

        //Clear the creation controller.
        addController.setEffort(new Effort());

        effortsVBox.getChildren().add(1, controller.getRoot());
    }

    /**
     * Removes effort from the task
     * @param removeController The controller of the task being removed
     */
    private void remove(final EffortEntryController removeController) {
        parentEditor.getTask().unlogEffort(removeController.getEffort());
        effortsVBox.getChildren().remove(removeController.getRoot());
    }
}
