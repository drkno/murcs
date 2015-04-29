package sws.murcs.controller;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import sws.murcs.exceptions.CustomException;
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

    /**
     * Updates the fields in the release editor pane
     */
    @Override
    public void updateFields() {
        Optional<Project> projectCheck = PersistenceManager.Current.getCurrentModel().getProjects().stream().filter(project -> project.getReleases().contains(edit)).findFirst();
        if (projectCheck.isPresent()) {
            associatedProject = projectCheck.get();
        }

        String currentShortName = shortNameTextField.getText();
        String currentDescription = descriptionTextArea.getText();
        LocalDate currentReleaseDate = releaseDatePicker.getValue();
        Project currentAssociatedProject = associatedProject;

        if (edit.getShortName() != null && !currentShortName.equals(edit.getShortName())) {
            shortNameTextField.setText(edit.getShortName());
        }
        if (edit.getDescription() != null && !currentDescription.equals(edit.getDescription())) {
            descriptionTextArea.setText(edit.getDescription());
        }
        if (currentReleaseDate == null || (edit.getReleaseDate() != null && !currentReleaseDate.equals(edit.getReleaseDate()))) {
            releaseDatePicker.setValue(edit.getReleaseDate());
        }
        projectChoiceBox.getSelectionModel().selectedItemProperty().removeListener(projectChangeListener);
        projectChoiceBox.getItems().clear();
        projectChoiceBox.getItems().addAll(PersistenceManager.Current.getCurrentModel().getProjects());
        if (currentAssociatedProject != null) {
            projectChoiceBox.getSelectionModel().select(currentAssociatedProject);
        }
        projectChoiceBox.getSelectionModel().selectedItemProperty().addListener(projectChangeListener);
    }

    /**
     * Loads the release editor pane
     */
    @Override
    public void load() {
        updateFields();
        updateAndHandle();
    }

    /**
     * Updates the release editor pane
     * @throws Exception
     */
    @Override
    public void update() throws Exception {
        if (edit.getShortName() == null || !edit.getShortName().equals(shortNameTextField.getText()))
            edit.setShortName(shortNameTextField.getText());
        if (edit.getDescription() == null || !edit.getDescription().equals(descriptionTextArea.getText()))
            edit.setDescription(descriptionTextArea.getText());
        if (edit.getReleaseDate() == null || !edit.getReleaseDate().equals(releaseDatePicker.getValue()))
            edit.setReleaseDate(releaseDatePicker.getValue());
        if (associatedProject == null || !associatedProject.equals(projectChoiceBox.getValue())) {

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

    /**
     * Updates the release editor pane and handles any exceptions that it throws
     */
    @Override
    public void updateAndHandle() {
        try {
            labelErrorMessage.setText("");
            update();
        }
        catch (CustomException e) {
            labelErrorMessage.setText(e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            //We don't want these exceptions shown to the user
        }
    }

    @FXML
    public void initialize() {
        descriptionTextArea.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue)
                updateAndHandle();
        });
        shortNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue)
                updateAndHandle();
        });
        releaseDatePicker.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue)
                updateAndHandle();
        });
        projectChoiceBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue)
                updateAndHandle();
        });

        projectChangeListener = (observable, oldValue, newValue) -> {
            if (newValue != null) updateAndHandle();
        };

        projectChoiceBox.getSelectionModel().selectedItemProperty().addListener(projectChangeListener);
    }
}
