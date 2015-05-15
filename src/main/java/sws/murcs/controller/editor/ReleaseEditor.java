package sws.murcs.controller.editor;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Project;
import sws.murcs.model.Release;
import sws.murcs.model.persistence.PersistenceManager;

import java.time.LocalDate;
import java.util.Optional;

/**
 * An editor for editing/creating a release.
 */
public class ReleaseEditor extends GenericEditor<Release> {

    /**
     * The shortName of a Release.
     */
    @FXML
    private TextField shortNameTextField;
    /**
     * The Description of a release.
     */
    @FXML
    private TextArea descriptionTextArea;
    /**
     * Release date picker.
     */
    @FXML
    private DatePicker releaseDatePicker;
    /**
     * The label to show the error message.
     */
    @FXML
    private Label labelErrorMessage;
    /**
     * The list of projects to choose from.
     */
    @FXML
    private ChoiceBox<Project> projectChoiceBox;
    /**
     * The releases associated project.
     */
    private Project associatedProject;

    @FXML
    @Override
    public final void initialize() {
        setChangeListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                saveChanges();
            }
        });

        descriptionTextArea.focusedProperty().addListener(getChangeListener());
        shortNameTextField.focusedProperty().addListener(getChangeListener());
        releaseDatePicker.focusedProperty().addListener(getChangeListener());
        projectChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());

        setErrorCallback(message -> {
            if (message.getClass() == String.class) {
                labelErrorMessage.setText(message);
            }
        });
    }


    @Override
    public final void loadObject() {
        Optional<Project> projectCheck = PersistenceManager.Current.getCurrentModel().getProjects()
                .stream()
                .filter(project -> project.getReleases().contains(getModel()))
                .findFirst();
        if (projectCheck.isPresent()) {
            associatedProject = projectCheck.get();
        }

        // While the project choice box is being populated,
        // don't fire listeners attached to it.
        // this is achieved by removing the listener temporarily
        projectChoiceBox.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        projectChoiceBox.getItems().clear();
        projectChoiceBox.getItems().addAll(PersistenceManager.Current.getCurrentModel().getProjects());
        if (associatedProject != null) {
            projectChoiceBox.getSelectionModel().select(associatedProject);
        }
        projectChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());

        String modelShortName = getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNotEqual(modelShortName, viewShortName)) {
            shortNameTextField.setText(modelShortName);
        }

        String modelDescription = getModel().getDescription();
        String viewDescription = descriptionTextArea.getText();
        if (isNotEqual(modelDescription, viewDescription)) {
            descriptionTextArea.setText(modelDescription);
        }

        LocalDate modelReleaseDate = getModel().getReleaseDate();
        LocalDate viewReleaseDate = releaseDatePicker.getValue();
        if (isNotEqual(modelReleaseDate, viewReleaseDate)) {
            releaseDatePicker.setValue(modelReleaseDate);
        }
    }

    @Override
    protected final void saveChangesWithException() throws Exception {
        String modelShortName = getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNullOrNotEqual(modelShortName, viewShortName)) {
            getModel().setShortName(viewShortName);
        }

        String modelDescription = getModel().getDescription();
        String viewDescription = descriptionTextArea.getText();
        if (isNullOrNotEqual(modelDescription, viewDescription)) {
            getModel().setDescription(viewDescription);
        }

        LocalDate modelReleaseDate = getModel().getReleaseDate();
        LocalDate viewReleaseDate = releaseDatePicker.getValue();
        if (isNullOrNotEqual(modelReleaseDate, viewReleaseDate)) {
            getModel().setReleaseDate(viewReleaseDate);
        }

        updateAssociatedProject();
    }

    @Override
    public final void dispose() {
        shortNameTextField.focusedProperty().removeListener(getChangeListener());
        releaseDatePicker.focusedProperty().removeListener(getChangeListener());
        descriptionTextArea.focusedProperty().removeListener(getChangeListener());
        projectChoiceBox.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        associatedProject = null;
        setChangeListener(null);
        UndoRedoManager.removeChangeListener(this);
        setModel(null);
        setErrorCallback(null);
    }

    /**
     * Updates the associated project.
     * @throws Exception when updating fails.
     */
    private void updateAssociatedProject() throws Exception {
        Project viewAssociatedProject = projectChoiceBox.getValue();

        if (viewAssociatedProject != null) {
            if (associatedProject == null) {
                associatedProject = viewAssociatedProject;
                getModel().changeRelease(viewAssociatedProject);
            }
            else if (!associatedProject.equals(viewAssociatedProject)) {
                associatedProject.removeRelease(getModel());
                associatedProject = viewAssociatedProject;
                getModel().changeRelease(viewAssociatedProject);
            }
        }
        else {
            throw new InvalidParameterException("There needs to be an associated project");
        }
    }
}