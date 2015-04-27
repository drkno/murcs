package sws.murcs.acceptance.stepdefs;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import javafx.application.Application;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Project;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.Release;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.view.App;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class ReleaseMaintenanceStepDefs extends ApplicationTest{

    private FxRobot fx;
    private Stage primaryStage;
    private Project project;
    private RelationalModel model;
    private Application app;
    private Release release;

    @Override
    public void start(Stage stage) throws Exception {}

    @Before
    public void setUp() throws Exception {
        UndoRedoManager.setDisabled(false);
        primaryStage = FxToolkit.registerPrimaryStage();
        app = FxToolkit.setupApplication(App.class);
        fx = new FxRobot();
        launch(App.class);

        model = new RelationalModel();
        PersistenceManager.Current.setCurrentModel(model);

        project = new Project();
        project.setShortName("Testing");

        release = new Release();
        release.setShortName("TestRelease");
        release.setAssociatedProject(project);
        release.setReleaseDate(LocalDate.of(2015, 4, 22));

        model.addProject(project);
    }

    @After
    public void tearDown() throws Exception {
        FxToolkit.cleanupStages();
        FxToolkit.cleanupApplication(app);
    }

    @And("^I have selected the release view from the display list type$")
    public void I_have_selected_the_release_view_from_the_display_list_type() throws Throwable {
        fx.clickOn("#displayChoiceBox").clickOn("Release");
        interact(() -> ((ListView) primaryStage.getScene().lookup("#displayList")).getSelectionModel().select(0));
    }

    @And("^a release is selected from the list$")
    public void a_release_is_selected_from_the_list() throws Throwable {
        ListView displayList = (ListView) primaryStage.getScene().lookup("#displayList");
        interact(() -> displayList.getSelectionModel().select(0));
    }

    @When("^I click on the remove button$")
    public void I_click_on_the_remove_button() throws Throwable {
        fx.clickOn("#removeButton");
        fx.clickOn("Yes");
    }

    @Then("^the release is removed from the list$")
    public void the_release_is_removed_from_the_list() throws Throwable {
        ListView displayList = (ListView) primaryStage.getScene().lookup("#displayList");
        assertEquals(0, displayList.getItems().size());
    }

    @When("^I select new release from the file menu$")
    public void I_select_new_release_from_the_file_menu() throws Throwable {
        fx.clickOn("#fileMenu").clickOn("#newMenu");
        fx.moveBy(100,0);
        fx.clickOn("#addRelease");
    }

    @And("^I fill in valid information in the popup$")
    public void I_fill_in_valid_information_in_the_popup() throws Throwable {
        fx.clickOn("#shortNameTextField").write("Release");
        fx.clickOn("#projectChoiceBox").clickOn("Testing");
    }

    @And("^Click Ok$")
    public void Click_Ok() throws Throwable {
        fx.clickOn("Create");
    }

    @Then("^A release is made with the given information$")
    public void A_release_is_made_with_the_given_information() throws Throwable {
        ListView displayList = (ListView) primaryStage.getScene().lookup("#displayList");
        Release release1 = (Release) displayList.getItems().get(0);
        assertEquals("Release",release1.getShortName());
        assertEquals(project, release1.getAssociatedProject());
    }

    @And("^I click on the add button$")
    public void I_click_on_the_add_button() throws Throwable {
        fx.clickOn("#addButton");
    }

    @Given("^there is a release$")
    public void there_is_a_release() throws Throwable {
        PersistenceManager.Current.getCurrentModel().addRelease(release);
    }

    @When("^I edit the values of the release$")
    public void I_edit_the_values_of_the_release() throws Throwable {
        fx.clickOn("#shortNameTextField").write("Foo");
        fx.press(KeyCode.TAB);
        fx.clickOn("#descriptionTextArea");
        fx.write("This is really important");
        fx.clickOn("#shortNameTextField");
    }

    @Then("^the release updates to the values given$")
    public void the_release_updates_to_the_values_given() throws Throwable {
        ListView displayList = (ListView) primaryStage.getScene().lookup("#displayList");
        Release release1 = (Release) displayList.getItems().get(0);
        assertEquals("TestReleaseFoo", release1.getShortName());
        assertEquals("This is really important", release1.getDescription());
        assertEquals(project, release1.getAssociatedProject());
    }
}
