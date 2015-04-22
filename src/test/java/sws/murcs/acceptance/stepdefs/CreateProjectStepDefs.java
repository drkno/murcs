package sws.murcs.acceptance.stepdefs;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import javafx.stage.Stage;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import sws.murcs.view.App;

/**
 * Tests the ability to use CreateProjectPopUp to create a new project
 */
public class CreateProjectStepDefs extends ApplicationTest {

    private FxRobot fx;
    private Stage primaryStage;

    @Before
    public void setUp() throws Exception {
        primaryStage = FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        fx = new FxRobot();
        launch(App.class);
    }

    @After
    public void tearDown() throws Exception {
        FxToolkit.cleanupStages();
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
