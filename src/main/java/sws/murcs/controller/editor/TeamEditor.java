package sws.murcs.controller.editor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import sws.murcs.model.Team;
import sws.murcs.model.persistence.PersistenceManager;

import java.util.HashMap;
import java.util.List;
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
     * The productOwner, scrumMaster and member pickers.
     */
    @FXML
    private ChoiceBox<Person> productOwnerPicker, scrumMasterPicker, addTeamMemberPicker;
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
    private HashMap<Person, Node> memberNodeIndex;

    @FXML
    @Override
    public final void initialize() {
        this.setChangeListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                saveChanges();
            }
        });

        shortNameTextField.focusedProperty().addListener(this.getChangeListener());
        longNameTextField.focusedProperty().addListener(this.getChangeListener());
        descriptionTextField.focusedProperty().addListener(this.getChangeListener());
        addTeamMemberPicker.getSelectionModel().selectedItemProperty().addListener(this.getChangeListener());
        productOwnerPicker.getSelectionModel().selectedItemProperty().addListener(this.getChangeListener());
        scrumMasterPicker.getSelectionModel().selectedItemProperty().addListener(this.getChangeListener());

        setErrorCallback(message -> {
            if (message.getClass() == String.class) {
                labelErrorMessage.setText(message);
            }
        });

        allocatablePeople = FXCollections.observableArrayList();
        addTeamMemberPicker.setItems((ObservableList<Person>) allocatablePeople);
        memberNodeIndex = new HashMap<>();
    }

    @Override
    public final void loadObject() {
        String modelShortName = this.getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNotEqual(modelShortName, viewShortName)) {
            shortNameTextField.setText(modelShortName);
        }

        String modelLongName = this.getModel().getLongName();
        String viewLongName = longNameTextField.getText();
        if (isNotEqual(modelLongName, viewLongName)) {
            longNameTextField.setText(modelLongName);
        }

        String modelDescription = this.getModel().getDescription();
        String viewDescription = descriptionTextField.getText();
        if (isNotEqual(modelDescription, viewDescription)) {
            descriptionTextField.setText(modelDescription);
        }

        allocatablePeople.addAll(PersistenceManager.Current.getCurrentModel().getUnassignedPeople());
        this.getModel().getMembers().forEach(m -> {
            Node member = generateMemberNode(m);
            teamMembersContainer.getChildren().add(member);
            memberNodeIndex.put(m, member);
        });
        updatePOSM();

        //hack set the error text to nothing when first loading the object
        labelErrorMessage.setText(" ");
    }

    @Override
    protected final void saveChangesWithException() throws Exception {
        Person modelProductOwner = this.getModel().getProductOwner();
        Person viewProductOwner = productOwnerPicker.getValue();
        if (isNotEqualOrIsEmpty(modelProductOwner, viewProductOwner)) {
            this.getModel().setProductOwner(viewProductOwner);
            updatePOSM();
        }

        Person modelScrumMaster = this.getModel().getScrumMaster();
        Person viewScrumMaster = scrumMasterPicker.getValue();
        if (isNotEqualOrIsEmpty(modelScrumMaster, viewScrumMaster)) {
            this.getModel().setScrumMaster(viewScrumMaster);
            updatePOSM();
        }

        Person person = addTeamMemberPicker.getValue();
        if (person != null) {
            this.getModel().addMember(person);
            allocatablePeople.remove(person);
            Node member = generateMemberNode(person);
            teamMembersContainer.getChildren().add(member);
            memberNodeIndex.put(person, member);
            addTeamMemberPicker.setValue(null);
        }

        String modelShortName = this.getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNotEqualOrIsEmpty(modelShortName, viewShortName)) {
            this.getModel().setShortName(viewShortName);
        }

        String modelLongName = this.getModel().getLongName();
        String viewLongName = longNameTextField.getText();
        if (isNotEqualOrIsEmpty(modelLongName, viewLongName)) {
            this.getModel().setLongName(viewLongName);
        }

        String modelDescription = this.getModel().getDescription();
        String viewDescription = descriptionTextField.getText();
        if (isNotEqualOrIsEmpty(modelDescription, viewDescription)) {
            this.getModel().setDescription(viewDescription);
        }
    }

    @Override
    public final void dispose() {
        productOwnerPicker.getSelectionModel().selectedItemProperty().removeListener(this.getChangeListener());
        scrumMasterPicker.getSelectionModel().selectedItemProperty().removeListener(this.getChangeListener());
        addTeamMemberPicker.getSelectionModel().selectedItemProperty().removeListener(this.getChangeListener());
        shortNameTextField.focusedProperty().removeListener(this.getChangeListener());
        longNameTextField.focusedProperty().removeListener(this.getChangeListener());
        descriptionTextField.focusedProperty().removeListener(this.getChangeListener());
        memberNodeIndex.clear();
        memberNodeIndex = null;
        allocatablePeople.clear();
        allocatablePeople = null;
        this.setChangeListener(null);
        UndoRedoManager.removeChangeListener(this);
        this.setModel(null);
        this.setErrorCallback(null);
    }

    /**
     * Updates the PO and SM.
     */
    private void updatePOSM() {
        Person productOwner = this.getModel().getProductOwner();
        Person scrumMaster = this.getModel().getScrumMaster();

        // Add all the people with the PO skill to the list of POs
        List<Person> productOwners = this.getModel().getMembers()
                .stream()
                .filter(p -> p.canBeRole(Skill.PO_NAME))
                .collect(Collectors.toList());

        // The ScrumMaster can not be a valid product owner
        productOwners.remove(scrumMaster);

        // Remove listener while editing the product owner picker
        productOwnerPicker.getSelectionModel().selectedItemProperty().removeListener(this.getChangeListener());
        productOwnerPicker.getItems().clear();
        productOwnerPicker.getItems().addAll(productOwners);
        if (productOwner != null) {
            productOwnerPicker.getSelectionModel().select(productOwner);
        }
        productOwnerPicker.getSelectionModel().selectedItemProperty().addListener(this.getChangeListener());

        //Add all the people with the scrum master skill
        // to the list of scrum masters
        List<Person> scrumMasters = this.getModel().getMembers()
                .stream()
                .filter(p -> p.canBeRole(Skill.SM_NAME))
                .collect(Collectors.toList());

        // The ProductOwner cannot be a valid scrum master
        scrumMasters.remove(productOwner);

        // Remove listener while editing the scrum master picker
        scrumMasterPicker.getSelectionModel().selectedItemProperty().removeListener(this.getChangeListener());
        scrumMasterPicker.getItems().clear();
        scrumMasterPicker.getItems().addAll(scrumMasters);
        scrumMasterPicker.getSelectionModel().clearSelection();
        if (scrumMaster != null) {
            scrumMasterPicker.getSelectionModel().select(scrumMaster);
        }
        scrumMasterPicker.getSelectionModel().selectedItemProperty().addListener(this.getChangeListener());
    }

    /**
     * Generates a node for a team member.
     * @param person The team member
     * @return the node representing the team member
     */
    private Node generateMemberNode(final Person person) {
        Text nameText = new Text(person.toString());
        Button removeButton = new Button("X");
        removeButton.setOnAction(event -> {
            GenericPopup popup = new GenericPopup();
            popup.setTitleText("Remove Team Member");
            String message = "Are you sure you wish to remove " + person.getShortName() + " from this team?";
            if (this.getModel().getScrumMaster() != null && this.getModel().getScrumMaster().equals(person)) {
                message += "\nThey are currently the teams Scrum Master.";
            }
            if (this.getModel().getProductOwner() != null && this.getModel().getProductOwner().equals(person)) {
                message += "\nThey are currently the teams Product Owner.";
            }
            popup.setMessageText(message);
            popup.addOkCancelButtons(f -> {
                allocatablePeople.add(person);
                Node member = memberNodeIndex.get(person);
                teamMembersContainer.getChildren().remove(member);
                memberNodeIndex.remove(person);
                this.getModel().removeMember(person);
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
