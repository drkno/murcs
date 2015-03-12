package sws.project.magic;

import sws.project.magic.fxml.FxmlPaneGenerator;

/**
 * Provides a short example of how to use the FXMLPaneGenerator
 *
 */
public class Example {
    //To edit a string: Set the generator and provide a path to the FXML
    @Editable(editPaneGenerator = FxmlPaneGenerator.class, argument = "/sws/project/String.fxml")
    private String foo;

    //Number.fxml will work for floats, integers and doubles. You can specify a friendly name for the fields
    @Editable(editPaneGenerator = FxmlPaneGenerator.class, friendlyName = "A field named 'Bar'", argument = "/sws/project/Number.fxml")
    private int bar;

    //If a field has a non generic getter/setter you can specify it explicitly
    @Editable(editPaneGenerator = FxmlPaneGenerator.class, getterName = "aNonFriendlyGetterNameForTestBoolean", argument = "/sws/project/Boolean.fxml")
    private boolean testBoolean;

    /**
     * Gets foo
     * @return foo
     */
    public String getFoo() {
        return foo;
    }

    /**
     * Sets foo
     * @param foo the new value of foo
     */
    public void setFoo(String foo) {
        this.foo = foo;
    }

    /**
     * Gets bar
     * @return the value of bar
     */
    public int getBar() {
        return bar;
    }

    /**
     * Sets bar
     * @param bar the new value of bar
     */
    public void setBar(int bar) {
        this.bar = bar;
    }

    /**
     * A getter for 'testBoolean' demonstrating the use of non friendly getter name
     * @return
     */
    public boolean aNonFriendlyGetterNameForTestBoolean() {
        return testBoolean;
    }

    /**
     * A setter for the testBoolean
     * @param testBoolean the new value for testBoolean
     */
    public void setTestBoolean(boolean testBoolean) {
        this.testBoolean = testBoolean;
    }
}
