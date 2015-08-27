package sws.murcs.controller.editor;

import java.io.IOException;
import java.util.List;
import javafx.event.ActionEvent;
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

        contentVBox.getChildren().add(0, createController.getRoot());
    }

    private EffortEntryController newEffortEntryController() {
        try {
            effortEntryControllerLoader.setRoot(null);
            effortEntryControllerLoader.setController(null);
            Parent root = effortEntryControllerLoader.load();
            EffortEntryController controller = effortEntryControllerLoader.getController();
            controller.setEffortController(this);
            controller.setRoot(root);
            return controller;
        }catch (IOException e) {
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
     * Called when the "Add" button is clicked.
     * @param event The event that called this method
     */
    @FXML
    private void addButtonClick(final ActionEvent event) {
        //Perhaps we should grey out the add button?
        if (createController.hasErrors()) return;

        parentEditor.getTask().logEffort(createController.getEffort());

        EffortEntryController controller = newEffortEntryController();
        controller.setEffort(createController.getEffort());

        //Clear the creation controller.
        createController.setEffort(new Effort());

        effortsVBox.getChildren().add(0, controller.getRoot());
    }
}
