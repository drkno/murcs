package sws.murcs.controller.editor;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import sws.murcs.controller.AppController;
import sws.murcs.controller.GenericPopup;
import sws.murcs.controller.NavigationManager;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Person;
import sws.murcs.model.Skill;
import sws.murcs.model.Team;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.view.App;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The controller for the team editor.
 */
public class TeamEditor extends GenericEditor<Team> {

    /**
     * The member of the team.
     */
    @FXML
    private VBox teamMembersContainer;
    /**
     * The shortName, longName and description fields for a Team.
     */
    @FXML
    private TextField shortNameTextField, longNameTextField, descriptionTextField;
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
     * The label for showing error messages.
     */
    @FXML
    private Label labelErrorMessage;
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
        descriptionTextField.focusedProperty().addListener(getChangeListener());
        addTeamMemberPicker.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        productOwnerPicker.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        scrumMasterPicker.getSelectionModel().selectedItemProperty().addListener(getChangeListener());

        allocatablePeople = FXCollections.observableArrayList();
        addTeamMemberPicker.setItems((ObservableList<Person>) allocatablePeople);
        memberNodeIndex = new HashMap<>();

        allocatablePeople.addAll(PersistenceManager.Current.getCurrentModel().getUnassignedPeople());

        setErrorCallback(message -> {
            if (message != null && message.getClass() == String.class) {
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

        String modelDescription = getModel().getDescription();
        String viewDescription = descriptionTextField.getText();
        if (isNotEqual(modelDescription, viewDescription)) {
            descriptionTextField.setText(modelDescription);
        }
        teamMembersContainer.getChildren().clear();
        getModel().getMembers().forEach(member -> {
            Node memberNode = generateMemberNode(member);
            teamMembersContainer.getChildren().add(memberNode);
            memberNodeIndex.put(member, memberNode);
        });

        updatePOSM();
    }

    @Override
    protected final void saveChangesWithException() throws Exception {
        Person modelProductOwner = getModel().getProductOwner();
        Person viewProductOwner = productOwnerPicker.getValue();
        if (isNullOrNotEqual(modelProductOwner, viewProductOwner)) {
            getModel().setProductOwner(viewProductOwner);
            updatePOSM();
        }

        Person modelScrumMaster = getModel().getScrumMaster();
        Person viewScrumMaster = scrumMasterPicker.getValue();
        if (isNullOrNotEqual(modelScrumMaster, viewScrumMaster)) {
            getModel().setScrumMaster(viewScrumMaster);
            updatePOSM();
        }

        Person person = addTeamMemberPicker.getValue();
        if (person != null) {
            getModel().addMember(person);
            Node memberNode = generateMemberNode(person);
            teamMembersContainer.getChildren().add(memberNode);
            memberNodeIndex.put(person, memberNode);
            Platform.runLater(() -> {
                addTeamMemberPicker.getSelectionModel().clearSelection();
                allocatablePeople.remove(person);
            });
            updatePOSM();
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

        String modelDescription = getModel().getDescription();
        String viewDescription = descriptionTextField.getText();
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
        descriptionTextField.focusedProperty().removeListener(getChangeListener());
        allocatablePeople = null;
        memberNodeIndex = null;
        setChangeListener(null);
        UndoRedoManager.removeChangeListener(this);
        setModel(null);
        setErrorCallback(null);
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

        // Remove listener while editing the product owner picker
        productOwnerPicker.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        productOwnerPicker.getItems().clear();
        productOwnerPicker.getItems().addAll(productOwners);
        if (modelProductOwner != null) {
            productOwnerPicker.getSelectionModel().select(modelProductOwner);
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

        // Remove listener while editing the scrum master picker
        scrumMasterPicker.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        scrumMasterPicker.getItems().clear();
        scrumMasterPicker.getItems().addAll(scrumMasters);
        scrumMasterPicker.getSelectionModel().clearSelection();
        if (modelScrumMaster != null) {
            scrumMasterPicker.getSelectionModel().select(modelScrumMaster);
        }
        scrumMasterPicker.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
    }

    /**
     * Generates a node for a team member.
     * @param person The team member
     * @return the node representing the team member
     */
    private Node generateMemberNode(final Person person) {
        Hyperlink nameText = new Hyperlink(person.toString());
        nameText.setOnAction(param -> NavigationManager.navigateTo(person));
        Button removeButton = new Button("X");
        removeButton.setOnAction(event -> {
            GenericPopup popup = new GenericPopup();
            popup.setTitleText("Remove Team Member");
            String message = "Are you sure you wish to remove " + person.getShortName() + " from this team?";
            if (getModel().getScrumMaster() != null && getModel().getScrumMaster().equals(person)) {
                message += "\nThey are currently the teams Scrum Master.";
            }
            if (getModel().getProductOwner() != null && getModel().getProductOwner().equals(person)) {
                message += "\nThey are currently the teams Product Owner.";
            }
            popup.setMessageText(message);
            popup.addOkCancelButtons(f -> {
                allocatablePeople.add(person);
                Node memberNode = memberNodeIndex.get(person);
                teamMembersContainer.getChildren().remove(memberNode);
                memberNodeIndex.remove(person);
                getModel().removeMember(person);
                updatePOSM();
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
}
