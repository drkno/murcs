package sws.project.acceptance.stepdefs;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import javafx.stage.Stage;
import org.junit.Assert;
import org.loadui.testfx.GuiTest;
import org.testfx.framework.junit.ApplicationTest;

public class QuitAppStepDefs extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {

    }

    @When("^I click the file button in the menu$")
    public void i_click_the_file_button_in_the_menu() throws Throwable {
        moveTo("#fileMenu").clickOn("#fileMenu");
    }

    @When("^Click the quit option$")
    public void click_the_quit_option() throws Throwable {
        moveTo("#fileQuit").clickOn("#fileQuit");
    }

    @Then("^The app closes$")
    public void the_app_closes() throws Throwable {
        boolean exists = GuiTest.getWindows().stream().filter(w -> ((Stage)w).getTitle().equals("project")).findAny().isPresent();
        Assert.assertFalse(exists);
    }

    @Given("^I am at the main app window$")
    public void I_am_at_the_main_app_window() throws Throwable {
        String[] args = {};
        launch(sws.project.view.App.class, args);
    }
}
