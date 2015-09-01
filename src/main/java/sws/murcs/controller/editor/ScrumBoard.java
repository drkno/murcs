package sws.murcs.controller.editor;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import sws.murcs.model.Sprint;

/**
 * Scrum Board controller.
 */
public class ScrumBoard extends GenericEditor<Sprint> {

    /**
     * ListViews for each of the columns.
     */
    @FXML
    private ListView notStartedListView, inProgressListView, doneListView;

    @Override
    protected void initialize() {

    }

    @Override
    public void loadObject() {

    }

    @Override
    protected void saveChangesAndErrors() {

    }
}
