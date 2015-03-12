package sws.project.magic;

import com.sun.istack.internal.NotNull;
import sws.project.magic.fxml.FxmlPanelGenerator;

/**
 *
 */
public class FooBar {
    @Editable(FxmlPanelGenerator.class)
    private String foo;

    @Editable(FxmlPanelGenerator.class)
    private String bar;

    //@Editable(value = SimpleBooleanPaneGenerator.class, getterName = "isTestBoolean")
    private boolean testBoolean;

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

    public boolean isTestBoolean() {
        return testBoolean;
    }

    public void setTestBoolean(boolean testBoolean) {
        this.testBoolean = testBoolean;
    }
}
