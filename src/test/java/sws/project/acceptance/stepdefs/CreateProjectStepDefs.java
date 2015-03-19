package sws.project.acceptance.stepdefs;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import javafx.stage.Stage;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

/**
 * Tests the ability to use CreateProjectPopUp to create a new project
 *
 * Created by Haydon on 17/03/2015.
 */
public class CreateProjectStepDefs extends ApplicationTest {

    private FxRobot fx;
    private Stage primaryStage;

    @Before
    public void setUp() throws Exception {
        primaryStage = FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(sws.project.view.App.class);
        fx = new FxRobot();
        launch(sws.project.view.App.class);
    }

    @After
    public void tearDown() throws Exception {
        FxToolkit.cleanupStages();
    }

    @When("^I click the File menu$")
    public void When_I_click_the_File_menu() throws Throwable {
        fx.moveTo("#fileMenu").clickOn("#fileMenu");
    }

    @And("^I click the New selection$")
    public void And_I_click_the_New_selection() throws Throwable {
        fx.moveTo("#newMenu").clickOn("#newMenu");
    }

    @And("^I click the Project selection$")
    public void And_I_click_the_Project_selection() throws Throwable {
        fx.moveTo("#projectOption").clickOn("#projectOption");
    }

    @Then("^the popup shows$")
    public void Then_the_popup_shows() throws Throwable {

    }

    @Override
    public void start(Stage stage) throws Exception {

    }
}