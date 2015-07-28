package sws.murcs.controller.editor;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sws.murcs.model.*;
import sws.murcs.model.persistence.PersistenceManager;

/**
 * The controller for editing sprints.
 */
public class SprintEditor extends GenericEditor<Sprint>{
    /**
     * Text fields for the short and long name of the sprint
     */
    @FXML
    private TextField shortNameTextField, longNameTextField;

    /**
     * A text area for the description of a sprint
     */
    @FXML
    private TextArea descriptionTextArea;

    /**
     * A combo box for picking the team associated with the sprint
     */
    @FXML
    private ComboBox<Team> teamComboBox;

    /**
     * A combobox for picking the backlog associated with a sprint
     */
    @FXML
    private ComboBox<Backlog> backlogComboBox;

    /**
     * A combobox for picking the release associated with a sprint
     */
    @FXML
    private ComboBox<Release> releaseComboBox;

    @Override
    public void loadObject() {
        Organisation organisation = PersistenceManager.getCurrent().getCurrentModel();
        Sprint sprint = getModel();

        //Fill the short name field
        shortNameTextField.setText(sprint.getShortName());

        //Fill the long name field
        longNameTextField.setText(sprint.getLongName());

        //Fill the description field
        //TODO descriptionTextArea.setText(sprint.getDescrription());

        //Update the backlog combo box
        backlogComboBox.getItems().clear();
        backlogComboBox.getItems().addAll(organisation.getBacklogs());

        //Update the releases combo box
        releaseComboBox.getItems().clear();
        releaseComboBox.getItems().addAll(organisation.getReleases());

        //Update the team combo box
        teamComboBox.getItems().clear();
        teamComboBox.getItems().addAll(organisation.getTeams());
    }

    @Override
    protected void saveChangesAndErrors() {

    }

    @Override
    protected void initialize() {

    }
}
