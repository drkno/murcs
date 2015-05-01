package sws.murcs.controller.editor;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Project;
import sws.murcs.model.Release;
import sws.murcs.model.persistence.PersistenceManager;

import java.time.LocalDate;
import java.util.Optional;

/**
 * An editor for editing/creating a release
 */
public class ReleaseEditor extends GenericEditor<Release> {

    @FXML
    private TextField shortNameTextField;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private DatePicker releaseDatePicker;
    @FXML
    private Label labelErrorMessage;
    @FXML
    private ChoiceBox<Project> projectChoiceBox;

    private ChangeListener<Project> projectChangeListener;
    private Project associatedProject;

    @FXML
    @Override
    public void initialize() {
        descriptionTextArea.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue)
                saveChanges();
        });
        shortNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue)
                saveChanges();
        });
        releaseDatePicker.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue)
                saveChanges();
        });
        projectChoiceBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue)
                saveChanges();
        });

        projectChangeListener = (observable, oldValue, newValue) -> {
            if (newValue != null) saveChanges();
        };

        projectChoiceBox.getSelectionModel().selectedItemProperty().addListener(projectChangeListener);

        setErrorCallback(message -> {
            if (message.getClass() == String.class)
                labelErrorMessage.setText(message);
        });
    }


    @Override
    public void loadObject() {
        Optional<Project> projectCheck = PersistenceManager.Current.getCurrentModel().getProjects().stream().
                filter(project -> project.getReleases().contains(edit)).findFirst();
        if (projectCheck.isPresent()) {
            associatedProject = projectCheck.get();
        }

        // While the project choice box is being populated don't fire listeners attached to it.
        // this is achieved by removing the listener temporarily
        projectChoiceBox.getSelectionModel().selectedItemProperty().removeListener(projectChangeListener);
        projectChoiceBox.getItems().clear();
        projectChoiceBox.getItems().addAll(PersistenceManager.Current.getCurrentModel().getProjects());
        if (associatedProject != null) {
            projectChoiceBox.getSelectionModel().select(associatedProject);
        }
        projectChoiceBox.getSelectionModel().selectedItemProperty().addListener(projectChangeListener);

        String modelShortName = edit.getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNotEqual(modelShortName, viewShortName))
            shortNameTextField.setText(modelShortName);

        String modelDescription = edit.getDescription();
        String viewDescription = descriptionTextArea.getText();
        if (isNotEqual(modelDescription, viewDescription))
            descriptionTextArea.setText(modelDescription);

        LocalDate modelReleaseDate = edit.getReleaseDate();
        LocalDate viewReleaseDate = releaseDatePicker.getValue();
        if (isNotEqual(modelReleaseDate, viewReleaseDate))
            releaseDatePicker.setValue(modelReleaseDate);
    }

    @Override
    protected void saveChangesWithException() throws Exception {
        String modelShortName = edit.getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNotEqualOrIsEmpty(modelShortName, viewShortName))
            edit.setShortName(viewShortName);

        String modelDescription = edit.getDescription();
        String viewDescription = descriptionTextArea.getText();
        if (isNotEqualOrIsEmpty(modelDescription, viewDescription))
            edit.setDescription(viewDescription);

        LocalDate modelReleaseDate = edit.getReleaseDate();
        LocalDate viewReleaseDate = releaseDatePicker.getValue();
        if (isNotEqualOrIsEmpty(modelReleaseDate, viewReleaseDate))
            edit.setReleaseDate(viewReleaseDate);

        updateAssociatedProject();
    }

    /**
     * Updates the associated project
     * @throws Exception when updating fails.
     */
    private void updateAssociatedProject() throws Exception {
        //fixme This code feels out of place seems like some of it should be in the model
        //We've just changed what project we are associating this with so remove the release from the last one
        if (associatedProject != null){
            associatedProject.removeRelease(edit);
        }

        //Update the associated project
        associatedProject = projectChoiceBox.getValue();

        if (associatedProject != null) {
            associatedProject.addRelease(edit);
        }
        else {
            throw new InvalidParameterException("There needs to be an associated project");
        }

        UndoRedoManager.commit("edit release");
    }
}
