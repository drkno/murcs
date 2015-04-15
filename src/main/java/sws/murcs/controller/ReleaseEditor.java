package sws.murcs.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import sws.murcs.exceptions.CustomException;
import sws.murcs.model.Project;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.Release;
import sws.murcs.model.Skill;
import sws.murcs.model.persistence.PersistenceManager;

import javax.management.relation.Relation;

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

    @Override
    public void load() {
        shortNameTextField.setText(edit.getShortName());
        descriptionTextArea.setText(edit.getDescription());
        releaseDatePicker.setValue(edit.getReleaseDate());

        //Todo set up project stuff
    }

    @Override
    public void update() throws Exception {
        edit.setShortName(shortNameTextField.getText());
        edit.setDescription(descriptionTextArea.getText());
        edit.setReleaseDate(releaseDatePicker.getValue());
        //Todo link in the project selection as well

        RelationalModel model = PersistenceManager.Current.getCurrentModel();

        //If it hasn't been added to the relational model yet then add it
        if (!model.getReleases().contains(edit))
            model.addRelease(edit);

        //If there is a saved callback then call it
        if (onSaved != null)
            onSaved.eventNotification(edit);
    }

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
    }
}
