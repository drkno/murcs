package tests.acceptance.stepdefs;

import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import dontclick.MainWindow;
import javafx.stage.Stage;

/**
 * Created by jayha_000 on 3/2/2015.
 */
public class ButtonClickStepDefs extends FxRobotTestBase  {
    private String originalText;

    @Given("I click the button")
    public void click_the_button() throws Exception{
        MainWindow window = new MainWindow();
        window.start(new Stage());

        originalText = getButtonText();
    }

    @Then("the text changes")
    public void then_the_text_changed(){
        String currentText = getButtonText();

        //TODO Assert.assertNotEqual(currentText, originalText);
    }

    private String getButtonText(){
        return "";
    }
}
