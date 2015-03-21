package sws.murcs.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.exceptions.NameInvalidException;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.Skill;
import sws.murcs.model.persistence.PersistenceManager;

/**
 * A controller to edit skills
 */
public class SkillEditor extends GenericEditor<Skill> {

    @FXML
    TextField shortNameTextField, longNameTextField;
    @FXML
    TextArea descriptionTextArea;
    @FXML
    Label labelErrorMessage;
    @FXML
    AnchorPane editor;

    /**
     * Saves the edit being edited
     */
    public void update()  throws Exception{
        edit.setShortName(shortNameTextField.getText());
        edit.setLongName(longNameTextField.getText());
        edit.setDescription(descriptionTextArea.getText());

        RelationalModel model = PersistenceManager.Current.getCurrentModel();

        //If we haven't added the edit yet, throw them in the list of unassigned people
        if (!model.getSkills().contains(edit))
            model.addSkill(edit);

        //If we have a saved callBack, call it
        if (onSaved != null)
            onSaved.call();
    }

    /**
     * Updates the object in memory and handles any exception
     */
    public void updateAndHandle(){
        try {
            labelErrorMessage.setText("");
            update();
        }
        catch (DuplicateObjectException | NameInvalidException e) {
            labelErrorMessage.setText(e.getMessage());
        }
        catch (Exception e) {
            //Don't show the user this.
        }
    }

    /**
     * Loads the edit into the form
     */
    public void load() {
        shortNameTextField.setText(edit.getShortName());
        longNameTextField.setText(edit.getLongName());
        descriptionTextArea.setText(edit.getDescription());

        //if 'edit' is ScrumMaster or PO
        //  then disable the form
        if (edit.getShortName() != null && (edit.getShortName().equals(Skill.ROLES.PO.toString()) || edit.getShortName().equals(Skill.ROLES.SM.toString())))
            editor.setDisable(true);
    }

    @FXML
    public void initialize() {
        shortNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue)
                updateAndHandle();
        });

        longNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue)
                updateAndHandle();
        });

        descriptionTextArea.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue)
                updateAndHandle();
        });
    }
}