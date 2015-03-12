package sws.project.magic;

import com.sun.istack.internal.NotNull;
import sws.project.magic.fxml.FxmlPanelGenerator;

/**
 *
 */
public class FooBar {
    @Editable(value = FxmlPanelGenerator.class, argument = "/sws/project/String.fxml")
    private String foo;

    //@Editable(FxmlPanelGenerator.class)
    private int bar;

    @Editable(value = FxmlPanelGenerator.class, getterName = "isTestBoolean", argument = "/sws/project/Boolean.fxml")
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
