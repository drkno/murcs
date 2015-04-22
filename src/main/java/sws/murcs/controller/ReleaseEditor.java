package sws.murcs.controller;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import sws.murcs.exceptions.CustomException;
import sws.murcs.model.Project;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.Release;
import sws.murcs.model.persistence.PersistenceManager;

import java.time.LocalDate;

/**
 * Created by James on 15/04/2015.
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

    /**
     * Updates the fields in the release editor pane
     */
    @Override
    public void updateFields() {
        String currentShortName = shortNameTextField.getText();
        String currentDescription = descriptionTextArea.getText();
        LocalDate currentReleaseDate = releaseDatePicker.getValue();
        Project currentAssociatedProject = edit.getAssociatedProject();

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
        //Todo set up project stuff
    }

    /**
     * Loads the release editor pane
     */
    @Override
    public void load() {
        updateFields();
    }

    /**
     * Updates the release editor pane
     * @throws Exception
     */
    @Override
    public void update() throws Exception {
        edit.setShortName(shortNameTextField.getText());
        edit.setDescription(descriptionTextArea.getText());
        edit.setReleaseDate(releaseDatePicker.getValue());
        edit.setAssociatedProject(projectChoiceBox.getValue());

        RelationalModel model = PersistenceManager.Current.getCurrentModel();

        //If it hasn't been added to the relational model yet then add it
        if (!model.getReleases().contains(edit))
            model.addRelease(edit);

        //If there is a saved callback then call it
        if (onSaved != null)
            onSaved.updateListView(edit);
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
