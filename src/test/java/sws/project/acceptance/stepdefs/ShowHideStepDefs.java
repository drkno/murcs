package sws.project.acceptance.stepdefs;

import cucumber.api.PendingException;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import javafx.stage.Stage;
import org.loadui.testfx.GuiTest;
import org.testfx.framework.junit.ApplicationTest;
import org.junit.Assert;

public class ShowHideStepDefs extends ApplicationTest {

    @Before
    public void setup() throws Exception {
        String[] args = {};
        launch(sws.project.view.App.class, args);
    }

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

    @When("^I click the 'X' button$")
    public void i_click_the_X_button() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}
