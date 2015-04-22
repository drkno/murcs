package sws.murcs.acceptance.stepdefs;

import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import javafx.application.Application;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import sws.murcs.controller.EditorHelper;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Project;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.Release;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.view.App;

import java.time.LocalDate;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

/**
 * 22/04/2015
 *
 * @author Dion
 */
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
        UndoRedoManager.setDisabled(true);
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
        release.setReleaseDate(LocalDate.of(2015,4,22));
        release.setDescription("There is no spoon");

        model.addProject(project);
        model.addRelease(release);
    }

    @After
    public void tearDown() throws Exception {
        FxToolkit.cleanupStages();
        FxToolkit.cleanupApplication(app);
    }



    @Given("^I have selected the release view from the display list type$")
    public void I_have_selected_the_release_view_from_the_display_list_type() throws Throwable {
        fx.clickOn("#displayChoiceBox").clickOn("Release");
    }

    @And("^a release is selected from the list$")
    public void a_release_is_selected_from_the_list() throws Throwable {
        ListView displayList = (ListView) primaryStage.getScene().lookup("#displayList");
        displayList.getSelectionModel().select(0);
    }

    @When("^I click on the remove button$")
    public void I_click_on_the_remove_button() throws Throwable {
        fx.clickOn("#removeButton");
        fx.clickOn("#Yes");
    }

    @Then("^the release is removed from the list$")
    public void the_release_is_removed_from_the_list() throws Throwable {
        ListView displayList = (ListView) primaryStage.getScene().lookup("#displayList");
        assertEquals(0, displayList.getItems().size());
    }

    @Given("^I am editing a release$")
    public void I_am_editing_a_release() throws Throwable {
        Stage releaseStage = EditorHelper.createNew(Release.class, null);
        FxToolkit.registerStage((Supplier<Stage>) releaseStage);



        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @And("^the Short Name field is blank$")
    public void the_Short_Name_field_is_blank() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @When("^I click OK$")
    public void I_click_OK() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @Then("^an error is displayed$")
    public void an_error_is_displayed() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @And("^the Short Name is specified$")
    public void the_Short_Name_is_specified() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @And("^the Short Name is not unique$")
    public void the_Short_Name_is_not_unique() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @Given("^I am creating a new release$")
    public void I_am_creating_a_new_release() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @And("^a Short Name is specified$")
    public void a_Short_Name_is_specified() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @And("^the specified name is unique$")
    public void the_specified_name_is_unique() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @Then("^the popup goes away$")
    public void the_popup_goes_away() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @And("^the release is added to the list$")
    public void the_release_is_added_to_the_list() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @Given("^The Create Project Popup is shown$")
    public void The_Create_Project_Popup_is_shown() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @When("^I click Cancel$")
    public void I_click_Cancel() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @Given("^I have added all info apart from project$")
    public void I_have_added_all_info_apart_from_project() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @Then("^An error will appear$")
    public void An_error_will_appear() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @Given("^I add all required information$")
    public void I_add_all_required_information() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @Then("^The popup goes away$")
    public void The_popup_goes_away() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @And("^The release is added to the list$")
    public void The_release_is_added_to_the_list() throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }
}
