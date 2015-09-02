package sws.murcs.controller.editor;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
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
import sws.murcs.exceptions.MultipleRolesException;
import sws.murcs.model.Person;
import sws.murcs.model.Skill;
import sws.murcs.model.Team;
import sws.murcs.model.persistence.PersistenceManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The controller for the team editor.
 */
public class TeamEditor extends GenericEditor<Team> {

    /**
     * Button used to navigate to the SM person.
     */
    @FXML
    private Button navigateToSMButton;

    /**
     * Button used to navigate to the PO person.
     */
    @FXML
    private Button navigateToPOButton;

    /**
     * The member of the team.
     */
    @FXML
    private VBox teamMembersContainer;

    /**
     * The shortName and longName fields for a Team.
     */
    @FXML
    private TextField shortNameTextField, longNameTextField;

    /**
     * The description area of a Team.
     */
    @FXML
    private TextArea descriptionTextArea;

    /**
     * The product owner and scrum master pickers.
     */
    @FXML
    private ChoiceBox<Person> productOwnerPicker, scrumMasterPicker;

    /**
     * The member picker.
     */
    @FXML
    private ComboBox<Person> addTeamMemberPicker;

    /**
     * Buttons for clearing the current SM and PO.
     */
    @FXML
    private Button clearPOButton, clearSMButton;

    /**
     * List of people that can be added to the team.
     */
    private List<Person> allocatablePeople;

    /**
     * A map of people to their nodes in the member list on the view.
     */
    private Map<Person, Node> memberNodeIndex;

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
        descriptionTextArea.focusedProperty().addListener(getChangeListener());
        addTeamMemberPicker.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        productOwnerPicker.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        scrumMasterPicker.getSelectionModel().selectedItemProperty().addListener(getChangeListener());

