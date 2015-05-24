package sws.murcs.acceptance.stepdefs;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import javafx.application.Application;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Model;
import sws.murcs.model.Organisation;
import sws.murcs.model.Person;
import sws.murcs.model.Project;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.view.App;

import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

public class ListDisplayStepDefs extends ApplicationTest {

    private FxRobot fx;
    private Stage primaryStage;
    private Person person1;
    private Person person2;
    private Person person3;
    private Project project;
    private Organisation model;
    private Application app;

    @Override
    public void start(Stage stage) throws Exception {}

    @Before("@ListDisplay")
    public void setUp() throws Exception {
        UndoRedoManager.setDisabled(false);
        primaryStage = FxToolkit.registerPrimaryStage();
        app = FxToolkit.setupApplication(App.class);
        fx = new FxRobot();
        launch(App.class);

        interact(() -> {
            try {
                model = new Organisation();
                PersistenceManager.getCurrent().setCurrentModel(model);
                UndoRedoManager.forget(true);
                UndoRedoManager.add(model);

                person1 = new Person();
                person1.setShortName("John");
                person1.setUserId("abc123");
                person2 = new Person();
                person2.setShortName("Dave");
                person2.setUserId("def456");
                person3 = new Person();
                person3.setShortName("Jim");
                person3.setUserId("ghi789");
                project = new Project();
                project.setShortName("Testing");

                model.add(person1);
                model.add(person2);
                model.add(person3);
                model.add(project);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @After("@ListDisplay")
    public void tearDown() throws Exception {
        PersistenceManager.getCurrent().setCurrentModel(null);
        UndoRedoManager.forgetListeners();
        UndoRedoManager.setDisabled(true);
        FxToolkit.cleanupStages();
        FxToolkit.cleanupApplication(app);
    }

    @When("^I change the list display type$")
    public void I_change_the_list_display_type() throws Throwable {
        fx.clickOn("#displayChoiceBox").clickOn("Project");
        fx.clickOn("#displayChoiceBox").clickOn("Person");
    }

    @Then("^the list items repopulate with that type$")
    public void the_list_items_repopulate_with_that_type() throws Throwable {
        ListView displayList = (ListView) primaryStage.getScene().lookup("#displayList");
        Object object = displayList.getItems().get(0);
        assertTrue(object instanceof Person);
    }

    @And("^the first item is selected$")
    public void the_first_item_is_selected() throws Throwable {
        ListView displayList = (ListView) primaryStage.getScene().lookup("#displayList");
        assertTrue(displayList.getSelectionModel().selectedIndexProperty().intValue() == 0);
    }

    @When("^I select an item from the list display$")
    public void I_select_an_item_from_the_list_display() throws Throwable {
        fx.clickOn("#displayChoiceBox").clickOn("Person");
        fx.clickOn("Dave");
    }

    @Then("^details of the select item are shown$")
    public void details_of_the_select_item_are_shown() throws Throwable {
        GridPane contentPane = (GridPane) primaryStage.getScene().lookup("#contentPane");
        TextField shortName = (TextField) contentPane.lookup("#shortNameTextField");
        assertNotNull(shortName);
    }

    @Given("^I have selected an item from the list display$")
    public void I_have_selected_an_item_from_the_list_display() throws Throwable {
        fx.clickOn("#displayChoiceBox").clickOn("Person");
        fx.clickOn("Dave");
    }

    @When("^I (\\w+) the items short name$")
    public void I_edit_the_items_short_name(String changeType) throws Throwable {
        FxRobot robot = fx.clickOn("#shortNameTextField");
        if (changeType.equals("prefix")) {
            robot.type(KeyCode.HOME);
        }
        robot.type(KeyCode.Z);
        fx.clickOn("#longNameTextField");
    }

    @Then("^the short name is updated in the list display$")
    public void the_short_name_is_updated_in_the_list_display() throws Throwable {
        ListView displayList = (ListView) primaryStage.getScene().lookup("#displayList");
        String name = displayList.getSelectionModel().getSelectedItem().toString();
        assertTrue(Objects.equals("Davez", name) || Objects.equals("zDave", name));
    }

    @Given("^there are multiple items in the list display$")
    public void there_are_multiple_items_in_the_list_display() throws Throwable {
        I_change_the_list_display_type();
    }

    @Then("^the list is sorted alphabetically$")
    public void the_list_is_sorted_alphabetically() throws Throwable {
        ListView displayList = (ListView) primaryStage.getScene().lookup("#displayList");
        List<Model> observableList = displayList.getItems();
        Model previous = null;
        for (final Model current : observableList) {
            if (previous != null && current.getShortName().compareToIgnoreCase(previous.getShortName()) <= 0)
                fail("the display list was not sorted alphabetically");
            previous = current;
        }
    }
}
