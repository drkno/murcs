package sws.project.magic;

import sws.project.magic.fxml.FxmlPanelGenerator;

/**
 *
 */
public class FooBar {
    @Editable(editPaneGenerator = FxmlPanelGenerator.class, argument = "/sws/project/String.fxml")
    private String foo;

    @Editable(editPaneGenerator = FxmlPanelGenerator.class, argument = "/sws/project/Number.fxml")
    private int bar;

    @Editable(editPaneGenerator = FxmlPanelGenerator.class, friendlyName = "magic test boolean", getterName = "isTestBoolean", argument = "/sws/project/Boolean.fxml")
    private boolean testBoolean;

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public int getBar() {
        return bar;
    }

    public void setBar(int bar) {
        this.bar = bar;
    }

    public boolean isTestBoolean() {
        return testBoolean;
    }

    public void setTestBoolean(boolean testBoolean) {
        this.testBoolean = testBoolean;
    }
}