        allocatablePeople = FXCollections.observableArrayList();
        addTeamMemberPicker.setItems((ObservableList<Person>) allocatablePeople);
        memberNodeIndex = new HashMap<>();
        navigateToPOButton.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (productOwnerPicker.getSelectionModel().getSelectedItem() != null) {
                Person person = productOwnerPicker.getSelectionModel().getSelectedItem();
                if (e.isControlDown()) {
                    getNavigationManager().navigateToNewTab(person);
                } else {
                    getNavigationManager().navigateTo(person);
                }
            }
        });
        navigateToSMButton.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (scrumMasterPicker.getSelectionModel().getSelectedItem() != null) {
                Person person = scrumMasterPicker.getSelectionModel().getSelectedItem();
                if (e.isControlDown()) {
                    getNavigationManager().navigateToNewTab(person);
                } else {
                    getNavigationManager().navigateTo(person);
                }
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

        String modelDescription = getModel().getDescription();
        String viewDescription = descriptionTextArea.getText();
        if (isNotEqual(modelDescription, viewDescription)) {
            descriptionTextArea.setText(modelDescription);
        }
        teamMembersContainer.getChildren().clear();
        getModel().getMembers().forEach(member -> {
            Node memberNode = generateMemberNode(member);
            teamMembersContainer.getChildren().add(memberNode);
            memberNodeIndex.put(member, memberNode);
        });

        allocatablePeople.addAll(PersistenceManager.getCurrent().getCurrentModel().getUnassignedPeople());
        updatePOSM();

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
        Person modelProductOwner = getModel().getProductOwner();
        Person viewProductOwner = productOwnerPicker.getValue();
        if (isNullOrNotEqual(modelProductOwner, viewProductOwner)) {
            try {
                getModel().setProductOwner(viewProductOwner);
                updatePOSM();
            } catch (CustomException e) {
                //This should never happen as the list of PO's should only contain valid options
                ErrorReporter.get().reportError(e, "Failed to assign the PO. This is very bad.");
            }
        }

        Person modelScrumMaster = getModel().getScrumMaster();
        Person viewScrumMaster = scrumMasterPicker.getValue();
        if (isNullOrNotEqual(modelScrumMaster, viewScrumMaster)) {
            try {
                getModel().setScrumMaster(viewScrumMaster);
                updatePOSM();
            } catch (CustomException e) {
                //This should never happen as the list of SM's should only contain valid options
                ErrorReporter.get().reportError(e, "Failed to assign the SM. This is very bad.");
            }
        }

        Person person = addTeamMemberPicker.getValue();
        if (person != null) {
            try {
                getModel().addMember(person);
                Node memberNode = generateMemberNode(person);
                teamMembersContainer.getChildren().add(memberNode);
                memberNodeIndex.put(person, memberNode);
                Platform.runLater(() -> {
                    addTeamMemberPicker.getSelectionModel().clearSelection();
                    allocatablePeople.remove(person);
                });
                updatePOSM();
            } catch (CustomException e) {
                //This should never happen as the list of people to add should only contain valid options
                ErrorReporter.get().reportError(e, "Failed to add the person to the team. This is bad.");
            }
        }

        String modelShortName = getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNullOrNotEqual(modelShortName, viewShortName)) {
            try {
                getModel().setShortName(viewShortName);
            } catch (CustomException e) {
                addFormError(shortNameTextField, e.getMessage());
            }
        }

        String modelLongName = getModel().getLongName();
        String viewLongName = longNameTextField.getText();
        if (isNullOrNotEqual(modelLongName, viewLongName)) {
            getModel().setLongName(viewLongName);
        }

        String modelDescription = getModel().getDescription();
        String viewDescription = descriptionTextArea.getText();
        if (isNullOrNotEqual(modelDescription, viewDescription)) {
            getModel().setDescription(viewDescription);
        }
    }

    @Override
    public final void dispose() {
        productOwnerPicker.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        scrumMasterPicker.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        addTeamMemberPicker.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        shortNameTextField.focusedProperty().removeListener(getChangeListener());
        longNameTextField.focusedProperty().removeListener(getChangeListener());
        descriptionTextArea.focusedProperty().removeListener(getChangeListener());
        allocatablePeople = null;
        memberNodeIndex = null;
        super.dispose();
    }

    /**
     * Updates the PO and SM.
     */
    private void updatePOSM() {
        updatePO();
        updateSM();
    }

    /**
     * Updates the PO.
     */
    private void updatePO() {
        Person modelProductOwner = getModel().getProductOwner();
        Person modelScrumMaster = getModel().getScrumMaster();

        // Add all the people with the PO skill to the list of POs
        List<Person> productOwners = getModel().getMembers()
                .stream()
                .filter(p -> p.canBeRole(Skill.PO_NAME))
                .collect(Collectors.toList());

        // The ScrumMaster can not be a valid product owner
        productOwners.remove(modelScrumMaster);

        clearPOButton.setDisable(true);
        navigateToPOButton.setDisable(true);

        // Remove listener while editing the product owner picker
        productOwnerPicker.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        productOwnerPicker.getItems().clear();
        productOwnerPicker.getItems().addAll(productOwners);
        if (modelProductOwner != null) {
            productOwnerPicker.getSelectionModel().select(modelProductOwner);
            clearPOButton.setDisable(false);
            if (!isCreationWindow) {
                navigateToPOButton.setDisable(false);
            }
        }
        productOwnerPicker.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
    }

    /**
     * Updates the SM.
     */
    private void updateSM() {
        Person modelProductOwner = getModel().getProductOwner();
        Person modelScrumMaster = getModel().getScrumMaster();

        //Add all the people with the scrum master skill
        // to the list of scrum masters
        List<Person> scrumMasters = getModel().getMembers()
                .stream()
                .filter(p -> p.canBeRole(Skill.SM_NAME))
                .collect(Collectors.toList());

        // The ProductOwner cannot be a valid scrum master
        scrumMasters.remove(modelProductOwner);

        clearSMButton.setDisable(true);
        navigateToSMButton.setDisable(true);

        // Remove listener while editing the scrum master picker
        scrumMasterPicker.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        scrumMasterPicker.getItems().clear();
        scrumMasterPicker.getItems().addAll(scrumMasters);
        scrumMasterPicker.getSelectionModel().clearSelection();
        if (modelScrumMaster != null) {
            scrumMasterPicker.getSelectionModel().select(modelScrumMaster);
            clearSMButton.setDisable(false);
            if (!isCreationWindow) {
                navigateToSMButton.setDisable(false);
            }
        }
        scrumMasterPicker.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
    }

    /**
     * Clears the currently selected Product Owner.
     * @param event The event
     */
    @FXML
    private void clearPO(final ActionEvent event) {
        try {
            getModel().setProductOwner(null);
        } catch (MultipleRolesException e) {
            ErrorReporter.get().reportError(e, "Failed to clear the PO. This shouldn't happen. It's definitely Jay's "
                    + "fault.");
        }
        updatePOSM();
    }

    /**
     * Clears the currently selected Scrum Master.
     * @param event The event
     */
    @FXML
    private void clearSM(final ActionEvent event) {
        try {
            getModel().setScrumMaster(null);
        } catch (MultipleRolesException e) {
            ErrorReporter.get().reportError(e, "Failed to clear the SM. This is probably James' fault.");
        }
        updatePOSM();
    }

    /**
     * Generates a node for a team member.
     * @param person The team member
     * @return the node representing the team member
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private Node generateMemberNode(final Person person) {
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
                popup.setTitleText("Remove Team Member");
                String message = "Are you sure you wish to remove " + person.getShortName() + " from this team?";
                if (person.equals(getModel().getScrumMaster())) {
                    message += "\nThey are currently the teams Scrum Master.";
                }
                if (person.equals(getModel().getProductOwner())) {
                    message += "\nThey are currently the teams Product Owner.";
                }
                popup.setMessageText(message);
                popup.addYesNoButtons(() -> {
                    allocatablePeople.add(person);
                    Node memberNode = memberNodeIndex.get(person);
                    teamMembersContainer.getChildren().remove(memberNode);
                    memberNodeIndex.remove(person);
                    getModel().removeMember(person);
                    updatePOSM();
                    popup.close();
                }, "danger-will-robinson", "everything-is-fine");
                popup.show();
            }
            else {
                allocatablePeople.add(person);
                Node memberNode = memberNodeIndex.get(person);
                teamMembersContainer.getChildren().remove(memberNode);
                memberNodeIndex.remove(person);
                getModel().removeMember(person);
                updatePOSM();
            }
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
            Text nameText = new Text(person.toString());
            pane.add(nameText, 0, 0);
        }
        else {
            Hyperlink nameLink = new Hyperlink(person.toString());
            nameLink.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                if (e.isControlDown()) {
                    getNavigationManager().navigateToNewTab(person);
                } else {
                    getNavigationManager().navigateTo(person);
                }
            });
            pane.add(nameLink, 0, 0);
        }
        pane.add(removeButton, 1, 0);
        FadeButtonOnHover fadeButtonOnHover = new FadeButtonOnHover(removeButton, pane);
        fadeButtonOnHover.setupEffect();
        GridPane.setMargin(removeButton, new Insets(1, 1, 1, 0));

        return pane;
    }
}
