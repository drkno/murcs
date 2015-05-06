package sws.murcs.controller.editor;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import sws.murcs.controller.GenericPopup;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Person;
import sws.murcs.model.Skill;
import sws.murcs.model.persistence.PersistenceManager;

/**
 * Allows you to model a model.
 */
public class PersonEditor extends GenericEditor<Person> {

    /**
     * shortName, longName and userId text fields.
     */
    @FXML
    private TextField shortNameTextField, userIdTextField, longNameTextField;
    /**
     * The label for showing errors.
     */
    @FXML
    private Label labelErrorMessage;
    /**
     * The ChoiceBox for selecting skills.
     */
    @FXML
    private ChoiceBox<Skill> skillChoiceBox;
    /**
    /**
     * The VBox which contains the list of skills the person has.
     */
    @FXML
    private VBox allocatedSkillsContainer;

    @FXML
    @Override
    public final void initialize() {
        setChangeListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                saveChanges();
            }
        });

        shortNameTextField.focusedProperty().addListener(getChangeListener());
        longNameTextField.focusedProperty().addListener(getChangeListener());
        userIdTextField.focusedProperty().addListener(getChangeListener());
        skillChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());

        skillChoiceBox.getItems().clear();
        skillChoiceBox.getItems().addAll(PersistenceManager.Current.getCurrentModel().getSkills());

        setErrorCallback(message -> {
            if (message.getClass() == String.class) {
                labelErrorMessage.setText(message);
            }
        });
    }

    @Override
    public final void loadObject() {
        String modelShortName = getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNotEqual(modelShortName, viewShortName)) {
            shortNameTextField.setText(modelShortName);
        }

        String modelLongName = getModel().getLongName();
        String viewLongName = longNameTextField.getText();
        if (isNotEqual(modelLongName, viewLongName)) {
            longNameTextField.setText(modelLongName);
        }

        String modelUserId = getModel().getUserId();
        String viewUserId = userIdTextField.getText();
        if (isNotEqual(modelUserId, viewUserId)) {
            userIdTextField.setText(modelUserId);
        }

        updateSkills();

        //fixme set the error text to nothing when first loading the object
        labelErrorMessage.setText(" ");
    }

    @Override
    protected final void saveChangesWithException() throws Exception {
        Skill selectedSkill = skillChoiceBox.getValue();
        if (selectedSkill != null) {
            generateSkillNode(selectedSkill);
            getModel().addSkill(selectedSkill);
            updateSkills();
        }

        String modelShortName = getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNotEqualOrIsEmpty(modelShortName, viewShortName)) {
            getModel().setShortName(viewShortName);
        }

        String modelLongName = getModel().getLongName();
        String viewLongName = longNameTextField.getText();
        if (isNotEqualOrIsEmpty(modelLongName, viewLongName)) {
            getModel().setLongName(viewLongName);
        }

        String modelUserId = getModel().getUserId();
        String viewUserId = userIdTextField.getText();
        if (isNotEqualOrIsEmpty(modelUserId, viewUserId)) {
            getModel().setUserId(viewUserId);
        }
    }

    @Override
    public final void dispose() {
        shortNameTextField.focusedProperty().removeListener(getChangeListener());
        longNameTextField.focusedProperty().removeListener(getChangeListener());
        shortNameTextField.focusedProperty().removeListener(getChangeListener());
        skillChoiceBox.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        setChangeListener(null);
        UndoRedoManager.removeChangeListener(this);
        setModel(null);
        setErrorCallback(null);
    }

    /**
     * Generates a node for a skill.
     * @param skill The skill
     * @return the node representing the skill
     */
    private Node generateSkillNode(final Skill skill) {
        Text nameText = new Text(skill.toString());
        Button removeButton = new Button("X");
        removeButton.setOnAction(event -> {
            GenericPopup popup = new GenericPopup();
            popup.setMessageText("Are you sure you want to remove "
                    + skill.getShortName() + " from "
                    + getModel().getShortName());
            popup.setTitleText("Remove Skill from Person");
            popup.addOkCancelButtons(func -> {
                getModel().removeSkill(skill);
                skillChoiceBox.getItems().add(skill);
                updateSkills();
                popup.close();
            });
            popup.show();
        });

        GridPane pane = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS);
        column1.fillWidthProperty().setValue(true);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHgrow(Priority.SOMETIMES);

        pane.getColumnConstraints().add(column1);
        pane.getColumnConstraints().add(column2);

        pane.add(nameText, 0, 0);
        pane.add(removeButton, 1, 0);

        return pane;
    }

    /**
     * Updates the list of skills the person has.
     */
    private void updateSkills() {
        allocatedSkillsContainer.getChildren().clear();
        for (Skill skill : getModel().getSkills()) {
            Node node = generateSkillNode(skill);
            allocatedSkillsContainer.getChildren().add(node);
            skillChoiceBox.getItems().remove(skill);
        }
    }
}
