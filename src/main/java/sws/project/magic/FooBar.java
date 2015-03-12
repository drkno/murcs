package sws.project.magic;

import com.sun.istack.internal.NotNull;

/**
 *
 */
public class FooBar {
    @Editable(SimpleStringPaneGenerator.class)
    private String foo;

    @Editable(SimpleNumberPaneGenerator.class)
    private int bar;

    @Editable(value = SimpleBooleanPaneGenerator.class, getterName = "isTestBoolean")
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
