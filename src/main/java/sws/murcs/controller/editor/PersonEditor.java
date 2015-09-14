package sws.murcs.controller.editor;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import sws.murcs.controller.GenericPopup;
import sws.murcs.controller.controls.md.MaterialDesignButton;
import sws.murcs.controller.controls.md.animations.FadeButtonOnHover;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.exceptions.CustomException;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.model.Person;
import sws.murcs.model.Skill;
import sws.murcs.model.persistence.PersistenceManager;

import java.util.Collection;
import java.util.HashMap;
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
    private ObservableList<Skill> allocatableSkills;

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
        skillComboBox.setItems(allocatableSkills);
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

        Collection<Skill> available = PersistenceManager.getCurrent().getCurrentModel().getAvailableSkills(getModel());
        if (allocatableSkills.size() != available.size() || allocatableSkills.stream().allMatch(s -> available.contains(s))) {
            allocatableSkills.clear();
            allocatableSkills.addAll(available);
        }

        allocatedSkillsContainer.getChildren().clear();
        getModel().getSkills().forEach(skill -> {
            Node skillNode = generateSkillNode(skill);
            allocatedSkillsContainer.getChildren().add(skillNode);
            skillNodeIndex.put(skill, skillNode);
        });
        setIsCreationWindow(modelShortName == null);
        if (!getIsCreationWindow()) {
            super.setupSaveChangesButton();
        }
        else {
            shortNameTextField.requestFocus();
        }
        isLoaded = true;
    }

    @Override
    protected final void saveChangesAndErrors() {
        Skill selectedSkill = skillComboBox.getValue();
        if (selectedSkill != null) {

                Node skillNode = generateSkillNode(selectedSkill);
                allocatedSkillsContainer.getChildren().add(skillNode);
                skillNodeIndex.put(selectedSkill, skillNode);
                Platform.runLater(() -> {
                    skillComboBox.getSelectionModel().clearSelection();
                    allocatableSkills.remove(selectedSkill);
                    try {
                        getModel().addSkill(selectedSkill);
                    } catch (CustomException e) {
                        //This should never occur, we should be populating the
                        //list with valid items
                        ErrorReporter.get().reportError(e, "Failed to add the skill. This is bad.");
                    }
                });
        }

        String modelShortName = getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNullOrNotEqual(modelShortName, viewShortName)) {
            try {
                getModel().setShortName(viewShortName);
            }   catch (DuplicateObjectException e) {
                addFormError(shortNameTextField, "{NameExistsError1} {Person} {NameExistsError2}");
            } catch (InvalidParameterException e) {
                addFormError(shortNameTextField, "{ShortNameEmptyError}");
            }
        }

        String modelLongName = getModel().getLongName();
        String viewLongName = longNameTextField.getText();
        if (isNullOrNotEqual(modelLongName, viewLongName)) {
            getModel().setLongName(viewLongName);
        }

        String modelUserId = getModel().getUserId();
        String viewUserId = userIdTextField.getText();
        if (isNullOrNotEqual(modelUserId, viewUserId)) {
            try {
                getModel().setUserId(viewUserId);
            } catch (DuplicateObjectException e) {
                addFormError(shortNameTextField, "{UserNameExistsError1} {Person} {UserNameExistsError2}");
            } catch (InvalidParameterException e) {
                addFormError(shortNameTextField, "{UserNameExistsError2}");
            }
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
        super.dispose();
    }

    /**
     * Generates a node for a skill.
     * @param skill The skill
     * @return the node representing the skill
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private Node generateSkillNode(final Skill skill) {
        MaterialDesignButton removeButton = new MaterialDesignButton(null);
        removeButton.setPrefHeight(15);
        removeButton.setPrefWidth(15);
        Image image = new Image("sws/murcs/icons/removeWhite.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        imageView.setPreserveRatio(true);
        imageView.setPickOnBounds(true);
        removeButton.setGraphic(imageView);
        removeButton.getStyleClass().add("mdr-button");
        removeButton.getStyleClass().add("mdrd-button");
        removeButton.setOnAction(event -> {
            if (!isCreationWindow) {
                GenericPopup popup = new GenericPopup(getWindowFromNode(shortNameTextField));
                popup.setMessageText("{AreYouSureRemove} "
                        + skill.getShortName() + " {From} "
                        + getModel().getShortName() + "?");
                popup.setTitleText("{AreYouSure}");
                popup.addYesNoButtons(() -> {
                    allocatableSkills.add(skill);
                    Node skillNode = skillNodeIndex.get(skill);
                    allocatedSkillsContainer.getChildren().remove(skillNode);
                    skillNodeIndex.remove(skill);
                    getModel().removeSkill(skill);
                    popup.close();
                }, "danger-will-robinson", "everything-is-fine");
                popup.show();
            }
            else {
                allocatableSkills.add(skill);
                Node skillNode = skillNodeIndex.get(skill);
                allocatedSkillsContainer.getChildren().remove(skillNode);
                skillNodeIndex.remove(skill);
                getModel().removeSkill(skill);
            }
        });

        GridPane pane = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS);
        column1.fillWidthProperty().setValue(true);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHgrow(Priority.SOMETIMES);

        pane.getColumnConstraints().addAll(column1, column2);

        if (getIsCreationWindow()) {
            Text nameText = new Text(skill.toString());
            pane.add(nameText, 0, 0);
        }
        else {
            Hyperlink nameLink = new Hyperlink(skill.toString());
            nameLink.setMinWidth(0.0);
            nameLink.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.isControlDown()) {
                    getNavigationManager().navigateToNewTab(skill);
                } else {
                    getNavigationManager().navigateTo(skill);
                }
            });
            pane.add(nameLink, 0, 0);
        }
        pane.add(removeButton, 1, 0);
        GridPane.setMargin(removeButton, new Insets(1, 1, 1, 0));

        FadeButtonOnHover fadeButtonOnHover = new FadeButtonOnHover(removeButton, pane);
        fadeButtonOnHover.setupEffect();
        return pane;
    }
}
