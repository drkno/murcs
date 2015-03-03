package acceptance.stepdefs;

import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import javafx.embed.swing.JFXPanel;
import org.testfx.api.FxRobot;

/**
 * Created by jayha_000 on 3/2/2015.
 */
public class ButtonClickStepDefs extends FxRobot {
    private String originalText;

    @Given("I click the button")
    public void click_the_button() throws Exception{
        originalText = getButtonText();
        System.out.println(originalText);
    }

    @Then("the text changes")
    public void then_the_text_changed(){
        /*String currentText = getButtonText();

        Button buttonNode = (Button)FXTestUtils.getOrFail("#button");*/

        //TODO Assert.assertNotEqual(currentText, originalText);
    }

    private String getButtonText(){
        new JFXPanel();

        //Button b = (Button) find("#button", getRootNode());
        //return b.getText();
        return null;
    }
}
