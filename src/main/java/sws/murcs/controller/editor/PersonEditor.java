package sws.murcs.controller.editor;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import sws.murcs.controller.GenericPopup;
import sws.murcs.controller.NavigationManager;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Person;
import sws.murcs.model.Skill;
import sws.murcs.model.persistence.PersistenceManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * The ChoiceBox for selecting skills.
     */
    @FXML
    private ComboBox<Skill> skillComboBox;

    /**
     * The VBox which contains the list of skills the person has.
     */
    @FXML
    private VBox allocatedSkillsContainer;

    /**
     * List of skill that can be added to the person.
     */
    private List<Skill> allocatableSkills;

    /**
     * A map of skills to their nodes in the skill list on the view.
     */
    private Map<Skill, Node> skillNodeIndex;

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
        skillComboBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());

        allocatableSkills = FXCollections.observableArrayList();
        skillComboBox.setItems((ObservableList<Skill>) allocatableSkills);
        skillNodeIndex = new HashMap<>();
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

        allocatableSkills.clear();
        allocatableSkills.addAll(PersistenceManager.getCurrent().getCurrentModel().getAvailableSkills(getModel()));

        allocatedSkillsContainer.getChildren().clear();
        getModel().getSkills().forEach(skill -> {
            Node skillNode = generateSkillNode(skill);
            allocatedSkillsContainer.getChildren().add(skillNode);
            skillNodeIndex.put(skill, skillNode);
        });
        setIsCreationWindow(modelShortName == null);
    }

    @Override
    protected final void saveChangesWithException() throws Exception {
        Skill selectedSkill = skillComboBox.getValue();
        if (selectedSkill != null) {
            getModel().addSkill(selectedSkill);
            Node skillNode = generateSkillNode(selectedSkill);
            allocatedSkillsContainer.getChildren().add(skillNode);
            skillNodeIndex.put(selectedSkill, skillNode);
            Platform.runLater(() -> {
                skillComboBox.getSelectionModel().clearSelection();
                allocatableSkills.remove(selectedSkill);
            });
        }

        String modelShortName = getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNullOrNotEqual(modelShortName, viewShortName)) {
            getModel().setShortName(viewShortName);
        }

        String modelLongName = getModel().getLongName();
        String viewLongName = longNameTextField.getText();
        if (isNullOrNotEqual(modelLongName, viewLongName)) {
            getModel().setLongName(viewLongName);
        }

        String modelUserId = getModel().getUserId();
        String viewUserId = userIdTextField.getText();
        if (isNullOrNotEqual(modelUserId, viewUserId)) {
            getModel().setUserId(viewUserId);
        }
    }

    @Override
    public final void dispose() {
        shortNameTextField.focusedProperty().removeListener(getChangeListener());
        longNameTextField.focusedProperty().removeListener(getChangeListener());
        shortNameTextField.focusedProperty().removeListener(getChangeListener());
        skillComboBox.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        allocatableSkills = null;
        skillNodeIndex = null;
        setChangeListener(null);
        UndoRedoManager.removeChangeListener(this);
        setModel(null);
    }

    /**
     * Generates a node for a skill.
     * @param skill The skill
     * @return the node representing the skill
     */
    private Node generateSkillNode(final Skill skill) {
        Button removeButton = new Button("X");
        removeButton.setOnAction(event -> {
            GenericPopup popup = new GenericPopup();
            popup.setMessageText("Are you sure you want to remove "
                    + skill.getShortName() + " from "
                    + getModel().getShortName() + "?");
            popup.setTitleText("Remove Skill from Person");
            popup.addYesNoButtons(func -> {
                allocatableSkills.add(skill);
                Node skillNode = skillNodeIndex.get(skill);
                allocatedSkillsContainer.getChildren().remove(skillNode);
                skillNodeIndex.remove(skill);
                getModel().removeSkill(skill);
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

        if (getIsCreationWindow()) {
            Text nameText = new Text(skill.toString());
            pane.add(nameText, 0, 0);
        }
        else {
            Hyperlink nameLink = new Hyperlink(skill.toString());
            nameLink.setOnAction(a -> NavigationManager.navigateTo(skill));
            pane.add(nameLink, 0, 0);
        }
        pane.add(removeButton, 1, 0);

        return pane;
    }
}
