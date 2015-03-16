package sws.project.acceptance;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(format ={"pretty", "json:target/cucumber.json"},
        features = {"src/test/resources/sws/project"},
        tags = {"~@Manual", "~WIP"})
public class RunCukesTest {
}
