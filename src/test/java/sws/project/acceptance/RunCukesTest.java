package sws.project.acceptance;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/sws/project")
public class RunCukesTest {
    public RunCukesTest()
    {
        System.err.println("test");
    }
}
